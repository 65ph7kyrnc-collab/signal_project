package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Sends generated patient data to a connected TCP client.
 *
 * <p>The strategy starts a TCP server on the given port and waits for one client connection
 * on a background thread. Once a client is connected, each output call sends a comma-separated
 * message containing the patient identifier, timestamp, label, and data value.</p>
 */
public class TcpOutputStrategy implements OutputStrategy {

    /** Server socket that listens for incoming TCP client connections. */
    private ServerSocket serverSocket;

    /** The currently connected TCP client socket. */
    private Socket clientSocket;

    /** Writer used to send formatted data messages to the connected client. */
    private PrintWriter out;

    /**
     * Starts a TCP server on the specified port and waits for a client connection.
     *
     * @param port the TCP port on which the server should listen
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends one generated data record to the connected TCP client, if a client is connected.
     *
     * @param patientId the identifier of the patient associated with the data
     * @param timestamp the time at which the data was generated, in milliseconds
     * @param label the type or category of the generated data
     * @param data the generated data value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
