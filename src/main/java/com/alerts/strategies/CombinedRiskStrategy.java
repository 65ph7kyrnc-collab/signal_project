package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for the combined hypotensive hypoxemia condition.
 */
public class CombinedRiskStrategy extends AbstractAlertStrategy {
    private static final double SYSTOLIC_LOW_THRESHOLD = 90.0;
    private static final double SATURATION_LOW_THRESHOLD = 92.0;
    private static final long RECHECK_INTERVAL_MILLIS = 60 * 1000L;

    private final AlertFactory alertFactory;

    public CombinedRiskStrategy() {
        this(new BloodOxygenAlertFactory());
    }

    public CombinedRiskStrategy(AlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getAllRecords();
        List<PatientRecord> systolicRecords = filterByType(records, "SystolicPressure");
        List<PatientRecord> saturationRecords = filterByType(records, "Saturation");

        for (PatientRecord systolicRecord : systolicRecords) {
            if (systolicRecord.getMeasurementValue() >= SYSTOLIC_LOW_THRESHOLD) {
                continue;
            }

            for (PatientRecord saturationRecord : saturationRecords) {
                if (saturationRecord.getMeasurementValue() < SATURATION_LOW_THRESHOLD) {
                    long alertTime = Math.max(systolicRecord.getTimestamp(), saturationRecord.getTimestamp());
                    Alert alert = alertFactory.createAlert(
                            patientIdAsString(patient.getPatientId()),
                            "Hypotensive Hypoxemia",
                            alertTime);
                    alerts.add(new RepeatedAlertDecorator(new PriorityAlertDecorator(alert), RECHECK_INTERVAL_MILLIS));
                    return alerts;
                }
            }
        }

        return alerts;
    }
}