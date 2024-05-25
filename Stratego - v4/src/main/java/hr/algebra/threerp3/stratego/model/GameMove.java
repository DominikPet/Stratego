package hr.algebra.threerp3.stratego.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GameMove implements Serializable {
    private static final long serialVersionUID = -4042741131728972392L;
    private int startPositionRow;
    private int startPositionCol;
    private int endPositionRow;
    private int endPositionCol;
    private LocalDateTime time;

    public GameMove(int startPositionRow, int startPositionCol, int endPositionRow, int endPositionCol, LocalDateTime time) {
        this.startPositionRow = startPositionRow;
        this.startPositionCol = startPositionCol;
        this.endPositionRow = endPositionRow;
        this.endPositionCol = endPositionCol;
        this.time = time;
    }


    public String getStartPositionRow() {
        return Integer.toString(startPositionRow);
    }

    public String getEndPositionRow() {
        return Integer.toString(endPositionRow);
    }

    public String getStartPositionCol() {
        return Integer.toString(startPositionCol);
    }

    public String getEndPositionCol() {
        return Integer.toString(endPositionCol);
    }

    public Integer getStartPositionRowInt() {
        return startPositionRow;
    }

    public Integer getEndPositionRowInt() {
        return endPositionRow;
    }

    public Integer getStartPositionColInt() {
        return startPositionCol;
    }

    public Integer getEndPositionColInt() {
        return endPositionCol;
    }

    public LocalDateTime getTime() {
        return time;
    }


    @Override
    public String toString() {
        return "(" + startPositionRow + ", " + startPositionCol + ")" + " -> " + "(" + endPositionRow + ", " + endPositionCol + ")";
    }
}
