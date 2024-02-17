package io.chrispysz.service;

import io.chrispysz.entity.PredictionResult;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

@QuarkusTest
public class PredictionServiceTest {

    private static final List<String> VALID_SEQUENCES = List.of("NDGQANGQGRVYQASGDQHIIEHHHYSPAWAGPDSIPPAFGRAPVSQ", "MKGRAFGHGRTYQAGGDLTVHE");

    @Inject
    PredictionService predictionService;

    @Test
    void shouldReturnValidMaxIndexWithValueWhenValidSequenceListProvidedAndNoModel() throws IOException {

        List<PredictionResult> actualResults = predictionService.processPredictionRequest(VALID_SEQUENCES, null);

        Assertions.assertEquals(2, actualResults.size(), "Result should contain entry for each sequence.");

    }

}
