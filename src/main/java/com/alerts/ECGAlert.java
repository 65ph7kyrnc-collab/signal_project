package com.alerts;

/** Alert generated for abnormal heart rate or ECG rhythm behaviour. */
public class ECGAlert extends BaseAlert {
    public ECGAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

    @Override
    public String getAlertType() {
        return "ECG";
    }
}