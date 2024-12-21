package org.example;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
            String message;
            while ((message = input.readLine()) != null) {
                out.println(message);  // Send message to server
                System.out.println("Server: " + in.readLine());  // Display server response
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
