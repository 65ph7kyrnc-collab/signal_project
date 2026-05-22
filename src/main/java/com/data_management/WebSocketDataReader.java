package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Real-time {@link DataReader} implementation that receives patient data from a WebSocket server.
 *
 * <p>The simulator starts a WebSocket server when run with {@code --output websocket:<port>}.
 * This reader connects to that server, listens continuously, parses every incoming message, and
 * appends valid records to {@link DataStorage}. Malformed messages are ignored and logged so the
 * real-time stream can continue running.</p>
 */
public class WebSocketDataReader implements DataReader {
    private static final int DEFAULT_CONNECTION_TIMEOUT_SECONDS = 5;

    private final URI serverUri;
    private final PatientDataParser parser;
    private WebSocketClient client;
    private DataStorage dataStorage;
    private volatile boolean connected;

    /**
     * Creates a WebSocket reader for the given URI.
     *
     * @param serverUri WebSocket URI, for example {@code ws://localhost:8080}
     */
    public WebSocketDataReader(URI serverUri) {
        this(serverUri, new PatientDataParser());
    }

    /**
     * Creates a WebSocket reader for the given URI string.
     *
     * @param serverUri WebSocket URI string, for example {@code ws://localhost:8080}
     * @throws URISyntaxException if the URI string is invalid
     */
    public WebSocketDataReader(String serverUri) throws URISyntaxException {
        this(new URI(serverUri));
    }

    WebSocketDataReader(URI serverUri, PatientDataParser parser) {
        if (serverUri == null) {
            throw new IllegalArgumentException("serverUri must not be null");
        }
        this.serverUri = serverUri;
        this.parser = parser;
    }

    /**
     * Connects to the WebSocket server and starts listening for incoming data.
     *
     * <p>This method returns after the connection is established. Incoming messages continue to be
     * processed asynchronously by the WebSocket callback thread until {@link #stopReading()} is
     * called or the server closes the connection.</p>
     *
     * @param dataStorage storage receiving valid records
     * @throws IOException if the client cannot connect to the server
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        startReading(dataStorage);
    }

    /**
     * Starts the real-time WebSocket stream.
     *
     * @param dataStorage storage receiving valid records
     * @throws IOException if the client cannot connect to the server
     */
    @Override
    public void startReading(DataStorage dataStorage) throws IOException {
        if (dataStorage == null) {
            throw new IllegalArgumentException("dataStorage must not be null");
        }

        this.dataStorage = dataStorage;
        this.client = createClient();

        try {
            boolean connectionOpened = client.connectBlocking(
                    DEFAULT_CONNECTION_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS);
            if (!connectionOpened) {
                throw new IOException("Could not connect to WebSocket server: " + serverUri);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while connecting to WebSocket server: " + serverUri, exception);
        }
    }

    /** Stops the WebSocket connection if it is open. */
    @Override
    public void stopReading() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Parses and stores a single incoming message.
     *
     * <p>This method has package visibility so the parsing/storage behavior can be unit tested
     * without opening a real network connection.</p>
     *
     * @param message raw WebSocket message
     * @param targetStorage storage receiving the parsed record
     * @return true if the message was valid and stored, false otherwise
     */
    boolean handleMessage(String message, DataStorage targetStorage) {
        Optional<PatientDataMessage> parsedMessage = parser.parseWebSocketMessage(message);
        if (parsedMessage.isEmpty()) {
            System.err.println("Ignoring malformed WebSocket message: " + message);
            return false;
        }

        PatientDataMessage patientDataMessage = parsedMessage.get();
        targetStorage.addPatientData(
                patientDataMessage.getPatientId(),
                patientDataMessage.getMeasurementValue(),
                patientDataMessage.getLabel(),
                patientDataMessage.getTimestamp());
        return true;
    }

    /**
     * @return true if the WebSocket connection is currently open
     */
    public boolean isConnected() {
        return connected;
    }

    private WebSocketClient createClient() {
        return new WebSocketClient(serverUri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                connected = true;
                System.out.println("Connected to WebSocket server: " + serverUri);
            }

            @Override
            public void onMessage(String message) {
                handleMessage(message, dataStorage);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                connected = false;
                System.out.println("WebSocket connection closed: " + reason + " (code " + code + ")");
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("WebSocket error: " + exception.getMessage());
            }
        };
    }
}