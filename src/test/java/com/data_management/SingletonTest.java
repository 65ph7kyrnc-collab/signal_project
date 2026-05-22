package com.data_management;

import static org.junit.jupiter.api.Assertions.assertSame;

import com.cardio_generator.HealthDataSimulator;
import org.junit.jupiter.api.Test;

/** Tests for singleton requirements. */
class SingletonTest {
    @Test
    void dataStorageGetInstanceAlwaysReturnsSameObject() {
        assertSame(DataStorage.getInstance(), DataStorage.getInstance());
    }

    @Test
    void healthDataSimulatorGetInstanceAlwaysReturnsSameObject() {
        assertSame(HealthDataSimulator.getInstance(), HealthDataSimulator.getInstance());
    }
}