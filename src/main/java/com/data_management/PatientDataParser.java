package com.data_management;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses patient-data messages produced by the simulator output strategies.
 *
 * <p>The WebSocket output format used by {@code WebSocketOutputStrategy} is:</p>
 *
 * <pre>
 * patientId,timestamp,label,data
 * </pre>
 *
 * <p>Example:</p>
 *
 * <pre>
 * 2,1714376789050,Saturation,91%
 * </pre>
 *
 * <p>The file output format is also supported so both batch and real-time readers can reuse the
 * same conversion rules. Invalid or corrupted messages return {@link Optional#empty()} instead of
 * throwing an exception. This is important for real-time streams because one bad message should not
 * stop the client.</p>
 */
public class PatientDataParser {
    private static final Pattern FILE_LINE_PATTERN = Pattern.compile(
            "Patient ID: (\\d+), Timestamp: (\\d+), Label: ([^,]+), Data: (.+)");

    /**
     * Parses one WebSocket message.
     *
     * @param message raw message in {@code patientId,timestamp,label,data} format
     * @return parsed message, or empty if the message is invalid
     */
    public Optional<PatientDataMessage> parseWebSocketMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return Optional.empty();
        }

        String[] parts = message.split(",", 4);
        if (parts.length != 4) {
            return Optional.empty();
        }

        return parseParts(parts[0], parts[1], parts[2], parts[3]);
    }

    /**
     * Parses one file-output line.
     *
     * @param line raw line written by FileOutputStrategy
     * @return parsed message, or empty if the line is invalid
     */
    public Optional<PatientDataMessage> parseFileLine(String line) {
        if (line == null) {
            return Optional.empty();
        }

        Matcher matcher = FILE_LINE_PATTERN.matcher(line.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        return parseParts(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
    }

    private Optional<PatientDataMessage> parseParts(
            String patientIdText,
            String timestampText,
            String labelText,
            String valueText) {
        try {
            int patientId = Integer.parseInt(patientIdText.trim());
            long timestamp = Long.parseLong(timestampText.trim());
            String label = labelText.trim();
            double measurementValue = parseMeasurementValue(valueText.trim());

            if (patientId <= 0 || timestamp < 0 || label.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(new PatientDataMessage(patientId, timestamp, label, measurementValue));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    /**
     * Converts simulator values into numeric values used by DataStorage and alert strategies.
     *
     * @param rawValue value from the simulator message
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