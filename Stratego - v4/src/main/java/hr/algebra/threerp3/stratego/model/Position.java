package hr.algebra.threerp3.stratego.model;

import java.io.Serializable;

public class Position implements Serializable {
    private static final long serialVersionUID = -4042741131728972393L;
    public int row;
    public int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
