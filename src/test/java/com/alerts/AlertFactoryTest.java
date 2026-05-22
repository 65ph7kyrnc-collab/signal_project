package com.alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.alerts.factories.ECGAlertFactory;
import org.junit.jupiter.api.Test;

/** Tests for the Factory Method implementation. */
class AlertFactoryTest {
    @Test
    void bloodPressureFactoryCreatesBloodPressureAlert() {
        Alert alert = new BloodPressureAlertFactory().createAlert("1", "Critical Blood Pressure", 1000L);

        assertInstanceOf(BloodPressureAlert.class, alert);
        assertEquals("1", alert.getPatientId());
        assertEquals("Critical Blood Pressure", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
        assertEquals("Blood Pressure", alert.getAlertType());
    }

    @Test
    void bloodOxygenFactoryCreatesBloodOxygenAlert() {
        Alert alert = new BloodOxygenAlertFactory().createAlert("2", "Low Blood Saturation", 2000L);

        assertInstanceOf(BloodOxygenAlert.class, alert);
        assertEquals("Blood Oxygen", alert.getAlertType());
    }

    @Test
    void ecgFactoryCreatesEcgAlert() {
        Alert alert = new ECGAlertFactory().createAlert("3", "Abnormal ECG Peak", 3000L);

        assertInstanceOf(ECGAlert.class, alert);
        assertEquals("ECG", alert.getAlertType());
    }
}