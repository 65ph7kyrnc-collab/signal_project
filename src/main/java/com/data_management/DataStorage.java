package com.data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton in-memory repository for patient records.
 *
 * <p>Project Part 4 requires DataStorage to have exactly one globally accessible instance. The
 * constructor is private and all production code obtains the repository through
 * {@link #getInstance()}.</p>
 */
public class DataStorage {
    private static final DataStorage INSTANCE = new DataStorage();

    /** Stores Patient objects by their unique patient identifier. */
    private final Map<Integer, Patient> patientMap;

    /** Creates an empty patient-data storage object. Private because this class is a singleton. */
    private DataStorage() {
        this.patientMap = new HashMap<>();
    }

    /**
     * Returns the only DataStorage instance.
     *
     * @return global DataStorage instance
     */
    public static DataStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Clears all stored patients and records. This is mainly used by tests and controlled reruns.
     */
    public synchronized void clear() {
        patientMap.clear();
    }

    /**
     * Adds one measurement to the correct patient.
     *
     * @param patientId unique patient identifier
     * @param measurementValue numeric measurement value
     * @param recordType measurement type, for example ECG, Saturation, SystolicPressure, or DiastolicPressure
     * @param timestamp measurement timestamp in milliseconds
     */
    public synchronized void addPatientData(
            int patientId,
            double measurementValue,
            String recordType,
            long timestamp) {
        Patient patient = patientMap.computeIfAbsent(patientId, Patient::new);
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieves records for one patient in the requested time range.
     *
     * @param patientId unique patient identifier
     * @param startTime first timestamp to include
     * @param endTime last timestamp to include
     * @return all matching records, or an empty list when the patient is unknown
     */
    public synchronized List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient == null) {
            return new ArrayList<>();
        }
        return patient.getRecords(startTime, endTime);
    }

    /**
     * Returns a copy of all patients currently stored.
     *
     * @return list of all known patients
     */
    public synchronized List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * Optional command-line entry point for reading file output and evaluating alerts.
     *
     * @param args optional first argument: directory containing simulator output files
     * @throws IOException if the directory cannot be read
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: DataStorage <output-directory>");
            System.out.println("Example: mvn exec:java -Dexec.args=\"DataStorage output\"");
            return;
        }

        DataStorage storage = DataStorage.getInstance();
        storage.clear();

        DataReader reader = new FileDataReader(args[0]);
        reader.readData(storage);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        alertGenerator.evaluateAllPatients();

        System.out.println("Loaded patients: " + storage.getAllPatients().size());
        System.out.println("Triggered alerts: " + alertGenerator.getTriggeredAlerts().size());

        for (Alert alert : alertGenerator.getTriggeredAlerts()) {
            System.out.println(
                    "Patient " + alert.getPatientId()
                            + " | " + alert.getCondition()
                            + " | type=" + alert.getAlertType()
                            + " | priority=" + alert.getPriority()
                            + " | repeated=" + alert.isRepeated()
                            + " | " + alert.getTimestamp());
        }
    }
}