package com.data_management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Reads file output created by {@code HealthDataSimulator --output file:<output_dir>}.
 *
 * <p>The simulator's FileOutputStrategy writes lines in this format:</p>
 *
 * <pre>
 * Patient ID: 1, Timestamp: 1714376789050, Label: ECG, Data: 0.42
 * </pre>
 *
 * <p>This reader parses those lines and forwards the data into {@link DataStorage}. Invalid lines
 * are ignored so one malformed line does not stop the entire import.</p>
 */
public class FileDataReader implements DataReader {
    /** Pattern matching the output line written by FileOutputStrategy. */
    private static final Pattern OUTPUT_LINE_PATTERN = Pattern.compile(
            "Patient ID: (\\d+), Timestamp: (\\d+), Label: ([^,]+), Data: (.+)");

    /** Directory containing simulator-generated text files. */
    private final Path inputDirectory;

    /**
     * Creates a reader for a simulator output directory.
     *
     * @param inputDirectory directory created by {@code --output file:<output_dir>}
     */
    public FileDataReader(String inputDirectory) {
        this.inputDirectory = Path.of(inputDirectory);
    }

    /**
     * Reads all text files in the configured directory into the provided storage object.
     *
     * @param dataStorage storage receiving the parsed records
     * @throws IOException if the directory does not exist or cannot be listed
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        if (!Files.isDirectory(inputDirectory)) {
            throw new IOException("Input directory does not exist: " + inputDirectory);
        }

        try (Stream<Path> paths = Files.list(inputDirectory)) {
            paths.filter(path -> path.toString().endsWith(".txt"))
                    .forEach(path -> readFile(path, dataStorage));
        }
    }

    /**
     * Reads one file and parses each line.
     *
     * @param path file to read
     * @param dataStorage storage receiving the parsed records
     */
    private void readFile(Path path, DataStorage dataStorage) {
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> parseLine(line, dataStorage));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file: " + path, e);
        }
    }

    /**
     * Parses one line and stores it if it is valid.
     *
     * @param line raw simulator output line
     * @param dataStorage storage receiving the parsed record
     */
    private void parseLine(String line, DataStorage dataStorage) {
        Matcher matcher = OUTPUT_LINE_PATTERN.matcher(line.trim());

        if (!matcher.matches()) {
            return;
        }

        try {
            int patientId = Integer.parseInt(matcher.group(1));
            long timestamp = Long.parseLong(matcher.group(2));
            String label = matcher.group(3).trim();
            double value = parseMeasurementValue(matcher.group(4).trim());

            dataStorage.addPatientData(patientId, value, label, timestamp);
        } catch (NumberFormatException ignored) {
            // Invalid numeric data is ignored so one bad line does not break the whole import.
        }
    }

    /**
     * Converts simulator values to numeric values for storage and alert checks.
     *
     * <p>Examples:</p>
     * <ul>
     *   <li>{@code "91%"} becomes {@code 91.0}</li>
     *   <li>{@code "triggered"} becomes {@code 1.0}</li>
     *   <li>{@code "resolved"} and {@code "untriggered"} become {@code 0.0}</li>
     * </ul>
     *
     * @param rawValue value string from the simulator output
     * @return numeric representation of the value
     */
    private double parseMeasurementValue(String rawValue) {
        String normalized = rawValue.replace("%", "").trim();

        if (normalized.equalsIgnoreCase("triggered")) {
            return 1.0;
        }

        if (normalized.equalsIgnoreCase("resolved")
                || normalized.equalsIgnoreCase("untriggered")) {
            return 0.0;
        }

        return Double.parseDouble(normalized);
    }
}