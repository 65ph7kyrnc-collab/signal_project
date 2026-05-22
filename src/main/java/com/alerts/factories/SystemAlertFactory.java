package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.SystemAlert;

/** Factory for generic/manual system alerts. */
public class SystemAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new SystemAlert(patientId, condition, timestamp);
    }
}