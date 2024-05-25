package hr.algebra.threerp3.stratego.repository;

import hr.algebra.threerp3.stratego.model.GameMove;

import java.util.List;

public interface GameMoveRepository {
    void saveNewGameMove(GameMove gameMove);

    List<GameMove> getAllGameMoves();

    GameMove getLastGameMove();
}
