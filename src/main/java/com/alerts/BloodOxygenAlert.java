package com.alerts;

/** Alert generated for low oxygen saturation or rapid oxygen drops. */
public class BloodOxygenAlert extends BaseAlert {
    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

    @Override
    public String getAlertType() {
        return "Blood Oxygen";
    }
}