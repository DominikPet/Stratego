package hr.algebra.threerp3.stratego.utils;

import hr.algebra.threerp3.stratego.HelloApplication;
import hr.algebra.threerp3.stratego.HelloController;
import hr.algebra.threerp3.stratego.model.*;
import hr.algebra.threerp3.stratego.thread.SaveNewGameMoveThread;

import java.time.LocalDateTime;

import static hr.algebra.threerp3.stratego.HelloController.*;
import static hr.algebra.threerp3.stratego.utils.BoardUtils.*;

public class ButtonLogicUtils {
    public static Boolean winnerInGame = false;
    public static Boolean firstGameMoveSet = false;


    public static void unMarkAvailableAndTargetedButtons() {
        for (int cols = 0; cols < NUM_OF_COLS; cols++) {
            for (int rows = 0; rows < NUM_OF_ROWS; rows++) {
                if (board[rows][cols].role.equals(Roles.SELECTED))
                    board[rows][cols] = createBlankFigure();
                if (board[rows][cols].targeted) board[rows][cols].targeted = false;
            }
        }
    }

    public static void markAvailableButtons(int row, int col) {
        Figure figure = board[row][col];
        if (!figure.role.isEmpty()) {
            System.out.println("Marking for c:" + col + " r:" + row);

            switch (figure.role) {
                case Roles.SPY -> {
                    markAvailableButtonsInDirection(row, col, -1, 0);
                    markAvailableButtonsInDirection(row, col, 1, 0);
                    markAvailableButtonsInDirection(row, col, 0, -1);
                    markAvailableButtonsInDirection(row, col, 0, 1);
                    break;
                }
                case Roles.BOMB, Roles.FLAG -> {
                    break;
                }
                // ... (rest of the cases)
                default -> {
                    checkAndMarkCell(row, col, 0, -1);
                    checkAndMarkCell(row, col, 0, 1);
                    checkAndMarkCell(row, col, -1, 0);
                    checkAndMarkCell(row, col, 1, 0);
                }
            }
        }
    }

    private static void markAvailableButtonsInDirection(int row, int col, int rowDirection, int colDirection) {
        for (int i = 1; i < 10; i++) {
            int newRow = row + i * rowDirection;
            int newCol = col + i * colDirection;

            if (newRow < 0 || newRow >= 10 || newCol < 0 || newCol >= 10) {
                break;
            }

            Figure currentCell = board[newRow][newCol];

            if (!currentCell.role.isEmpty() && !currentCell.color.equals(turn)) {
                currentCell.targeted = true;
                break;
            } else if (currentCell.role.isEmpty()) {
                currentCell.role = Roles.SELECTED;
                System.out.println("I've marked as selected col:" + newCol + " row:" + newRow);
            } else {
                break;
            }
        }
    }

    private static void checkAndMarkCell(int row, int col, int rowDirection, int colDirection) {
        int newRow = row + rowDirection;
        int newCol = col + colDirection;

        if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
            Figure currentCell = board[newRow][newCol];

            if (currentCell.role.isEmpty()) {
                currentCell.role = Roles.SELECTED;
                System.out.println("I've marked as selected col:" + newCol + " row:" + newRow);
            } else if (!currentCell.color.equals(turn)) {
                currentCell.targeted = true;
            }
        }
    }


    public static void attack(int row, int col) {
        Figure attacker = board[ButtonSelected.selectedRow][ButtonSelected.selectedColumn];
        Figure defender = board[row][col];
        setLastMove(row, col);
        if (attacker.rank < defender.rank && !defender.role.equals(Roles.BOMB) && !defender.role.equals(Roles.FLAG)) {
            switchPlaces(row, col);
            switchTurn();
            staticController.attackLabel.setText(attacker.role + " defeated " + defender.role);
        } else if (defender.role.equals(Roles.FLAG)) {
            staticController.attackLabel.setText(turn.toString() + " wins!");
            gameOver = true;
            winnerInGame = true;
            sendGameState();
        } else if (defender.role.equals(Roles.BOMB)) {
            if (attacker.role.equals(Roles.MINER)) {
                switchPlaces(row, col);
                switchTurn();
                staticController.attackLabel.setText(Roles.MINER + " has defused the " + Roles.BOMB);
            } else {
                board[ButtonSelected.selectedRow][ButtonSelected.selectedColumn] = createBlankFigure();
                staticController.attackLabel.setText(attacker.role + " was blown up!");
            }
        } else if (defender.rank < attacker.rank) {
            if (defender.role.equals(Roles.MARSHAL) && attacker.role.equals(Roles.SPY)) {
                switchPlaces(row, col);
                switchTurn();
                staticController.attackLabel.setText(attacker.role + " defeated " + defender.role);
            } else {
                board[ButtonSelected.selectedRow][ButtonSelected.selectedColumn] = createBlankFigure();
                staticController.attackLabel.setText(defender.role + " defeated " + attacker.role);
            }
        } else {
            board[ButtonSelected.selectedRow][ButtonSelected.selectedColumn] = createBlankFigure();
            board[row][col] = createBlankFigure();
            staticController.attackLabel.setText("Both" + attacker.role + " and " + defender.role + " were destroyed");
        }
        unMarkAvailableAndTargetedButtons();
        drawBoard(board, staticController.gridBoard);
        switchTurn();
        sendGameState();
    }

    public static void switchPlaces(int row, int col) {
        int selRow = BoardUtils.ButtonSelected.selectedRow;
        int selCol = BoardUtils.ButtonSelected.selectedColumn;
        setLastMove(row, col);
        Figure selectedFigure = board[selRow][selCol];
        board[selRow][selCol] = createBlankFigure();
        board[row][col] = selectedFigure;
        unMarkAvailableAndTargetedButtons();
        drawBoard(board, staticController.gridBoard);
        switchTurn();
        sendGameState();
    }

    private static void setLastMove(int row, int col) {
        GameMove newGameMove = new GameMove(ButtonSelected.selectedColumn, ButtonSelected.selectedRow, col, row, LocalDateTime.now());
        XmlUtils.saveGameMove(newGameMove);
        SaveNewGameMoveThread saveNewGameMoveThread = new SaveNewGameMoveThread(newGameMove);
        firstGameMoveSet = true;
        Thread starterThread = new Thread(saveNewGameMoveThread);
        starterThread.start();
    }

    public static void handleButtonClick(int row, int col) {
        if (HelloController.gameOver) return;
        if (HelloController.board[row][col].role.equals(Roles.SELECTED)) {
            switchPlaces(row, col);
            if (!HelloApplication.loggedInRoleName.equals(RoleName.SINGLE_PLAYER)) gameOver = true;
            return;
        }
        if (HelloController.board[row][col].targeted) {
            attack(row, col);
            if (!HelloApplication.loggedInRoleName.equals(RoleName.SINGLE_PLAYER)) gameOver = true;
            return;
        }
        if (!turn.equals(HelloController.board[row][col].color) || HelloController.board[row][col].role.isEmpty()) {
            return;
        }
        System.out.println("Button Clicked! Row: " + row + ", Column: " + col + " " + HelloController.board[row][col].role);
        if ((row != ButtonSelected.selectedRow || col != ButtonSelected.selectedColumn) && ButtonSelected.selectedColumn != 100)
            unMarkAvailableAndTargetedButtons(); //selectedColum is 100 when no button was clicked this turn
        ButtonLogicUtils.markAvailableButtons(row, col);
        ButtonSelected.selectedRow = row;
        ButtonSelected.selectedColumn = col;
        drawBoard(board, staticController.gridBoard);
        if (!HelloApplication.loggedInRoleName.equals(RoleName.SINGLE_PLAYER)) sendGameState();
    }

    public static void sendGameState() {
        if (HelloApplication.loggedInRoleName.equals(RoleName.SINGLE_PLAYER)) return;
        GameState gameState = new GameState(BoardUtils.turn, board, staticController.attackLabel.getText(), winnerInGame);
        if (HelloApplication.loggedInRoleName.equals(RoleName.CLIENT)) {
            NetworkingUtils.sendGameStateToServer(gameState);
        } else {
            NetworkingUtils.sendGameStateToClient(gameState);
        }
    }
}
