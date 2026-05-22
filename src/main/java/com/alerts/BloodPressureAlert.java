package com.alerts;

/** Alert generated for blood pressure threshold or trend anomalies. */
public class BloodPressureAlert extends BaseAlert {
    public BloodPressureAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

    @Override
    public String getAlertType() {
        return "Blood Pressure";
    }
}