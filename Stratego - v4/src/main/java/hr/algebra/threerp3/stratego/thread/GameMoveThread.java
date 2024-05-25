package hr.algebra.threerp3.stratego.thread;

import hr.algebra.threerp3.stratego.model.GameMove;
import hr.algebra.threerp3.stratego.repository.GameMoveRepository;
import hr.algebra.threerp3.stratego.utils.ButtonLogicUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class GameMoveThread implements GameMoveRepository {
    public static final String GAME_MOVES_FILE_NAME = "files/moves.dat";

    private static boolean fileAccessInProgress = false;

    @Override
    public synchronized List<GameMove> getAllGameMoves() {
        List<GameMove> allGameMoves = new ArrayList<>();
        List<GameMove> emptyGameMoves = new ArrayList<>();
        emptyGameMoves.add(new GameMove(0, 0, 0, 0, LocalDateTime.now()));

        if (!Files.exists(Path.of(GAME_MOVES_FILE_NAME)) | !ButtonLogicUtils.firstGameMoveSet) return emptyGameMoves;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GAME_MOVES_FILE_NAME))) {
            allGameMoves = new ArrayList<>((List<GameMove>) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return emptyGameMoves;
        }

        return allGameMoves;
    }

    @Override
    public synchronized void saveNewGameMove(GameMove gameMove) {
        if (fileAccessInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        fileAccessInProgress = true;
        List<GameMove> allGameMoves = getAllGameMoves();
        allGameMoves.add(gameMove);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GAME_MOVES_FILE_NAME))) {
            oos.writeObject(allGameMoves);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileAccessInProgress = false;

        notifyAll();
    }

    public synchronized GameMove getLastGameMove() {
        if (fileAccessInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        fileAccessInProgress = true;

        GameMove last = getAllGameMoves().getLast();
        fileAccessInProgress = false;

        notifyAll();
        return last;
    }
}
