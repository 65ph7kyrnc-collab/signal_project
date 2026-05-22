package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.BloodPressureAlert;

/** Factory for blood pressure alerts. */
public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp);
    }
}