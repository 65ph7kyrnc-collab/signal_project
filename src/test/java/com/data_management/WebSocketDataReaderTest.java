package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for WebSocketDataReader message handling. */
class WebSocketDataReaderTest {
    @BeforeEach
    void clearStorage() {
        DataStorage.getInstance().clear();
    }

    @Test
    void handleMessageStoresValidMessage() throws Exception {
        DataStorage storage = DataStorage.getInstance();
        WebSocketDataReader reader = new WebSocketDataReader(new URI("ws://localhost:8080"));

        boolean stored = reader.handleMessage("1,1000,ECG,0.5", storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 2000L);
        assertTrue(stored);
        assertEquals(1, records.size());
        assertEquals("ECG", records.get(0).getRecordType());
        assertEquals(0.5, records.get(0).getMeasurementValue());
    }

    @Test
    void handleMessageIgnoresInvalidMessageAndKeepsStorageUnchanged() throws Exception {
        DataStorage storage = DataStorage.getInstance();
        WebSocketDataReader reader = new WebSocketDataReader(new URI("ws://localhost:8080"));

        boolean stored = reader.handleMessage("this-is-not-valid", storage);

        assertFalse(stored);
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void dataStorageDoesNotStoreDuplicateRealTimeMessages() throws Exception {
        DataStorage storage = DataStorage.getInstance();
        WebSocketDataReader reader = new WebSocketDataReader(new URI("ws://localhost:8080"));

        reader.handleMessage("2,1000,Saturation,91%", storage);
        reader.handleMessage("2,1000,Saturation,91%", storage);

        assertEquals(1, storage.getRecords(2, 0L, 2000L).size());
    }
}