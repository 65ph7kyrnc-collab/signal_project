package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Unit tests for PatientDataParser. */
class PatientDataParserTest {
    private final PatientDataParser parser = new PatientDataParser();

    @Test
    void parseWebSocketMessageAcceptsValidNumericMessage() {
        Optional<PatientDataMessage> parsedMessage = parser.parseWebSocketMessage("1,1000,ECG,0.42");

        assertTrue(parsedMessage.isPresent());
        assertEquals(1, parsedMessage.get().getPatientId());
        assertEquals(1000L, parsedMessage.get().getTimestamp());
        assertEquals("ECG", parsedMessage.get().getLabel());
        assertEquals(0.42, parsedMessage.get().getMeasurementValue());
    }

    @Test
    void parseWebSocketMessageConvertsPercentageValue() {
        Optional<PatientDataMessage> parsedMessage = parser.parseWebSocketMessage("2,2000,Saturation,91%");

        assertTrue(parsedMessage.isPresent());
        assertEquals(91.0, parsedMessage.get().getMeasurementValue());
    }

    @Test
    void parseWebSocketMessageConvertsTriggeredAndResolvedAlertValues() {
        Optional<PatientDataMessage> triggeredMessage = parser.parseWebSocketMessage("3,3000,Alert,triggered");
        Optional<PatientDataMessage> resolvedMessage = parser.parseWebSocketMessage("3,4000,Alert,resolved");

        assertTrue(triggeredMessage.isPresent());
        assertTrue(resolvedMessage.isPresent());
        assertEquals(1.0, triggeredMessage.get().getMeasurementValue());
        assertEquals(0.0, resolvedMessage.get().getMeasurementValue());
    }

    @Test
    void parseWebSocketMessageRejectsCorruptedMessages() {
        assertFalse(parser.parseWebSocketMessage("").isPresent());
        assertFalse(parser.parseWebSocketMessage("1,1000,ECG").isPresent());
        assertFalse(parser.parseWebSocketMessage("patient,1000,ECG,0.5").isPresent());
        assertFalse(parser.parseWebSocketMessage("1,timestamp,ECG,0.5").isPresent());
        assertFalse(parser.parseWebSocketMessage("1,1000,ECG,not-a-number").isPresent());
    }

    @Test
    void parseFileLineAcceptsExistingFileOutputFormat() {
        Optional<PatientDataMessage> parsedMessage = parser.parseFileLine(
                "Patient ID: 4, Timestamp: 5000, Label: Saturation, Data: 95%");

        assertTrue(parsedMessage.isPresent());
        assertEquals(4, parsedMessage.get().getPatientId());
        assertEquals(5000L, parsedMessage.get().getTimestamp());
        assertEquals("Saturation", parsedMessage.get().getLabel());
        assertEquals(95.0, parsedMessage.get().getMeasurementValue());
    }
}