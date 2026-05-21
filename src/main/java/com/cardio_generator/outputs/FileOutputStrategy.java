package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Writes generated patient data to text files grouped by data label.
 *
 * <p>Each unique label is mapped to one text file in the configured base directory. Data is
 * appended so that previous readings are preserved.</p>
 */
//Name UpperCamelCase
public class FileOutputStrategy implements OutputStrategy {
    /** Base directory where output files are created. */
    private final String baseDirectory;

    /** Maps data labels to the file paths used for those labels. */
    // lowerCamelCase
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Creates a file output strategy that writes files under the specified directory.
     *
     * @param baseDirectory the directory where output files should be stored
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Appends one generated patient data record to the file for its label.
     *
     * @param patientId the identifier of the patient associated with the data
     * @param timestamp the time at which the data was generated, in milliseconds
     * @param label the type or category of the generated data
     * @param data the generated data value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        //lowerCamelCase + method in different Google... format
        String filePath =
        fileMap.computeIfAbsent(
                label,
                key -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } 
        //changed to IOException
        catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}
