package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alerts.AlertGenerator;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.util.List;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Integration tests for WebSocketDataReader, DataStorage, and alert generation. */
class WebSocketIntegrationTest {
    private TestWebSocketServer server;
    private WebSocketDataReader reader;

    @BeforeEach
    void clearStorage() {
        DataStorage.getInstance().clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (reader != null) {
            reader.stopReading();
        }
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void webSocketReaderReceivesStoresAndSupportsAlertEvaluation() throws Exception {
        int port = findFreePort();
        server = new TestWebSocketServer(port);
        server.start();

        DataStorage storage = DataStorage.getInstance();
        reader = new WebSocketDataReader(new URI("ws://localhost:" + port));
        reader.startReading(storage);

        server.broadcast("1,1000,SystolicPressure,85");
        server.broadcast("1,1001,Saturation,91%");
        Thread.sleep(300L);

        List<PatientRecord> records = storage.getRecords(1, 0L, 2000L);
        assertEquals(2, records.size());

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        alertGenerator.evaluateAllPatients();

        assertTrue(alertGenerator.getTriggeredAlerts().size() > 0);
    }

    private static int findFreePort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static class TestWebSocketServer extends WebSocketServer {
        TestWebSocketServer(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            // No setup needed for this test server.
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            // No cleanup needed for this test server.
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // The reader does not send messages to the server in these tests.
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            throw new IllegalStateException(ex);
        }

        @Override
        public void onStart() {
            // Server started successfully.
        }
    }
}