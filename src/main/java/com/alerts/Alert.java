package com.alerts;

/**
 * Represents one generated alert.
 *
 * <p>An alert stores which patient triggered the alert, what condition triggered it, and when it
 * happened.</p>
 */
public class Alert {
    /** Patient ID as a string, matching the assignment's alert description format. */
    private final String patientId;

    /** Medical condition or event that triggered the alert. */
    private final String condition;

    /** Timestamp at which the alert was generated. */
    private final long timestamp;

    /**
     * Creates an alert.
     *
     * @param patientId patient identifier
     * @param condition alert condition
     * @param timestamp alert timestamp
     */
    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    /**
     * Returns the patient ID.
     *
     * @return patient ID
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Returns the alert condition.
     *
     * @return alert condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Returns the alert timestamp.
     *
     * @return timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
}