package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Patient.
 */
class PatientTest {
    @Test
    void getRecordsReturnsOnlyRecordsInsideInclusiveTimeRange() {
        Patient patient = new Patient(1);

        patient.addRecord(90.0, "Saturation", 1000L);
        patient.addRecord(91.0, "Saturation", 2000L);
        patient.addRecord(92.0, "Saturation", 3000L);

        List<PatientRecord> records = patient.getRecords(1000L, 2000L);

        assertEquals(2, records.size());
        assertEquals(1000L, records.get(0).getTimestamp());
        assertEquals(2000L, records.get(1).getTimestamp());
    }

    @Test
    void getRecordsReturnsEmptyListWhenNoRecordsMatch() {
        Patient patient = new Patient(1);

        patient.addRecord(90.0, "Saturation", 1000L);

        assertTrue(patient.getRecords(2000L, 3000L).isEmpty());
    }

    @Test
    void getAllRecordsReturnsRecordsSortedByTimestamp() {
        Patient patient = new Patient(1);

        patient.addRecord(92.0, "Saturation", 3000L);
        patient.addRecord(90.0, "Saturation", 1000L);
        patient.addRecord(91.0, "Saturation", 2000L);

        List<PatientRecord> records = patient.getAllRecords();

        assertEquals(1000L, records.get(0).getTimestamp());
        assertEquals(2000L, records.get(1).getTimestamp());
        assertEquals(3000L, records.get(2).getTimestamp());
    }
}