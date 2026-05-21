package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Evaluates patient data and generates alerts when medical warning conditions occur.
 *
 * <p>Implemented for Project Part 3. The class checks blood pressure, oxygen saturation,
 * combined hypotensive hypoxemia, ECG peaks, and manually triggered alerts.</p>
 */
public class AlertGenerator {
    /** Systolic blood pressure is critical above this value. */
    private static final double SYSTOLIC_HIGH_THRESHOLD = 180.0;

    /** Systolic blood pressure is critical below this value. */
    private static final double SYSTOLIC_LOW_THRESHOLD = 90.0;

    /** Diastolic blood pressure is critical above this value. */
    private static final double DIASTOLIC_HIGH_THRESHOLD = 120.0;

    /** Diastolic blood pressure is critical below this value. */
    private static final double DIASTOLIC_LOW_THRESHOLD = 60.0;

    /** Blood oxygen saturation is considered low below this value. */
    private static final double SATURATION_LOW_THRESHOLD = 92.0;

    /** Time window for rapid oxygen saturation drop detection. */
    private static final long TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000L;

    /*
     * Assumption:
     * The assignment requires ECG alerts using a sliding window / moving average,
     * but does not provide exact numbers. I therefore use a 5-record moving average.
     * An ECG value is treated as an abnormal peak if its absolute value is more than
     * twice the average of the previous five absolute ECG values and at least 0.5 higher.
     */
    private static final int ECG_WINDOW_SIZE = 5;
    private static final double ECG_PEAK_MULTIPLIER = 2.0;
    private static final double ECG_MINIMUM_PEAK_DIFFERENCE = 0.5;

    /** Storage object containing all patient data. */
    private final DataStorage dataStorage;

    /** Alerts generated during evaluation. */
    private final List<Alert> triggeredAlerts;

    /**
     * Creates an alert generator.
     *
     * @param dataStorage storage containing patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.triggeredAlerts = new ArrayList<>();
    }

    /**
     * Evaluates one patient's data and creates alerts for all matching alert rules.
     *
     * @param patient patient whose records should be checked
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = patient.getAllRecords();

        checkBloodPressureAlerts(patient, records);
        checkSaturationAlerts(patient, records);
        checkHypotensiveHypoxemiaAlert(patient, records);
        checkEcgAlerts(patient, records);
        checkTriggeredAlerts(patient, records);
    }

    /**
     * Evaluates all patients currently stored in DataStorage.
     *
     * <p>This helper method is useful when running the system from DataStorage.main().</p>
     */
    public void evaluateAllPatients() {
        for (Patient patient : dataStorage.getAllPatients()) {
            evaluateData(patient);
        }
    }

    /**
     * Returns all generated alerts.
     *
     * @return copy of generated alerts
     */
    public List<Alert> getTriggeredAlerts() {
        return new ArrayList<>(triggeredAlerts);
    }

    /**
     * Checks blood pressure threshold alerts and blood pressure trend alerts.
     *
     * @param patient patient being evaluated
     * @param records all records belonging to the patient
     */
    private void checkBloodPressureAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolicRecords = filterByType(records, "SystolicPressure");
        List<PatientRecord> diastolicRecords = filterByType(records, "DiastolicPressure");

        for (PatientRecord record : systolicRecords) {
            double value = record.getMeasurementValue();

            if (value > SYSTOLIC_HIGH_THRESHOLD || value < SYSTOLIC_LOW_THRESHOLD) {
                triggerAlert(patient, "Critical Blood Pressure", record.getTimestamp());
            }
        }

        for (PatientRecord record : diastolicRecords) {
            double value = record.getMeasurementValue();

            if (value > DIASTOLIC_HIGH_THRESHOLD || value < DIASTOLIC_LOW_THRESHOLD) {
                triggerAlert(patient, "Critical Blood Pressure", record.getTimestamp());
            }
        }

        checkBloodPressureTrend(patient, systolicRecords, "Systolic Blood Pressure Trend");
        checkBloodPressureTrend(patient, diastolicRecords, "Diastolic Blood Pressure Trend");
    }

    /**
     * Checks whether three consecutive readings consistently increase or decrease by more than
     * 10 mmHg per step.
     *
     * @param patient patient being evaluated
     * @param records blood pressure records of one type
     * @param condition alert condition name
     */
    private void checkBloodPressureTrend(
            Patient patient,
            List<PatientRecord> records,
            String condition) {
        for (int i = 2; i < records.size(); i++) {
            double firstChange =
                    records.get(i - 1).getMeasurementValue()
                            - records.get(i - 2).getMeasurementValue();

            double secondChange =
                    records.get(i).getMeasurementValue()
                            - records.get(i - 1).getMeasurementValue();

            boolean increasing = firstChange > 10.0 && secondChange > 10.0;
            boolean decreasing = firstChange < -10.0 && secondChange < -10.0;

            if (increasing || decreasing) {
                triggerAlert(patient, condition, records.get(i).getTimestamp());
            }
        }
    }

    /**
     * Checks low blood oxygen saturation and rapid saturation drops.
     *
     * @param patient patient being evaluated
     * @param records all patient records
     */
    private void checkSaturationAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> saturationRecords = filterByType(records, "Saturation");

        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < SATURATION_LOW_THRESHOLD) {
                triggerAlert(patient, "Low Blood Saturation", record.getTimestamp());
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

                if (earlier.getMeasurementValue() - later.getMeasurementValue() >= 5.0) {
                    triggerAlert(patient, "Rapid Blood Saturation Drop", later.getTimestamp());
                }
            }
        }
    }

    /**
     * Checks the combined hypotensive hypoxemia alert.
     *
     * <p>This alert triggers when systolic blood pressure is below 90 and oxygen saturation is
     * below 92.</p>
     *
     * @param patient patient being evaluated
     * @param records all patient records
     */
    private void checkHypotensiveHypoxemiaAlert(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolicRecords = filterByType(records, "SystolicPressure");
        List<PatientRecord> saturationRecords = filterByType(records, "Saturation");

        for (PatientRecord systolicRecord : systolicRecords) {
            if (systolicRecord.getMeasurementValue() >= SYSTOLIC_LOW_THRESHOLD) {
                continue;
            }

            for (PatientRecord saturationRecord : saturationRecords) {
                if (saturationRecord.getMeasurementValue() < SATURATION_LOW_THRESHOLD) {
                    long alertTime = Math.max(
                            systolicRecord.getTimestamp(),
                            saturationRecord.getTimestamp());

                    triggerAlert(patient, "Hypotensive Hypoxemia", alertTime);
                    return;
                }
            }
        }
    }

    /**
     * Checks ECG records for abnormal peaks using a sliding moving-average window.
     *
     * @param patient patient being evaluated
     * @param records all patient records
     */
    private void checkEcgAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecgRecords = filterByType(records, "ECG");

        for (int i = ECG_WINDOW_SIZE; i < ecgRecords.size(); i++) {
            List<PatientRecord> window = ecgRecords.subList(i - ECG_WINDOW_SIZE, i);

            double average = window.stream()
                    .mapToDouble(record -> Math.abs(record.getMeasurementValue()))
                    .average()
                    .orElse(0.0);

            double currentValue = Math.abs(ecgRecords.get(i).getMeasurementValue());

            if (currentValue > average * ECG_PEAK_MULTIPLIER
                    && currentValue - average > ECG_MINIMUM_PEAK_DIFFERENCE) {
                triggerAlert(patient, "Abnormal ECG Peak", ecgRecords.get(i).getTimestamp());
            }
        }
    }

    /**
     * Checks manually triggered alerts from nurse/patient alert-button output.
     *
     * @param patient patient being evaluated
     * @param records all patient records
     */
    private void checkTriggeredAlerts(Patient patient, List<PatientRecord> records) {
        for (PatientRecord record : filterByType(records, "Alert")) {
            if (record.getMeasurementValue() >= 1.0) {
                triggerAlert(patient, "Manual Alert Triggered", record.getTimestamp());
            }
        }
    }

    /**
     * Filters records by record type.
     *
     * @param records records to filter
     * @param recordType requested record type
     * @return records matching the requested type
     */
    private List<PatientRecord> filterByType(List<PatientRecord> records, String recordType) {
        return records.stream()
                .filter(record -> recordType.equalsIgnoreCase(record.getRecordType()))
                .collect(Collectors.toList());
    }

    /**
     * Creates an alert for a patient.
     *
     * @param patient patient who triggered the alert
     * @param condition condition that triggered the alert
     * @param timestamp alert timestamp
     */
    private void triggerAlert(Patient patient, String condition, long timestamp) {
        triggerAlert(new Alert(Integer.toString(patient.getPatientId()), condition, timestamp));
    }

    /**
     * Stores an alert internally.
     *
     * @param alert alert to store
     */
    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
    }
}