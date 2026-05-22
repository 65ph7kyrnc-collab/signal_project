package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.SystemAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;

/** Strategy for manually triggered alert-button events. */
public class ManualAlertStrategy extends AbstractAlertStrategy {
    private final AlertFactory alertFactory;

    public ManualAlertStrategy() {
        this(new SystemAlertFactory());
    }

    public ManualAlertStrategy(AlertFactory alertFactory) {
        this.alertFactory = alertFactory;
    }

    @Override
    public List<Alert> checkAlert(Patient patient) {
        List<Alert> alerts = new ArrayList<>();
        for (PatientRecord record : filterByType(patient.getAllRecords(), "Alert")) {
            if (record.getMeasurementValue() >= 1.0) {
                Alert alert = alertFactory.createAlert(
                        patientIdAsString(patient.getPatientId()),
                        "Manual Alert Triggered",
                        record.getTimestamp());
                alerts.add(new PriorityAlertDecorator(alert));
            }
        }
        return alerts;
    }
}