package hr.algebra.threerp3.stratego.utils;

import hr.algebra.threerp3.stratego.model.Colors;
import hr.algebra.threerp3.stratego.model.Figure;
import hr.algebra.threerp3.stratego.model.GameState;

public class GameStateUtils {
    public static GameState createGameState(Colors turn, Figure[][] board) {
        return new GameState(turn, board);
    }
}
