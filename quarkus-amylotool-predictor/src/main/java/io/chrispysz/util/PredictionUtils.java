package io.chrispysz.util;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PredictionUtils {
    private static final Map<Character, Integer> aminoAcidToIndex = new HashMap<>();
    private static final Map<String, Integer> specialTokenToIndex = new HashMap<>();
    private static final int numAminoAcids;
    private static final char[] ALL_AMINO_ACIDS = {'A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y'};
    private static final String[] SPECIAL_TOKENS = {"<OTHER>", "<START>", "<END>", "<PAD>"};

    static {
        for (int i = 0; i < ALL_AMINO_ACIDS.length; i++) {
            aminoAcidToIndex.put(ALL_AMINO_ACIDS[i], i);
        }
        numAminoAcids = ALL_AMINO_ACIDS.length;
        for (int i = 0; i < SPECIAL_TOKENS.length; i++) {
            specialTokenToIndex.put(SPECIAL_TOKENS[i], i + numAminoAcids);
        }
    }

    public int[][] tokenizeSequences(List<String> seqs, int seqLen) {
        int[][] tokenizedSequences = new int[seqs.size()][seqLen];
        int startIndex = specialTokenToIndex.get("<START>");
        int padIndex = specialTokenToIndex.get("<PAD>");
        int endIndex = specialTokenToIndex.get("<END>");

        for (int i = 0; i < seqs.size(); i++) {
            List<Integer> tokens = new ArrayList<>();

            tokens.add(startIndex);

            String seq = seqs.get(i);

            for (int j = 0; j < seq.length(); j++) {
                char aa = seq.charAt(j);
                tokens.add(aminoAcidToIndex.getOrDefault(aa, specialTokenToIndex.get("<OTHER>")));
            }

            tokens.add(endIndex);

            while (tokens.size() < seqLen) {
                tokens.add(padIndex);
            }

            for (int j = 0; j < seqLen; j++) {
                tokenizedSequences[i][j] = tokens.get(j);
            }
        }
        return tokenizedSequences;
    }

    public List<String> generateSubsequences(String sequence, int seqLen) {
        List<String> subsequences = new ArrayList<>();

        if (sequence == null || sequence.isEmpty()) {
            return subsequences;
        } else if (sequence.length() < seqLen) {
            subsequences.add(sequence);
        } else {
            for (int i = 0; i <= sequence.length() - seqLen; i++) {
                String subsequence = sequence.substring(i, i + seqLen);
                subsequences.add(subsequence);
            }
        }

        return subsequences;
    }

    public Map<Integer, Float> findIndexWithMaxAverage(Map<String, Map<Integer, Float>> indexScoresForModel) {
        Map<Integer, Float> result = new HashMap<>();

        if (indexScoresForModel.isEmpty()) {
            return result;
        }

        Map<Integer, Float> sumScores = new HashMap<>();
        Map<Integer, Integer> countScores = new HashMap<>();

        // Accumulate sums and counts for each index
        for (Map<Integer, Float> scores : indexScoresForModel.values()) {
            for (Map.Entry<Integer, Float> entry : scores.entrySet()) {
                int index = entry.getKey();
                float score = entry.getValue();
                sumScores.put(index, sumScores.getOrDefault(index, 0f) + score);
                countScores.put(index, countScores.getOrDefault(index, 0) + 1);
            }
        }

        // Calculate average for each index and find the index with the max average
        float maxAverage = -Float.MAX_VALUE;
        int indexWithMaxAverage = -1;
        for (Map.Entry<Integer, Float> entry : sumScores.entrySet()) {
            int index = entry.getKey();
            float average = entry.getValue() / countScores.get(index);
            if (average > maxAverage) {
                maxAverage = average;
                indexWithMaxAverage = index;
            }
        }
        result.put(indexWithMaxAverage, maxAverage);

        return result;
    }

}
