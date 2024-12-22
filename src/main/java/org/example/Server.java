package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static final List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started...");
            while (true) {
                new ClientHandler(serverSocket.accept()).start(); // New client connection
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    // ClientHandler to manage client connections
    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out); // Add client output stream to list
                }

                String message;
                // Start receiver thread
                Thread receiverThread = new Thread(new Receiver(in));
                receiverThread.start();

                // Start sender thread (to send messages to client)
                Thread senderThread = new Thread(new Sender(out));
                senderThread.start();

                // Wait for both threads to finish
                receiverThread.join();
                senderThread.join();

            } catch (IOException | InterruptedException e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        }

        // Receiver thread to read messages from the client
        class Receiver implements Runnable {
            private final BufferedReader in;

            public Receiver(BufferedReader in) {
                this.in = in;
            }

            @Override
            public void run() {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        // Print received message on the server screen
                        System.out.println("Client: " + message);

                        // Broadcast message to all clients
                        synchronized (clientWriters) {
                            for (PrintWriter writer : clientWriters) {
                                writer.println("Client: " + message);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error in receiving message from client: " + e.getMessage());
                }
            }
        }

        // Sender thread to send messages to the client
        class Sender implements Runnable {
            private final PrintWriter out;

            public Sender(PrintWriter out) {
                this.out = out;
            }

            @Override
            public void run() {
                try {
                    BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                    String serverMessage;
                    while ((serverMessage = userInput.readLine()) != null) {
                        // Send message with "Server:" prefix
                        out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Error in sending message to client: " + e.getMessage());
                }
            }
        }
    }
}
