package com.alerts;

/**
 * Reusable immutable implementation for the core alert fields.
 */
public abstract class BaseAlert implements Alert {
    private final String patientId;
    private final String condition;
    private final long timestamp;

    /**
     * Creates an immutable alert.
     *
     * @param patientId patient identifier
     * @param condition condition that caused the alert
     * @param timestamp alert timestamp
     */
    protected BaseAlert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    @Override
    public String getPatientId() {
        return patientId;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
