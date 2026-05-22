package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;

/** Factory for blood oxygen alerts. */
public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
}