package com.cardio_generator;

import com.cardio_generator.generators.AlertGenerator;
import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton that starts and coordinates the health data simulation.
 */
public class HealthDataSimulator {
    private static final HealthDataSimulator INSTANCE = new HealthDataSimulator();

    private int patientCount = 50;
    private ScheduledExecutorService scheduler;
    private OutputStrategy outputStrategy = new ConsoleOutputStrategy();
    private final Random random = new Random();

    /** Private constructor because the simulator is a singleton. */
    private HealthDataSimulator() {
    }

    /**
     * Returns the single simulator instance.
     *
     * @return global HealthDataSimulator instance
     */
    public static HealthDataSimulator getInstance() {
        return INSTANCE;
    }

    /**
     * Entry point for the simulator application.
     *
     * @param args command-line arguments for patient count and output strategy
     * @throws IOException if creating the configured output directory fails
     */
    public static void main(String[] args) throws IOException {
        HealthDataSimulator.getInstance().run(args);
    }

    /**
     * Runs the simulation using this singleton instance.
     *
     * @param args command-line arguments
     * @throws IOException if output setup fails
     */
    public void run(String[] args) throws IOException {
        parseArguments(args);
        scheduler = Executors.newScheduledThreadPool(patientCount * 4);
        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds);
        scheduleTasksForPatients(patientIds);
    }

    /** Stops scheduled tasks. Useful for controlled shutdowns and tests. */
    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    /** Resets configurable simulator state. Useful for tests. */
    public void resetForTesting() {
        shutdown();
        patientCount = 50;
        outputStrategy = new ConsoleOutputStrategy();
    }

    private void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Error: Invalid number of patients. Using default value: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        configureOutputStrategy(args[++i]);
                    }
                    break;
                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }

    private void configureOutputStrategy(String outputArg) throws IOException {
        if (outputArg.equals("console")) {
            outputStrategy = new ConsoleOutputStrategy();
        } else if (outputArg.startsWith("file:")) {
            String baseDirectory = outputArg.substring(5);
            Path outputPath = Paths.get(baseDirectory);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
            outputStrategy = new FileOutputStrategy(baseDirectory);
        } else if (outputArg.startsWith("websocket:")) {
            try {
                int port = Integer.parseInt(outputArg.substring(10));
                outputStrategy = new WebSocketOutputStrategy(port);
                System.out.println("WebSocket output will be on port: " + port);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port for WebSocket output. Please specify a valid port number.");
            }
        } else if (outputArg.startsWith("tcp:")) {
            try {
                int port = Integer.parseInt(outputArg.substring(4));
                outputStrategy = new TcpOutputStrategy(port);
                System.out.println("TCP socket output will be on port: " + port);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port for TCP output. Please specify a valid port number.");
            }
        } else {
            System.err.println("Unknown output type. Using default (console).");
        }
    }

    private void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("Options:");
        System.out.println("  -h                       Show help and exit.");
        System.out.println("  --patient-count <count>  Specify the number of patients to simulate data for (default: 50).");
        System.out.println("  --output <type>          Define the output method: console, file:<directory>, websocket:<port>, tcp:<port>.");
    }

    private List<Integer> initializePatientIds(int patientCount) {
        List<Integer> patientIds = new ArrayList<>();
        for (int i = 1; i <= patientCount; i++) {
            patientIds.add(i);
        }
        return patientIds;
    }

    private void scheduleTasksForPatients(List<Integer> patientIds) {
        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount);
        AlertGenerator alertGenerator = new AlertGenerator(patientCount);

        for (int patientId : patientIds) {
            scheduleTask(() -> ecgDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodSaturationDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodPressureDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> bloodLevelsDataGenerator.generate(patientId, outputStrategy), 2, TimeUnit.MINUTES);
            scheduleTask(() -> alertGenerator.generate(patientId, outputStrategy), 20, TimeUnit.SECONDS);
        }
    }

    private void scheduleTask(Runnable task, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, timeUnit);
    }
}