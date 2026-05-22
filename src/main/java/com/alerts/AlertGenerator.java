package com.alerts;

import com.alerts.strategies.AlertStrategy;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.CombinedRiskStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.ManualAlertStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates alert evaluation by executing independent alert strategies.
 *
 * <p>Project Part 4 refactors the previous monolithic alert logic into Strategy classes. Those
 * strategies use Factory Method classes to create concrete alert objects and decorators to add
 * priority/repeated-alert metadata where needed.</p>
 */
public class AlertGenerator {
    private final DataStorage dataStorage;
    private final List<AlertStrategy> strategies;
    private final List<Alert> triggeredAlerts;

    /**
     * Creates an alert generator using the default project strategies.
     *
     * @param dataStorage storage containing patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this(dataStorage, createDefaultStrategies());
    }

    /**
     * Creates the default list of alert strategies.
     *
     * <p>This method avoids Arrays.asList(...) because Java's type inference can sometimes fail
     * when several concrete strategy classes are passed into a generic varargs method.</p>
     *
     * @return default alert strategies
     */
    private static List<AlertStrategy> createDefaultStrategies() {
        List<AlertStrategy> defaultStrategies = new ArrayList<>();

        defaultStrategies.add(new BloodPressureStrategy());
        defaultStrategies.add(new OxygenSaturationStrategy());
        defaultStrategies.add(new CombinedRiskStrategy());
        defaultStrategies.add(new HeartRateStrategy());
        defaultStrategies.add(new ManualAlertStrategy());

        return defaultStrategies;
    }

    /**
     * Creates an alert generator with injected strategies, useful for testing and extension.
     *
     * @param dataStorage storage containing patient data
     * @param strategies strategies to execute
     */
    public AlertGenerator(DataStorage dataStorage, List<AlertStrategy> strategies) {
        this.dataStorage = dataStorage;
        this.strategies = new ArrayList<>(strategies);
        this.triggeredAlerts = new ArrayList<>();
    }

    /**
     * Evaluates one patient's data with all configured strategies.
     *
     * @param patient patient whose records should be checked
     */
    public void evaluateData(Patient patient) {
        for (AlertStrategy strategy : strategies) {
            triggeredAlerts.addAll(strategy.checkAlert(patient));
        }
    }

    /** Evaluates all patients currently stored in DataStorage. */
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

    /** Clears previously generated alerts. Useful before re-running evaluations. */
    public void clearTriggeredAlerts() {
        triggeredAlerts.clear();
    }
}