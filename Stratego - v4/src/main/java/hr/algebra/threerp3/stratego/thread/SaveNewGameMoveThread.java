package hr.algebra.threerp3.stratego.thread;

import hr.algebra.threerp3.stratego.model.GameMove;

public class SaveNewGameMoveThread extends GameMoveThread implements Runnable {

    private GameMove gameMove;

    public SaveNewGameMoveThread(GameMove gameMove) {
        this.gameMove = gameMove;
    }

    @Override
    public void run() {
        saveNewGameMove(gameMove);
    }
}
