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

/** Strategy for oxygen saturation threshold and rapid-drop alerts. */
public class OxygenSaturationStrategy extends AbstractAlertStrategy {
    private static final double SATURATION_LOW_THRESHOLD = 92.0;
    private static final double RAPID_DROP_THRESHOLD = 5.0;
    private static final long TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000L;
    private static final long RECHECK_INTERVAL_MILLIS = 60 * 1000L;

    private final AlertFactory alertFactory;

    public OxygenSaturationStrategy() {
        this(new BloodOxygenAlertFactory());
    }

    public OxygenSaturationStrategy(AlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> saturationRecords = filterByType(patient.getAllRecords(), "Saturation");

        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < SATURATION_LOW_THRESHOLD) {
                Alert alert = createAlert(patient, "Low Blood Saturation", record.getTimestamp());
                alerts.add(new RepeatedAlertDecorator(new PriorityAlertDecorator(alert), RECHECK_INTERVAL_MILLIS));
            }
        }

        for (int i = 0; i < saturationRecords.size(); i++) {
            PatientRecord earlier = saturationRecords.get(i);
            for (int j = i + 1; j < saturationRecords.size(); j++) {
                PatientRecord later = saturationRecords.get(j);
                long timeDifference = later.getTimestamp() - earlier.getTimestamp();
                if (timeDifference > TEN_MINUTES_IN_MILLIS) {
                    break;
                }
                if (earlier.getMeasurementValue() - later.getMeasurementValue() >= RAPID_DROP_THRESHOLD) {
                    alerts.add(new PriorityAlertDecorator(createAlert(patient, "Rapid Blood Saturation Drop", later.getTimestamp())));
                }
            }
        }

        return alerts;
    }

    private Alert createAlert(Patient patient, String condition, long timestamp) {
        return alertFactory.createAlert(patientIdAsString(patient.getPatientId()), condition, timestamp);
    }
}