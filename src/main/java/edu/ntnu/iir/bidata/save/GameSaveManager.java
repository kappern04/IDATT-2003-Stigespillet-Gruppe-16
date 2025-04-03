package edu.ntnu.iir.bidata.save;

import edu.ntnu.iir.bidata.object.Board;
import edu.ntnu.iir.bidata.object.Player;
import java.util.List;

public class GameSaveManager {
  private BoardSaver boardSaver;
  private PlayerSaver playerSaver;

  public GameSaveManager() {
    boardSaver = new BoardSaver();
    playerSaver = new PlayerSaver();
  }

  public boolean saveGame(Board board, List<Player> players, String saveName) {
    return boardSaver.saveBoard(board, saveName) &&
        playerSaver.savePlayers(players, saveName);
  }

  public Object[] loadGame(String saveName) {
    Board board = boardSaver.loadBoard(saveName);
    List<Player> players = playerSaver.loadPlayers(saveName);

    return new Object[]{board, players};
  }
}