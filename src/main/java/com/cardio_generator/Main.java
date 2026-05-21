package com.cardio_generator;

import com.data_management.DataStorage;
import java.util.Arrays;

/**
 * Dispatches command-line execution to either the simulator or the data-storage reader.
 *
 * <p>Added for Project Part 3 because the assignment asks the Maven configuration to allow
 * running both {@link HealthDataSimulator} and {@link DataStorage}. The first argument decides
 * which main class is used:</p>
 *
 * <ul>
 *   <li>{@code DataStorage}: runs {@code DataStorage.main(...)} with the remaining arguments.</li>
 *   <li>anything else: runs {@code HealthDataSimulator.main(...)} with all arguments.</li>
 * </ul>
 */
public final class Main {
    /** Utility class: no instances needed. */
    private Main() {
    }

    /**
     * Runs DataStorage or HealthDataSimulator based on the first argument.
     *
     * @param args command-line arguments
     * @throws Exception if the selected main method fails
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "DataStorage".equalsIgnoreCase(args[0])) {
            String[] dataStorageArgs = Arrays.copyOfRange(args, 1, args.length);
            DataStorage.main(dataStorageArgs);
            return;
        }

        HealthDataSimulator.main(args);
    }
}