package hr.algebra.threerp3.stratego.model;

import java.io.Serial;
import java.io.Serializable;

public class Figure implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    public Colors color;

    public String role;

    public int rank;

    public boolean targeted = false;

    public Figure(Colors color, String role, int rank) {
        this.color = color;
        this.role = role;
        this.rank = rank;
    }

    public Figure() {
        this.color = null;
        this.role = "";
        this.rank = -1;
    }
}
