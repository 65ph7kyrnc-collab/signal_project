package com.alerts;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.data_management.DataStorage;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for AlertGenerator.
 */
class AlertGeneratorTest {
    @Test
    void evaluateDataTriggersCriticalSystolicBloodPressureAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 181.0, "SystolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(hasCondition(generator, "Critical Blood Pressure"));
    }

    @Test
    void evaluateDataTriggersCriticalDiastolicBloodPressureAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 121.0, "DiastolicPressure", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(hasCondition(generator, "Critical Blood Pressure"));
    }

    @Test
    void exactBloodPressureThresholdsDoNotTriggerCriticalAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 180.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 90.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 120.0, "DiastolicPressure", 3000L);
        storage.addPatientData(1, 60.0, "DiastolicPressure", 4000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertFalse(hasCondition(generator, "Critical Blood Pressure"));
    }

    @Test
    void evaluateDataTriggersIncreasingBloodPressureTrendAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 112.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 125.0, "SystolicPressure", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(hasCondition(generator, "Systolic Blood Pressure Trend"));
    }

    @Test
    void evaluateDataTriggersDecreasingBloodPressureTrendAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 130.0, "DiastolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "DiastolicPressure", 2000L);
        storage.addPatientData(1, 100.0, "DiastolicPressure", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(hasCondition(generator, "Diastolic Blood Pressure Trend"));
    }

    @Test
    void exactTenPointBloodPressureChangesDoNotTriggerTrendAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 110.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 120.0, "SystolicPressure", 3000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertFalse(hasCondition(generator, "Systolic Blood Pressure Trend"));
    }

    @Test
    void evaluateDataTriggersLowSaturationAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 91.0, "Saturation", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(hasCondition(generator, "Low Blood Saturation"));
    }

    @Test
    void exactSaturationThresholdDoesNotTriggerLowSaturationAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 92.0, "Saturation", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertFalse(hasCondition(generator, "Low Blood Saturation"));
    }

    @Test
    void evaluateDataTriggersRapidSaturationDropAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        storage.addPatientData(1, 93.0, "Saturation", 1000L + 5 * 60 * 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(hasCondition(generator, "Rapid Blood Saturation Drop"));
    }

    @Test
    void rapidSaturationDropAfterMoreThanTenMinutesDoesNotTriggerAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        storage.addPatientData(1, 90.0, "Saturation", 1000L + 11 * 60 * 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertFalse(hasCondition(generator, "Rapid Blood Saturation Drop"));
    }

    @Test
    void evaluateDataTriggersHypotensiveHypoxemiaAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 88.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 91.0, "Saturation", 2000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(hasCondition(generator, "Hypotensive Hypoxemia"));
    }

    @Test
    void evaluateDataTriggersManualAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 1.0, "Alert", 1000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(hasCondition(generator, "Manual Alert Triggered"));
    }

    @Test
    void evaluateDataTriggersAbnormalEcgPeakAlert() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, 0.1, "ECG", 1000L);
        storage.addPatientData(1, 0.1, "ECG", 2000L);
        storage.addPatientData(1, 0.1, "ECG", 3000L);
        storage.addPatientData(1, 0.1, "ECG", 4000L);
        storage.addPatientData(1, 0.1, "ECG", 5000L);
        storage.addPatientData(1, 1.5, "ECG", 6000L);

        AlertGenerator generator = new AlertGenerator(storage);
        generator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(hasCondition(generator, "Abnormal ECG Peak"));
    }

    private boolean hasCondition(AlertGenerator generator, String condition) {
        return generator.getTriggeredAlerts().stream()
                .anyMatch(alert -> condition.equals(alert.getCondition()));
    }
}