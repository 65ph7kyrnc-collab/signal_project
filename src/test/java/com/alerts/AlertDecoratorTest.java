package com.alerts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import org.junit.jupiter.api.Test;

/** Tests for alert decorators. */
class AlertDecoratorTest {
    @Test
    void priorityDecoratorPreservesAlertDataAndChangesPriority() {
        Alert baseAlert = new BloodPressureAlert("1", "Critical Blood Pressure", 1000L);
        Alert decorated = new PriorityAlertDecorator(baseAlert, "URGENT");

        assertEquals("1", decorated.getPatientId());
        assertEquals("Critical Blood Pressure", decorated.getCondition());
        assertEquals(1000L, decorated.getTimestamp());
        assertEquals("Blood Pressure", decorated.getAlertType());
        assertEquals("URGENT", decorated.getPriority());
    }

    @Test
    void repeatedDecoratorAddsRepeatMetadata() {
        Alert baseAlert = new BloodOxygenAlert("2", "Low Blood Saturation", 2000L);
        Alert decorated = new RepeatedAlertDecorator(baseAlert, 60000L);

        assertEquals("2", decorated.getPatientId());
        assertTrue(decorated.isRepeated());
        assertEquals(60000L, decorated.getRepeatIntervalMillis());
    }

    @Test
    void decoratorsCanBeStacked() {
        Alert baseAlert = new ECGAlert("3", "Abnormal ECG Peak", 3000L);
        Alert decorated = new RepeatedAlertDecorator(new PriorityAlertDecorator(baseAlert), 30000L);

        assertEquals("HIGH", decorated.getPriority());
        assertTrue(decorated.isRepeated());
        assertEquals(30000L, decorated.getRepeatIntervalMillis());
    }
}