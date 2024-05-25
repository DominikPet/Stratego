package hr.algebra.threerp3.stratego.networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final String HOST = "localhost";
    public static final int PORT = 1989;

    public static void main(String[] args) {
        acceptRequests();
    }

    private static void acceptRequests() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                // outer try catch blocks cannot handle the anonymous implementations
                //new Thread(() ->  processPrimitiveClient(clientSocket)).start();
                new Thread(() -> processSerializableClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processPrimitiveClient(Socket clientSocket) {
        // we have to manually close dis and dos since clientSocket is not in try with resources
        // closing the streams closes the socket!
        try (DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream())) {

            String message = dis.readUTF();
            System.out.println("Server received: " + message);
            dos.writeInt(countVowels(message));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int countVowels(String message) {
        return message.toLowerCase().replaceAll("[^aeiou]", "").length();
    }

    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());) {
            Country country = (Country) ois.readObject();
            System.out.println("The client lives in " + country);
            country.setName("Ireland");
            oos.writeObject(country);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
