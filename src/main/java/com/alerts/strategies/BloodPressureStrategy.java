package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for critical blood pressure thresholds and blood pressure trend alerts.
 */
public class BloodPressureStrategy extends AbstractAlertStrategy {
    private static final double SYSTOLIC_HIGH_THRESHOLD = 180.0;
    private static final double SYSTOLIC_LOW_THRESHOLD = 90.0;
    private static final double DIASTOLIC_HIGH_THRESHOLD = 120.0;
    private static final double DIASTOLIC_LOW_THRESHOLD = 60.0;
    private static final double TREND_CHANGE_THRESHOLD = 10.0;

    private final AlertFactory alertFactory;

    public BloodPressureStrategy() {
        this(new BloodPressureAlertFactory());
    }

    public BloodPressureStrategy(AlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> records = patient.getAllRecords();
        List<PatientRecord> systolicRecords = filterByType(records, "SystolicPressure");
        List<PatientRecord> diastolicRecords = filterByType(records, "DiastolicPressure");

        for (PatientRecord record : systolicRecords) {
            double value = record.getMeasurementValue();
            if (value > SYSTOLIC_HIGH_THRESHOLD || value < SYSTOLIC_LOW_THRESHOLD) {
                alerts.add(new PriorityAlertDecorator(createAlert(patient, "Critical Blood Pressure", record.getTimestamp())));
            }
        }

        for (PatientRecord record : diastolicRecords) {
            double value = record.getMeasurementValue();
            if (value > DIASTOLIC_HIGH_THRESHOLD || value < DIASTOLIC_LOW_THRESHOLD) {
                alerts.add(new PriorityAlertDecorator(createAlert(patient, "Critical Blood Pressure", record.getTimestamp())));
            }
        }

        addTrendAlerts(patient, systolicRecords, "Systolic Blood Pressure Trend", alerts);
        addTrendAlerts(patient, diastolicRecords, "Diastolic Blood Pressure Trend", alerts);
        return alerts;
    }

    private void addTrendAlerts(Patient patient, List<PatientRecord> records, String condition, List<Alert> alerts) {
        for (int i = 2; i < records.size(); i++) {
            double firstChange = records.get(i - 1).getMeasurementValue() - records.get(i - 2).getMeasurementValue();
            double secondChange = records.get(i).getMeasurementValue() - records.get(i - 1).getMeasurementValue();

            boolean increasing = firstChange > TREND_CHANGE_THRESHOLD && secondChange > TREND_CHANGE_THRESHOLD;
            boolean decreasing = firstChange < -TREND_CHANGE_THRESHOLD && secondChange < -TREND_CHANGE_THRESHOLD;

            if (increasing || decreasing) {
                alerts.add(createAlert(patient, condition, records.get(i).getTimestamp()));
            }
        }
    }

    private Alert createAlert(Patient patient, String condition, long timestamp) {
        return alertFactory.createAlert(patientIdAsString(patient.getPatientId()), condition, timestamp);
    }
}