package com.cardio_generator.outputs;

/**
 * Defines a common output contract for generated patient data.
 */
public interface OutputStrategy {
    /**
     * Outputs one generated patient data record.
     *
     * @param patientId the identifier of the patient associated with the data
     * @param timestamp the time at which the data was generated, in milliseconds
     * @param label the type or category of the generated data
     * @param data the generated data value
     */
    void output(int patientId, long timestamp, String label, String data);
}
