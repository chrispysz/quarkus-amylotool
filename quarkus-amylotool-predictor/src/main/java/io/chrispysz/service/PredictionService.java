package io.chrispysz.service;

import io.chrispysz.model.PredictionResult;
import io.chrispysz.util.PredictionUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;
import org.tensorflow.Result;
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

    @Channel("prediction-results")
    Emitter<PredictionResult> predictionResultEmitter;

    @Inject
    Logger log;
    @Inject
    PredictionUtils utils;
    ClassLoader classLoader = getClass().getClassLoader();


    public List<PredictionResult> processPredictionRequest(Map<String, String> sequences, String model, String taskId) {
        List<PredictionResult> results = new ArrayList<>();

        Map<String, Map<Integer, Float>> indexScoresForModel = new HashMap<>();
        int sequencesSize = sequences.size();
        int currentSequenceIndex = 0;


        for (Map.Entry<String, String> sequence : sequences.entrySet()) {
            currentSequenceIndex++;
            List<String> subsequences = utils.generateSubsequences(sequence.getValue(), SEQ_LEN - 2);

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

                PredictionResult res;
                if (model == null) {
                    res = new PredictionResult(taskId, sequence.getKey(), entry.getKey(), entry.getValue(), null);
                } else {
                    res = new PredictionResult(taskId, sequence.getKey(), entry.getKey(), entry.getValue(), Integer.parseInt(model));
                }

                if (currentSequenceIndex == sequencesSize) {
                    res.isLast = true;
                }

                results.add(res);
                predictionResultEmitter.send(res);
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
            try (Result result = session.runner()
                    .feed("serving_default_input-seq", TInt32.tensorOf(seqNdArray))
                    .feed("serving_default_input-annotations", TFloat32.tensorOf(zerosNdArray))
                    .fetch("StatefulPartitionedCall")
                    .run()) {

                TFloat32 resultTensor = (TFloat32) result.get(0);

                long[] shape = resultTensor.shape().asArray();

                float[] results = new float[(int) shape[0]];

                for (int i = 0; i < shape[0]; i++) {
                    results[i] = resultTensor.getFloat(i, 0);
                }


                return results;
            } catch (Exception e) {
                log.error("Error during model execution", e);
                return null;
            }
        } catch (Exception e) {
            log.error("Error loading model from path: " + loadPath, e);
            return null;
        }
    }


}
