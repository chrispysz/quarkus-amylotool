package io.chrispysz.service;

import io.chrispysz.entity.PredictionResult;
import io.chrispysz.util.PredictionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.IntNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;
import org.tensorflow.types.TInt32;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PredictionService {

    private final static int SEQ_LEN = 42;
    private final static int ZEROS_DIM = 8943;
    private final static int NUM_MODELS = 6;
    @ConfigProperty(name = "predictions.models.path")
    String MODELS_PATH;

    private final Logger logger;
    private final PredictionUtils utils;
    private final ClassLoader classLoader;

    public PredictionService(Logger logger, PredictionUtils utils) {
        this.logger = logger;
        this.utils = utils;
        this.classLoader = getClass().getClassLoader();
    }

    public List<PredictionResult> processPredictionRequest(List<String> sequences, String model) {
        Map<String, Map<Integer, Float>> indexScoresForModel = new HashMap<>();
        List<PredictionResult> results = new ArrayList<>();

        for (String sequence : sequences) {
            List<String> subsequences = utils.generateSubsequences(sequence, SEQ_LEN - 2);

            if (model == null || model.isEmpty()) {
                for (int modelNum = 1; modelNum <= NUM_MODELS; modelNum++) {
                    runPredictionsForModel(modelNum, subsequences, indexScoresForModel);
                }
            } else {
                runPredictionsForModel(Integer.parseInt(model), subsequences, indexScoresForModel);
            }

            Map<Integer, Float> indexWithMaxAverage = utils.findIndexWithMaxAverage(indexScoresForModel);

            if (!indexWithMaxAverage.isEmpty()) {
                Map.Entry<Integer, Float> entry = indexWithMaxAverage.entrySet().iterator().next();

                if (model == null) {
                    results.add(new PredictionResult(sequence, entry.getKey(), entry.getValue(), null));
                } else {
                    results.add(new PredictionResult(sequence, entry.getKey(), entry.getValue(), Integer.parseInt(model)));
                }
            }
        }
        return results;
    }

    private void runPredictionsForModel(int modelNum, List<String> subsequences, Map<String, Map<Integer, Float>> indexScoresForModel) {
        Map<Integer, Float> indexScores = new HashMap<>();

        String modelPath = MODELS_PATH + modelNum;
        float[] modelResults = predictWithModel(modelPath, subsequences);

        if (modelResults != null) {

            for (int i = 0; i < modelResults.length; i++) {
                indexScores.put(i, modelResults[i]);
            }

            indexScoresForModel.put(String.valueOf(modelNum), indexScores);
        }
    }


    private float[] predictWithModel(String modelPath, List<String> sequences) {
        String loadPath = MODELS_PATH.contains("deployments") ? modelPath : classLoader.getResource(modelPath).getPath();

        try (SavedModelBundle model = SavedModelBundle.load(loadPath, "serve")) {
            Session session = model.session();

            int[][] tokenizedSequences = utils.tokenizeSequences(sequences, SEQ_LEN);
            float[][] zerosArray = new float[tokenizedSequences.length][ZEROS_DIM];

            IntNdArray seqNdArray = NdArrays.ofInts(Shape.of(tokenizedSequences.length, SEQ_LEN));
            FloatNdArray zerosNdArray = NdArrays.ofFloats(Shape.of(tokenizedSequences.length, ZEROS_DIM));

            for (int i = 0; i < tokenizedSequences.length; i++) {
                seqNdArray.set(NdArrays.vectorOf(tokenizedSequences[i]), i);
                zerosNdArray.set(NdArrays.vectorOf(zerosArray[i]), i);
            }

            // Execute the model
            try (TFloat32 resultTensor = (TFloat32) session.runner()
                    .feed("serving_default_input-seq", TInt32.tensorOf(seqNdArray))
                    .feed("serving_default_input-annotations", TFloat32.tensorOf(zerosNdArray))
                    .fetch("StatefulPartitionedCall")
                    .run().get(0)) {

                long[] shape = resultTensor.shape().asArray();

                float[] results = new float[(int) shape[0]];

                for (int i = 0; i < shape[0]; i++) {
                    results[i] = resultTensor.getFloat(i, 0);
                }


                return results;
            } catch (Exception e) {
                logger.error("Error during model execution", e);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error loading model from path: " + loadPath, e);
            return null;
        }
    }


}
