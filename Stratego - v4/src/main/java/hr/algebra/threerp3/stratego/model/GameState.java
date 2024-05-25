package hr.algebra.threerp3.stratego.model;

import java.io.Serializable;

public class GameState implements Serializable {

    private Colors turn;
    private Figure[][] gameBoardState;
    private Boolean winner = false;
    private String label;

    public GameState(Colors turn, Figure[][] gameBoardState) {
        this.turn = turn;
        this.gameBoardState = gameBoardState;
        this.winner = null;
        this.label = null;
    }

    public GameState(Colors turn, Figure[][] gameBoardState, String label) {
        this.turn = turn;
        this.gameBoardState = gameBoardState;
        this.winner = null;
        this.label = label;
    }

    public GameState(Colors turn, Figure[][] gameBoardState, String label, Boolean winner) {
        this.turn = turn;
        this.gameBoardState = gameBoardState;
        this.winner = winner;
        this.label = label;
    }

    public Colors getTurn() {
        return turn;
    }

    public Boolean getWinner() {
        return winner;
    }

    public String getLabel() {
        return label;
    }

    public void setTurn(Colors turn) {
        this.turn = turn;
    }

    public Figure[][] getGameBoardState() {
        return gameBoardState;
    }

    public void setGameBoardState(Figure[][] gameBoardState) {
        this.gameBoardState = gameBoardState;
    }
}
