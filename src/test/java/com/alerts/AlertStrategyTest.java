package com.alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.Patient;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for concrete alert strategies. */
class AlertStrategyTest {
    @Test
    void bloodPressureStrategyUsesFactoryAndPriorityDecoratorForCriticalAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(181.0, "SystolicPressure", 1000L);

        List<Alert> alerts = new BloodPressureStrategy().checkAlert(patient);

        assertEquals(1, alerts.size());
        assertEquals("Critical Blood Pressure", alerts.get(0).getCondition());
        assertEquals("Blood Pressure", alerts.get(0).getAlertType());
        assertEquals("HIGH", alerts.get(0).getPriority());
    }

    @Test
    void oxygenSaturationStrategyCreatesRepeatedPriorityAlertForLowSaturation() {
        Patient patient = new Patient(2);
        patient.addRecord(91.0, "Saturation", 1000L);

        List<Alert> alerts = new OxygenSaturationStrategy().checkAlert(patient);

        assertEquals(1, alerts.size());
        assertEquals("Low Blood Saturation", alerts.get(0).getCondition());
        assertTrue(alerts.get(0).isRepeated());
        assertEquals("HIGH", alerts.get(0).getPriority());
    }

    @Test
    void heartRateStrategyCreatesRepeatedPriorityAlertForAbnormalEcgPeak() {
        Patient patient = new Patient(3);
        patient.addRecord(0.1, "ECG", 1000L);
        patient.addRecord(0.1, "ECG", 2000L);
        patient.addRecord(0.1, "ECG", 3000L);
        patient.addRecord(0.1, "ECG", 4000L);
        patient.addRecord(0.1, "ECG", 5000L);
        patient.addRecord(1.5, "ECG", 6000L);

        List<Alert> alerts = new HeartRateStrategy().checkAlert(patient);

        assertEquals(1, alerts.size());
        assertEquals("Abnormal ECG Peak", alerts.get(0).getCondition());
        assertEquals("ECG", alerts.get(0).getAlertType());
        assertTrue(alerts.get(0).isRepeated());
    }
}