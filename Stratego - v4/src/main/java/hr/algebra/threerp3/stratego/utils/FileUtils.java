package hr.algebra.threerp3.stratego.utils;

import hr.algebra.threerp3.stratego.model.Colors;
import hr.algebra.threerp3.stratego.model.Figure;
import hr.algebra.threerp3.stratego.model.GameState;
import javafx.scene.control.Alert;

import java.io.*;

public class FileUtils {

    public static final String GAME_SAVE_FILE_NAME = "savedGame.dat";

    public static void saveGame(Figure[][] gameBoard,
                                Colors turn,
                                Integer numberOfRows,
                                Integer numberOfColumns) {
        GameState gameStateToBeSaved = GameStateUtils.createGameState(turn, gameBoard);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FileUtils.GAME_SAVE_FILE_NAME))) {
            oos.writeObject(gameStateToBeSaved);
            DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                    "Game saved!", "Your game has been successfully saved!");
        } catch (IOException e) {
            DialogUtils.showDialog(Alert.AlertType.ERROR,
                    "Game not saved!", "Your game has not been successfully saved!");
            throw new RuntimeException(e);
        }
    }

    public static GameState loadGame() {
        GameState recoveredGameState;

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FileUtils.GAME_SAVE_FILE_NAME))) {
            recoveredGameState = (GameState) ois.readObject();
        } catch (Exception ex) {
            DialogUtils.showDialog(Alert.AlertType.ERROR,
                    "Game not loaded!", "Your game has not been successfully loaded!");
            throw new RuntimeException(ex);
        }

        return recoveredGameState;
    }

}
