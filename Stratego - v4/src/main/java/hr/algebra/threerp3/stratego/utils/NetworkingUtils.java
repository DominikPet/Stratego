package hr.algebra.threerp3.stratego.utils;

import hr.algebra.threerp3.stratego.model.ConfKey;
import hr.algebra.threerp3.stratego.model.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkingUtils {
    public static void sendGameStateToServer(GameState gameState) {

        Integer serverPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfKey.SERVER_PORT);
        String host = ConfigurationReader.readStringConfigurationValueForKey(ConfKey.HOST);

        try (Socket clientSocket = new Socket(host, serverPort)) {
            System.err.println("Server is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            sendSerializableRequest(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendGameStateToClient(GameState gameState) {

        Integer clientPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfKey.CLIENT_PORT);
        String host = ConfigurationReader.readStringConfigurationValueForKey(ConfKey.HOST);

        try (Socket clientSocket = new Socket(host, clientPort)) {
            System.err.println("Client is connecting to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            sendSerializableRequest(clientSocket, gameState);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendSerializableRequest(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        System.out.println("Game state sent to server");
        String confirmationMessage = (String) ois.readObject();
        System.out.println("Confirmation message: " + confirmationMessage);
    }
}
