package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated blood oxygen saturation readings for patients.
 *
 * <p>Each patient starts with a baseline saturation value between 95% and 100%.
 * Every generated reading applies a small random variation and keeps the value within
 * a realistic range before sending it to the configured {@link OutputStrategy}.</p>
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    /** Random number generator used to create baseline values and fluctuations. */
    private static final Random random = new Random();

    /** Stores the most recent saturation value for each patient by patient identifier. */
    private int[] lastSaturationValues;

    /**
     * Creates a generator and initializes a baseline saturation value for each patient.
     *
     * @param patientCount the number of patients for whom values should be generated
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates and outputs one blood saturation reading for a patient.
     *
     * @param patientId the identifier of the patient whose reading is generated
     * @param outputStrategy the strategy used to output the generated reading
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}