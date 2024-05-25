package hr.algebra.threerp3.stratego.thread;

import hr.algebra.threerp3.stratego.model.GameMove;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class GetLastGameMoveThread extends GameMoveThread implements Runnable {
    private final Label lastMoveLabel;

    public GetLastGameMoveThread(Label lastMoveLabel) {
        this.lastMoveLabel = lastMoveLabel;
    }

    @Override
    public void run() {
        while (true) {
            GameMove lastGameMove = getLastGameMove();
            Platform.runLater(() -> lastMoveLabel.setText("The last game move: " + lastGameMove.toString()));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
