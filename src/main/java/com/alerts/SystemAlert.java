package com.alerts;

/** Generic system/manual alert used for patient-triggered alert-button events. */
public class SystemAlert extends BaseAlert {
    public SystemAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

    @Override
    public String getAlertType() {
        return "System";
    }
}