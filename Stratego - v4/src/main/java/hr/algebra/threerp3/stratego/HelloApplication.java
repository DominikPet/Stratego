package hr.algebra.threerp3.stratego;

import hr.algebra.threerp3.stratego.chat.ChatRemoteService;
import hr.algebra.threerp3.stratego.model.ConfKey;
import hr.algebra.threerp3.stratego.model.GameState;
import hr.algebra.threerp3.stratego.model.RoleName;
import hr.algebra.threerp3.stratego.utils.BoardUtils;
import hr.algebra.threerp3.stratego.utils.ChatUtils;
import hr.algebra.threerp3.stratego.utils.ConfigurationReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class HelloApplication extends Application {

    public static Boolean clientConnected = false;

    public static ChatRemoteService chatRemoteService;

    public static RoleName loggedInRoleName;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 750);
        stage.setTitle(loggedInRoleName.name());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        String inputRoleName = args[0];
        loggedInRoleName = RoleName.CLIENT;

        for (RoleName rn : RoleName.values()) {
            if (rn.name().equals(inputRoleName)) {
                loggedInRoleName = rn;
                break;
            }
        }

        new Thread(Application::launch).start();

        if (loggedInRoleName.equals(RoleName.SERVER)) {
            ChatUtils.startChatService();
            acceptRequestsAsServer();
        } else if (loggedInRoleName.equals(RoleName.CLIENT)) {
            clientConnected = true;
            ChatUtils.startChatClient();
            acceptRequestsAsClient();
        }
    }


    public static void openNewStage() throws Exception {
        // Load the FXML file for the new stage
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("rulesScreen.fxml"));
        Parent root = loader.load();

        // Create a new stage
        Stage newStage = new Stage();
        newStage.setTitle("Rules");
        newStage.setResizable(false);
        newStage.setScene(new Scene(root));

        // Show the new stage
        newStage.show();
    }

    private static void acceptRequestsAsServer() {

        Integer serverPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfKey.SERVER_PORT);

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.err.println("Server listening on port: " + serverSocket.getLocalPort());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Client connected from port: " + clientSocket.getPort());
                Platform.runLater(() -> processSerializableClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptRequestsAsClient() {

        Integer clientPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfKey.CLIENT_PORT);

        try (ServerSocket serverSocket = new ServerSocket(clientPort)) {
            System.err.println("Client listening on port: " + serverSocket.getLocalPort());
            HelloController.gameOver = true;
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.println("Server connected from port: " + clientSocket.getPort());
                Platform.runLater(() -> processSerializableClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());) {
            GameState gameState = (GameState) ois.readObject();
            HelloController.board = gameState.getGameBoardState();
            BoardUtils.turn = gameState.getTurn();
            Platform.runLater(() -> BoardUtils.drawBoard(HelloController.board, HelloController.staticController.gridBoard));
            System.out.println("Game state received");

            if (gameState.getWinner().equals(false)) HelloController.gameOver = false;
            else HelloController.gameOver = true; //if there is no winner, the player can move

            if (!Objects.equals(gameState.getLabel(), "")) {
                HelloController.staticController.attackLabel.setText(gameState.getLabel());
            }
            oos.writeObject("Game state received confirmation");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}