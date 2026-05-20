package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the common contract for classes that generate simulated patient data.
 */
public interface PatientDataGenerator {
    /**
     * Generates one data sample for the given patient and sends it to an output strategy.
     *
     * @param patientId the identifier of the patient for whom data is generated
     * @param outputStrategy the strategy used to output the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
