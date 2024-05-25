package hr.algebra.threerp3.stratego.utils;

import hr.algebra.threerp3.stratego.HelloController;
import hr.algebra.threerp3.stratego.model.Colors;
import hr.algebra.threerp3.stratego.model.Figure;
import hr.algebra.threerp3.stratego.model.GameState;
import javafx.scene.control.Alert;

public class SaveLoadUtils {

    public static void saveGame() {
        FileUtils.saveGame(HelloController.board, BoardUtils.turn, HelloController.NUM_OF_ROWS, HelloController.NUM_OF_COLS);
        FileUtils.saveGame(HelloController.board, BoardUtils.turn, HelloController.NUM_OF_ROWS, HelloController.NUM_OF_COLS);
    }

    public static void loadGame() {

        GameState recoveredGameState = FileUtils.loadGame();

        if (recoveredGameState != null) {
            Figure[][] gameBoardState = recoveredGameState.getGameBoardState();

            for (int i = 0; i < HelloController.NUM_OF_ROWS; i++) {
                for (int j = 0; j < HelloController.NUM_OF_COLS; j++) {
                    Figure currentFigure = HelloController.board[i][j];
                    Figure savedFigure = gameBoardState[i][j];
                    if (savedFigure != null) {
                        currentFigure.role = savedFigure.role;
                        currentFigure.color = savedFigure.color;
                        currentFigure.rank = BoardUtils.getRank(currentFigure.role);
                    } else {
                        currentFigure.role = "";
                        currentFigure.color = Colors.Blank;
                    }
                }
            }

            BoardUtils.turn = recoveredGameState.getTurn();
            BoardUtils.drawBoard(HelloController.board, HelloController.staticController.gridBoard);

            DialogUtils.showDialog(Alert.AlertType.INFORMATION,
                    "Game loaded!", "Your game has been successfully loaded!");
        }
    }
}
