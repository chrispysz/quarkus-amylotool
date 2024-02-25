package io.chrispysz.service;

import io.chrispysz.model.PredictionResult;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

@QuarkusTest
public class PredictionServiceTest {

    private static final Map<String, String> VALID_SEQUENCES = Map.of(">TEST.01", "NDGQANGQGRVYQASGDQHIIEHHHYSPAWAGPDSIPPAFGRAPVSQ", ">TEST.02", "MKGRAFGHGRTYQAGGDLTVHE");

    @Inject
    PredictionService predictionService;

    @Test
    void shouldReturnValidMaxIndexWithValueWhenValidSequenceListProvidedAndNoModel() {

        List<PredictionResult> actualResults = predictionService.processPredictionRequest(VALID_SEQUENCES, null, "123");

        Assertions.assertEquals(2, actualResults.size(), "Result should contain entry for each sequence.");

    }

}
