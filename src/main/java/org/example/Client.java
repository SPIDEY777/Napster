package org.example;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Thread for receiving messages from the server
            Thread receiverThread = new Thread(new Receiver(in));
            receiverThread.start();

            // Thread for sending messages to the server
            Thread senderThread = new Thread(new Sender(out, userInput));
            senderThread.start();

            // Wait for both threads to finish
            receiverThread.join();
            senderThread.join();

        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Receiver thread to read messages from the server
    static class Receiver implements Runnable {
        private final BufferedReader in;

        public Receiver(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message); // Display message directly without "Server:" prefix
                }
            } catch (IOException e) {
                System.out.println("Error in receiving message: " + e.getMessage());
            }
        }
    }

    // Sender thread to send messages to the server
    static class Sender implements Runnable {
        private final PrintWriter out;
        private final BufferedReader userInput;

        public Sender(PrintWriter out, BufferedReader userInput) {
            this.out = out;
            this.userInput = userInput;
        }

        @Override
        public void run() {
            try {
                String userMessage;
                while ((userMessage = userInput.readLine()) != null) {
                    out.println(userMessage); // Send message directly without "Client:" prefix
                }
            } catch (IOException e) {
                System.out.println("Error in sending message: " + e.getMessage());
            }
        }
    }
}
