package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for DataStorage.
 */
class DataStorageTest {
    @Test
    void addPatientDataCreatesNewPatientAndStoresRecord() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 98.0, "Saturation", 1000L);

        List<Patient> patients = storage.getAllPatients();
        List<PatientRecord> records = storage.getRecords(1, 0L, 2000L);

        assertEquals(1, patients.size());
        assertEquals(1, records.size());
        assertEquals(98.0, records.get(0).getMeasurementValue());
        assertEquals("Saturation", records.get(0).getRecordType());
    }

    @Test
    void getRecordsReturnsEmptyListForUnknownPatient() {
        DataStorage storage = new DataStorage();

        List<PatientRecord> records = storage.getRecords(999, 0L, 1000L);

        assertTrue(records.isEmpty());
    }

    @Test
    void getRecordsFiltersByTimeRange() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 80.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 90.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 100.0, "SystolicPressure", 3000L);

        List<PatientRecord> records = storage.getRecords(1, 1500L, 2500L);

        assertEquals(1, records.size());
        assertEquals(2000L, records.get(0).getTimestamp());
    }
}