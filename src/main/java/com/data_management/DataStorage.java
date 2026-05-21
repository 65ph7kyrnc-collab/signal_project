package com.data_management;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores and retrieves patient records for the monitoring system.
 *
 * <p>This class is the central in-memory repository for Project Part 3. It maps each patient ID
 * to one {@link Patient} object. The actual individual measurements are stored inside the
 * corresponding Patient object.</p>
 */
public class DataStorage {
    /** Stores Patient objects by their unique patient identifier. */
    private final Map<Integer, Patient> patientMap;

    /** Creates an empty patient-data storage object. */
    public DataStorage() {
        this.patientMap = new HashMap<>();
    }

    /**
     * Adds one measurement to the correct patient.
     *
     * <p>If the patient does not exist yet, the method creates the patient first. This keeps the
     * caller simple because callers do not have to manually check whether the patient already
     * exists.</p>
     *
     * @param patientId unique patient identifier
     * @param measurementValue numeric measurement value
     * @param recordType measurement type, for example {@code ECG}, {@code Saturation},
     *        {@code SystolicPressure}, or {@code DiastolicPressure}
     * @param timestamp measurement timestamp in milliseconds
     */
    public void addPatientData(
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
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);

        if (patient == null) {
            return new ArrayList<>();
        }

        return patient.getRecords(startTime, endTime);
    }

    /**
     * Returns a copy of all patients currently stored.
     *
     * <p>Returning a copy prevents outside code from accidentally changing the internal storage
     * structure.</p>
     *
     * @return list of all known patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * Optional command-line entry point for reading file output and evaluating alerts.
     *
     * <p>Usage:</p>
     *
     * <pre>
     * mvn exec:java -Dexec.args="DataStorage path/to/output-directory"
     * </pre>
     *
     * <p>If no directory is given, the method prints usage instructions and exits without failing.
     * This makes the class safe to run while still supporting the assignment requirement that
     * DataStorage can be executed from Maven.</p>
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

        DataStorage storage = new DataStorage();
        DataReader reader = new FileDataReader(args[0]);
        reader.readData(storage);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        System.out.println("Loaded patients: " + storage.getAllPatients().size());
        System.out.println("Triggered alerts: " + alertGenerator.getTriggeredAlerts().size());

        for (Alert alert : alertGenerator.getTriggeredAlerts()) {
            System.out.println(
                    "Patient " + alert.getPatientId()
                            + " | " + alert.getCondition()
                            + " | " + alert.getTimestamp());
        }
    }
}