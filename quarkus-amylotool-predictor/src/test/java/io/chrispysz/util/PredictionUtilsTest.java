package io.chrispysz.util;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@QuarkusTest
public class PredictionUtilsTest {

    private static final int SEQ_CUTOFF = 40;

    @Inject
    PredictionUtils utils;

    @Test
    public void shouldTokenizeSequence() {
        List<String> sequences = List.of("ACGT");
        int seqLen = 8;
        int[][] expected = {
                {23, 0, 1, 5, 16, 24, 25, 25}
        };
        int[][] actual = utils.tokenizeSequences(sequences, seqLen);
        Assertions.assertArrayEquals(expected, actual, "Tokenized sequence should be {23, 0, 1, 5, 16, 24, 25, 25}");
    }

    @Test
    public void shouldReturnIndexWithMaxAverage() {
        Map<String, Map<Integer, Float>> indexScoresForModel = new HashMap<>();
        indexScoresForModel.put("Model1", Map.of(0, 10f, 1, 20f, 2, 5f));
        indexScoresForModel.put("Model2", Map.of(0, 30f, 1, 40f, 2, 6f));
        indexScoresForModel.put("Model3", Map.of(0, 20f, 1, 30f, 2, 4f));

        Map<Integer, Float> result = utils.findIndexWithMaxAverage(indexScoresForModel);

        Assertions.assertEquals(1, result.size(), "Result should contain only one entry.");
        Assertions.assertTrue(result.containsKey(1), "Index 2 should have the max average.");
        Assertions.assertEquals(30f, result.get(1), "The max average should be 30.");
    }

    @Test
    public void shouldReturnEmptyMapForEmptyInput() {
        Map<String, Map<Integer, Float>> indexScoresForModel = new HashMap<>();

        Map<Integer, Float> result = utils.findIndexWithMaxAverage(indexScoresForModel);

        Assertions.assertTrue(result.isEmpty(), "Result should be empty for empty input.");
    }

    @Test
    public void shouldGenerateSubsequencesFromLongerSequence() {
        String sequence = "MSRQYDNYGRDQYNIENVDTLNVVNHADRNVLLDDWKPSFEHMQ";

        List<String> subsequences = utils.generateSubsequences(sequence, SEQ_CUTOFF);

        Assertions.assertEquals(5, subsequences.size(), "Should generate correct number of subsequences.");
        Assertions.assertEquals(List.of("MSRQYDNYGRDQYNIENVDTLNVVNHADRNVLLDDWKPSF", "SRQYDNYGRDQYNIENVDTLNVVNHADRNVLLDDWKPSFE",
                        "RQYDNYGRDQYNIENVDTLNVVNHADRNVLLDDWKPSFEH", "QYDNYGRDQYNIENVDTLNVVNHADRNVLLDDWKPSFEHM", "YDNYGRDQYNIENVDTLNVVNHADRNVLLDDWKPSFEHMQ"),
                subsequences, "Subsequences should match expected values.");
    }

    @Test
    public void shouldGenerateSubsequenceFromShorterSequence() {
        String sequence = "MSRQYDNYGRDQYNIENVDTL";

        List<String> subsequences = utils.generateSubsequences(sequence, SEQ_CUTOFF);

        Assertions.assertEquals(1, subsequences.size(), "Should generate correct number of subsequences.");
        Assertions.assertEquals(List.of("MSRQYDNYGRDQYNIENVDTL"),
                subsequences, "Subsequences should match expected values.");
    }

    @Test
    public void shouldGenerateSubsequenceFromExactSequence() {
        String sequence = "MSRQYDNYGRDQYNIENVDTLNVVNHADRNVLLDDWKPSF";

        List<String> subsequences = utils.generateSubsequences(sequence, SEQ_CUTOFF);

        Assertions.assertEquals(1, subsequences.size(), "Should generate correct number of subsequences.");
        Assertions.assertEquals(List.of("MSRQYDNYGRDQYNIENVDTLNVVNHADRNVLLDDWKPSF"),
                subsequences, "Subsequences should match expected values.");
    }

    @Test
    public void shouldGenerateNothingFromEmptySequence() {
        List<String> subsequences = utils.generateSubsequences("", SEQ_CUTOFF);

        Assertions.assertEquals(0, subsequences.size(), "Should generate empty list.");
    }

    @Test
    public void shouldGenerateNothingFromNullSequence() {
        List<String> subsequences = utils.generateSubsequences(null, SEQ_CUTOFF);

        Assertions.assertEquals(0, subsequences.size(), "Should generate empty list.");
    }

    @Test
    public void testResultToMap() {
        Map<String, Map<Integer, Float>> indexScoresForModel = new HashMap<>();
        Map<Integer, Float> indexScores = new HashMap<>();
        int modelNum = 1;
        float[] modelResults = {0.1f, 0.2f, 0.3f};

        Map<Integer, Float> expectedIndexScores = new HashMap<>();
        for (int i = 0; i < modelResults.length; i++) {
            expectedIndexScores.put(i, modelResults[i]);
        }

        utils.resultToMap(indexScoresForModel, indexScores, modelNum, modelResults);

        Assertions.assertFalse(indexScoresForModel.isEmpty(), "indexScoresForModel should not be empty");
        Assertions.assertTrue(indexScoresForModel.containsKey(String.valueOf(modelNum)), "indexScoresForModel should contain the modelNum as key");
        Assertions.assertEquals(expectedIndexScores, indexScoresForModel.get(String.valueOf(modelNum)), "The indexScores map should match the expected values");
    }
}
