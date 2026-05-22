package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.ECGAlert;

/** Factory for ECG and heart rhythm alerts. */
public class ECGAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, condition, timestamp);
    }
}