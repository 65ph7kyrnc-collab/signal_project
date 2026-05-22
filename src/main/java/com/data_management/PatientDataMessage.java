package com.data_management;

/**
 * Immutable value object representing one parsed patient-data message.
 */
public class PatientDataMessage {
    private final int patientId;
    private final long timestamp;
    private final String label;
    private final double measurementValue;

    /**
     * Creates a parsed patient-data message.
     *
     * @param patientId unique patient identifier
     * @param timestamp time at which the measurement was generated
     * @param label measurement type, for example ECG, Saturation, or Alert
     * @param measurementValue numeric representation of the measurement
     */
    public PatientDataMessage(int patientId, long timestamp, String label, double measurementValue) {
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.label = label;
        this.measurementValue = measurementValue;
    }

    public int getPatientId() {
        return patientId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getLabel() {
        return label;
    }

    public double getMeasurementValue() {
        return measurementValue;
    }
}