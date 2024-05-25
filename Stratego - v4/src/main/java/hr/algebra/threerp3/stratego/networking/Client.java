package hr.algebra.threerp3.stratego.networking;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        sendRequest();
    }

    private static void sendRequest() {
        // Closing socket will also close the socket's InputStream and OutputStream.
        try (Socket clientSocket = new Socket(Server.HOST, Server.PORT)) {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            //sendPrimitiveRequest(clientSocket);
            sendSerializableRequest(clientSocket);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendPrimitiveRequest(Socket clientSocket) throws IOException {
        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

        System.out.print("Insert sentence: ");
        dos.writeUTF(readSentence(System.in));
        System.out.println("Number of vowels: " + dis.readInt());
    }

    private static String readSentence(InputStream in) {
        try (Scanner scanner = new Scanner(in)) {
            return scanner.nextLine();
        }
    }

    private static void sendSerializableRequest(Socket client) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(new Country("Croatia", 1));
        System.out.println("Moved to: " + (Country) ois.readObject());
    }
}