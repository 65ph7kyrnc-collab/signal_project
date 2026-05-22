package com.alerts.strategies;

import com.data_management.PatientRecord;
import java.util.List;
import java.util.stream.Collectors;

/** Shared helper methods for alert strategies. */
abstract class AbstractAlertStrategy implements AlertStrategy {
    protected List<PatientRecord> filterByType(List<PatientRecord> records, String recordType) {
        return records.stream()
                .filter(record -> recordType.equalsIgnoreCase(record.getRecordType()))
                .collect(Collectors.toList());
    }

    protected String patientIdAsString(int patientId) {
        return Integer.toString(patientId);
    }
}