package com.data_management;

/**
 * Represents one individual medical measurement for one patient.
 *
 * <p>Examples of records are heart-rate data, blood pressure readings, blood oxygen saturation,
 * ECG values, and manually triggered alert-button events.</p>
 */
public class PatientRecord {
    /** Patient ID this record belongs to. */
    private final int patientId;

    /** Numeric value of the measurement. */
    private final double measurementValue;

    /** Type of measurement, for example ECG, Saturation, or SystolicPressure. */
    private final String recordType;

    /** Time at which the measurement was created. */
    private final long timestamp;

    /**
     * Creates one patient record.
     *
     * @param patientId unique patient identifier
     * @param measurementValue numeric measurement value
     * @param recordType type of the measurement
     * @param timestamp timestamp of the measurement
     */
    public PatientRecord(
            int patientId,
            double measurementValue,
            String recordType,
            long timestamp) {
        this.patientId = patientId;
        this.measurementValue = measurementValue;
        this.recordType = recordType;
        this.timestamp = timestamp;
    }

    /**
     * Returns the patient ID.
     *
     * @return patient ID
     */
    public int getPatientId() {
        return patientId;
    }

    /**
     * Returns the measurement value.
     *
     * @return measurement value
     */
    public double getMeasurementValue() {
        return measurementValue;
    }

    /**
     * Returns the record type.
     *
     * @return record type
     */
    public String getRecordType() {
        return recordType;
    }

    /**
     * Returns the timestamp.
     *
     * @return timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
}