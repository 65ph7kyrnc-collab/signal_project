package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for FileDataReader.
 */
class FileDataReaderTest {
    @BeforeEach
    void clearSingletonStorageBeforeEachTest() {
        DataStorage.getInstance().clear();
    }

    @Test
    void readDataParsesSimulatorOutputFiles() throws Exception {
        Path directory = Files.createTempDirectory("signal-reader-test");

        Files.writeString(
                directory.resolve("Saturation.txt"),
                "Patient ID: 2, Timestamp: 1000, Label: Saturation, Data: 91%\n");

        Files.writeString(
                directory.resolve("Alert.txt"),
                "Patient ID: 2, Timestamp: 2000, Label: Alert, Data: triggered\n");

        DataStorage storage = DataStorage.getInstance();

        new FileDataReader(directory.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(2, 0L, 3000L);

        assertEquals(2, records.size());
        assertEquals("Saturation", records.get(0).getRecordType());
        assertEquals(91.0, records.get(0).getMeasurementValue());
        assertEquals("Alert", records.get(1).getRecordType());
        assertEquals(1.0, records.get(1).getMeasurementValue());
    }

    @Test
    void readDataIgnoresInvalidLines() throws Exception {
        Path directory = Files.createTempDirectory("signal-reader-invalid-test");

        Files.writeString(
                directory.resolve("invalid.txt"),
                "This is not a valid simulator output line\n"
                        + "Patient ID: 1, Timestamp: 1000, Label: ECG, Data: 0.5\n");

        DataStorage storage = DataStorage.getInstance();

        new FileDataReader(directory.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, 2000L);

        assertEquals(1, records.size());
        assertEquals(0.5, records.get(0).getMeasurementValue());
    }

    @Test
    void readDataThrowsExceptionForMissingDirectory() {
        DataStorage storage = DataStorage.getInstance();

        assertThrows(
                IOException.class,
                () -> new FileDataReader("this-directory-does-not-exist").readData(storage));
    }
}