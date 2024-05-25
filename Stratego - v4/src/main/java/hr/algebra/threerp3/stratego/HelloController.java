package hr.algebra.threerp3.stratego;

import hr.algebra.threerp3.stratego.model.Colors;
import hr.algebra.threerp3.stratego.model.Figure;
import hr.algebra.threerp3.stratego.model.GameMove;
import hr.algebra.threerp3.stratego.model.RoleName;
import hr.algebra.threerp3.stratego.thread.GameMoveThread;
import hr.algebra.threerp3.stratego.thread.GetLastGameMoveThread;
import hr.algebra.threerp3.stratego.utils.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloController {
    @FXML
    public GridPane gridBoard;

    @FXML
    public Label turnLabel;

    @FXML
    public TextArea chatTextArea;

    @FXML
    public TextField chatMsgTextField;
    @FXML
    public Button sendBtn;

    @FXML
    public Label attackLabel;

    @FXML
    public Label theLastGameMoveLabel;

    public static boolean gameOver = false;

    public static int NUM_OF_ROWS = 10;
    public static int NUM_OF_COLS = 10;

    public static HelloController staticController;

    public static Figure[][] board = new Figure[NUM_OF_ROWS][NUM_OF_COLS];

    @FXML
    private void initialize() {
        setController();
        if (!HelloApplication.loggedInRoleName.equals(RoleName.SINGLE_PLAYER)) setComponentPreferences();
        initializeBoard();
        startGameMoveThread();
    }

    private void deleteFiles() {
        deleteFileContents(GameMoveThread.GAME_MOVES_FILE_NAME);
        deleteFileContents(XmlUtils.FILENAME);
        deleteFileContents(XmlUtils.BOARDFILENAME);
    }

    public void replayGame() {
        List<GameMove> gameMovesList = XmlUtils.getAllGameMoves();
        board = XmlUtils.getBoard();
        if (gameMovesList.isEmpty()) return;
        AtomicInteger i = new AtomicInteger(0);
        final Timeline replayer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GameMove gm = gameMovesList.get(i.get());
                ButtonLogicUtils.handleButtonClick(gm.getStartPositionColInt(), gm.getStartPositionRowInt());
                ButtonLogicUtils.handleButtonClick(gm.getEndPositionColInt(), gm.getEndPositionRowInt());
                i.set(i.get() + 1);
            }
        }), new KeyFrame(Duration.seconds(1)));
        replayer.setCycleCount(gameMovesList.size());
        replayer.play();
        System.out.println("Finished replaying");
    }

    public static void deleteFileContents(String fileName) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                FileWriter writer = new FileWriter(file, false);
                writer.close();
                System.out.println("Contents of " + fileName + " have been deleted successfully.");
            } else {
                System.out.println("File " + fileName + " does not exist.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the contents of the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void startGameMoveThread() {
        GetLastGameMoveThread getLastGameMoveThread = new GetLastGameMoveThread(staticController.theLastGameMoveLabel);
        Thread starter = new Thread(getLastGameMoveThread);
        starter.start();
    }

    private void setComponentPreferences() {
        chatTextArea.setEditable(false);
        //theLastGameMoveLabel.setText("The last game move: ");
        chatMsgTextField.setOnAction(e -> {
            if (!chatMsgTextField.getText().isEmpty())
                ChatUtils.sendChatMessage();
        });
        chatTextArea.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                chatTextArea.setScrollTop(Double.MAX_VALUE);
            }
        });
        if (HelloApplication.clientConnected.equals(true)) gridBoard.getChildren().clear();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> ChatUtils.refreshChatMessages()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
    }

    public void openRulesWindows() throws Exception {
        HelloApplication.openNewStage();
    }

    private void setController() {
        staticController = this;
    }

    private void initializeBoard() {
        List<Figure> redFigures = BoardUtils.createFigures(Colors.Red);
        List<Figure> blueFigures = BoardUtils.createFigures(Colors.Blue);

        Collections.shuffle(redFigures);
        Collections.shuffle(blueFigures);

        gridBoard.getChildren().clear();

        for (int row = 0; row < NUM_OF_ROWS; row++) {
            for (int col = 0; col < NUM_OF_COLS; col++) {
                Figure figure;
                if (col < 4 && !redFigures.isEmpty()) {
                    figure = redFigures.remove(0);
                } else if (col >= 6 && !blueFigures.isEmpty()) {
                    figure = blueFigures.remove(0);
                } else {
                    figure = BoardUtils.createBlankFigure();
                }
                board[row][col] = figure;
            }
        }
        BoardUtils.drawBoard(board, gridBoard);
        XmlUtils.saveBoard(board);
    }

    public void generateDocumentationOnClick() {
        DocumentationUtils.generateDocumentation();
        DialogUtils.showDialog(Alert.AlertType.INFORMATION, "Documentation", "Generated!");
    }

    public void newGame() {
        deleteFiles();
        resetGame();
    }

    public void resetGame() {
        BoardUtils.turn = Colors.Red;
        turnLabel.setText("Turn: " + BoardUtils.turn.toString());
        attackLabel.setText("");
        ButtonLogicUtils.winnerInGame = false;
        gameOver = false;
        ButtonLogicUtils.firstGameMoveSet = false;
        initializeBoard();
        ButtonLogicUtils.sendGameState();
    }
    public void saveGame() {
        SaveLoadUtils.saveGame();
    }

    public void loadGame() {
        SaveLoadUtils.loadGame();
        turnLabel.setText("Turn: " + BoardUtils.turn.toString());
    }

    public void sendMessageOnClick() {
        ChatUtils.sendChatMessage();
    }
}