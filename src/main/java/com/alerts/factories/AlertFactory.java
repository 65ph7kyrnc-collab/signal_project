package com.alerts.factories;

import com.alerts.Alert;

/**
 * Factory Method base class for creating alert objects without exposing concrete alert classes
 * to the code that evaluates patient data.
 */
public abstract class AlertFactory {
    /**
     * Creates a concrete alert for the supplied patient, condition, and timestamp.
     *
     * @param patientId patient identifier
     * @param condition condition that caused the alert
     * @param timestamp alert timestamp
     * @return concrete alert instance
     */
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}