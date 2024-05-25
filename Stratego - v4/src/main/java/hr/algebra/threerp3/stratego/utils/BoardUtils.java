package hr.algebra.threerp3.stratego.utils;

import hr.algebra.threerp3.stratego.HelloApplication;
import hr.algebra.threerp3.stratego.HelloController;
import hr.algebra.threerp3.stratego.model.Colors;
import hr.algebra.threerp3.stratego.model.Figure;
import hr.algebra.threerp3.stratego.model.RoleName;
import hr.algebra.threerp3.stratego.model.Roles;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

import static hr.algebra.threerp3.stratego.utils.ButtonLogicUtils.handleButtonClick;

public class BoardUtils {

    private static final String hiddenName = "xxx";

    public static class ButtonSelected {
        public static int selectedRow;
        public static int selectedColumn = 100;
    }

    public static Colors turn = Colors.Red;

    public static int getRank(String role) {
        return switch (role) {
            case Roles.MARSHAL -> 1;
            case Roles.GENERAL -> 2;
            case Roles.COLONEL -> 3;
            case Roles.MAJOR -> 4;
            case Roles.CAPTAIN -> 5;
            case Roles.LIEUTENANT -> 6;
            case Roles.SERGEANT -> 7;
            case Roles.MINER -> 8;
            case Roles.SCOUT -> 9;
            case Roles.SPY -> 10;
            default -> 0;
        };
    }

    public static List<Figure> createFigures(Colors color) {
        List<Figure> figures = new ArrayList<>();

        // Add figures based on the specified counts
        addFigures(figures, color, Roles.MARSHAL, 1);
        addFigures(figures, color, Roles.GENERAL, 1);
        addFigures(figures, color, Roles.COLONEL, 2);
        addFigures(figures, color, Roles.MAJOR, 3);
        addFigures(figures, color, Roles.CAPTAIN, 4);
        addFigures(figures, color, Roles.LIEUTENANT, 4);
        addFigures(figures, color, Roles.SERGEANT, 4);
        addFigures(figures, color, Roles.MINER, 5);
        addFigures(figures, color, Roles.SCOUT, 8);
        addFigures(figures, color, Roles.SPY, 1);
        addFigures(figures, color, Roles.FLAG, 1);
        addFigures(figures, color, Roles.BOMB, 6);

        return figures;
    }

    public static void addFigures(List<Figure> figures, Colors color, String role, int count) {
        for (int i = 0; i < count; i++) {
            figures.add(new Figure(color, role, getRank(role)));
        }
    }

    public static void drawBoard(Figure[][] figures, GridPane gridBoard) {
        gridBoard.getChildren().clear();
        System.out.println("Drawing board");
        for (int cols = 0; cols < figures.length; cols++) {
            for (int rows = 0; rows < figures.length; rows++) {
                if (!figures[rows][cols].role.isEmpty()) {
                    final int currentRow = rows;
                    final int currentCol = cols;

                    if (figures[rows][cols].role.equals(Roles.SELECTED)) { //checks if it's an avaliable position marked for movement
                        Button button = createAvailableButton();
                        gridBoard.add(button, rows, cols);
                        button.setOnAction(actionEvent -> handleButtonClick(currentRow, currentCol));
                        continue;
                    }

                    if (figures[rows][cols].targeted) { //checks if it's an avaliable position marked for movement
                        Button button = createTargetedButton(figures[rows][cols]);
                        gridBoard.add(button, rows, cols);
                        button.setOnAction(actionEvent -> handleButtonClick(currentRow, currentCol));
                        continue;
                    }

                    Button button = createButton(figures[rows][cols]);    //normal figure button creation
                    gridBoard.add(button, rows, cols);
                    button.setOnAction(event -> handleButtonClick(currentRow, currentCol));
                } else {
                    Figure figure = createBlankFigure();
                    Button button = createButton(figure);
                    gridBoard.add(button, rows, cols);
                }
            }
        }

        HelloController.staticController.turnLabel.setText("Turn: " + turn);
    }

    private static Button createAvailableButton() {
        Button button = new Button();
        button.setStyle("-fx-background-color: yellow; -fx-border-color: black; -fx-border-width: 2;");
        button.setMinSize(55, 45);

        return button;
    }

    public static Button createTargetedButton(Figure figure) {
        Button button = new Button();
        button.setStyle("-fx-background-color: #ff7878; -fx-border-color: black; -fx-border-width: 2;");
        button.setMinSize(55, 45);

        String buttonText = "";
        if (!figure.role.isEmpty()) {
            buttonText = figure.role.substring(0, 2) + figure.rank;
            // Set text color based on the figure's color
            if (figure.color == Colors.Red) {
                buttonText = setHiddenName(RoleName.CLIENT, buttonText);
                button.setTextFill(Color.RED);
            } else {
                buttonText = setHiddenName(RoleName.SERVER, buttonText);
                button.setTextFill(Color.BLUE);
            }
        }
        button.setText(buttonText);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        return button;
    }

    public static Figure createBlankFigure() {
        return new Figure(Colors.Blank, "", -1);
    }

    public static void switchTurn() {
        turn = turn.equals(Colors.Red) ? Colors.Blue : Colors.Red;
        ButtonSelected.selectedColumn = 100;
        HelloController.staticController.turnLabel.setText("Turn: " + turn);
    }

    public static Button createButton(Figure figure) {
        Button button = new Button();
        button.setStyle("-fx-background-color: transparent; -fx-border-color: black; -fx-border-width: 2;");
        button.setMinSize(55, 45);

        String buttonText = "";
        if (!figure.role.isEmpty()) {
            buttonText = figure.role.substring(0, 2) + figure.rank;
            // Set text color based on the figure's color
            if (figure.color == Colors.Red) {
                buttonText = setHiddenName(RoleName.CLIENT, buttonText);
                button.setTextFill(Color.RED);
            } else {
                buttonText = setHiddenName(RoleName.SERVER, buttonText);
                button.setTextFill(Color.BLUE);
            }
        }


        button.setText(buttonText);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        return button;
    }

    private static String setHiddenName(RoleName rn, String text) {
        if (HelloApplication.loggedInRoleName.equals(rn)) return hiddenName;
        return text;
    }
}