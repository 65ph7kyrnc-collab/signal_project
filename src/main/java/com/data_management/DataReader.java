package com.data_management;

import java.io.IOException;

/**
 * Contract for classes that read patient data from some source and store it in DataStorage.
 *
 * <p>Project Part 5 introduces real-time data streams. Implementations can either finish after
 * reading a static source, such as {@link FileDataReader}, or keep listening until
 * {@link #stopReading()} is called, such as {@link WebSocketDataReader}.</p>
 */
public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     *
     * <p>This method is kept for compatibility with the previous file-based project phases. For
     * streaming readers it starts the stream and then returns after the connection is open.</p>
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Starts reading from this reader. File-based readers may complete immediately; stream-based
     * readers may keep receiving data asynchronously.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if the data source cannot be opened
     */
    default void startReading(DataStorage dataStorage) throws IOException {
        readData(dataStorage);
    }

    /**
     * Stops reading from this source. Static readers do not need to do anything.
     */
    default void stopReading() {
        // Default no-op for readers that do not keep open resources.
    }
}
