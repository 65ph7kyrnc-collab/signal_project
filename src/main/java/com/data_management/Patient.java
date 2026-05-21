package com.data_management;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents one patient and stores all medical records belonging to that patient.
 *
 * <p>This class is responsible only for patient-level record storage and retrieval. It does not
 * know anything about alert rules or file parsing, which keeps the design modular.</p>
 */
public class Patient {
    /** Unique identifier of this patient. */
    private final int patientId;

    /** All records belonging to this patient. */
    private final List<PatientRecord> patientRecords;

    /**
     * Creates a patient with the given ID.
     *
     * @param patientId unique patient identifier
     */
    public Patient(int patientId) {
        this.patientId = patientId;
        this.patientRecords = new ArrayList<>();
    }

    /**
     * Returns this patient's ID.
     *
     * @return patient ID
     */
    public int getPatientId() {
        return patientId;
    }

    /**
     * Adds a new medical record to this patient.
     *
     * @param measurementValue numeric measurement value
     * @param recordType measurement type, for example {@code ECG}, {@code Saturation},
     *        {@code SystolicPressure}, {@code DiastolicPressure}, or {@code Alert}
     * @param timestamp measurement timestamp
     */
    public void addRecord(double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(patientId, measurementValue, recordType, timestamp);
        patientRecords.add(record);
    }

    /**
     * Returns all records between the given start and end timestamps, including both boundaries.
     *
     * <p>Implemented for Project Part 3. The assignment explicitly requires this method to filter
     * patient records by time range.</p>
     *
     * @param startTime first timestamp to include
     * @param endTime last timestamp to include
     * @return matching records sorted by timestamp
     */
    public List<PatientRecord> getRecords(long startTime, long endTime) {
        List<PatientRecord> recordsInRange = new ArrayList<>();

        for (PatientRecord record : patientRecords) {
            long timestamp = record.getTimestamp();

            if (timestamp >= startTime && timestamp <= endTime) {
                recordsInRange.add(record);
            }
        }

        recordsInRange.sort(Comparator.comparingLong(PatientRecord::getTimestamp));
        return recordsInRange;
    }

    /**
     * Returns all records for this patient.
     *
     * <p>A copy is returned to avoid exposing the internal mutable list. This prevents outside
     * classes from accidentally changing this patient's stored records.</p>
     *
     * @return all records sorted by timestamp
     */
    public List<PatientRecord> getAllRecords() {
        List<PatientRecord> records = new ArrayList<>(patientRecords);
        records.sort(Comparator.comparingLong(PatientRecord::getTimestamp));
        return records;
    }
}