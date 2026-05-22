package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for abnormal ECG peaks and heart-rate/rhythm related alerts.
 */
public class HeartRateStrategy extends AbstractAlertStrategy {
    private static final int ECG_WINDOW_SIZE = 5;
    private static final double ECG_PEAK_MULTIPLIER = 2.0;
    private static final double ECG_MINIMUM_PEAK_DIFFERENCE = 0.5;
    private static final long RECHECK_INTERVAL_MILLIS = 30 * 1000L;

    private final AlertFactory alertFactory;

    public HeartRateStrategy() {
        this(new ECGAlertFactory());
    }

    public HeartRateStrategy(AlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> ecgRecords = filterByType(patient.getAllRecords(), "ECG");

        for (int i = ECG_WINDOW_SIZE; i < ecgRecords.size(); i++) {
            List<PatientRecord> window = ecgRecords.subList(i - ECG_WINDOW_SIZE, i);
            double average = window.stream()
                    .mapToDouble(record -> Math.abs(record.getMeasurementValue()))
                    .average()
                    .orElse(0.0);
            double currentValue = Math.abs(ecgRecords.get(i).getMeasurementValue());

            if (currentValue > average * ECG_PEAK_MULTIPLIER
                    && currentValue - average > ECG_MINIMUM_PEAK_DIFFERENCE) {
                Alert alert = alertFactory.createAlert(
                        patientIdAsString(patient.getPatientId()),
                        "Abnormal ECG Peak",
                        ecgRecords.get(i).getTimestamp());
                alerts.add(new RepeatedAlertDecorator(new PriorityAlertDecorator(alert), RECHECK_INTERVAL_MILLIS));
            }
        }

        return alerts;
    }
}