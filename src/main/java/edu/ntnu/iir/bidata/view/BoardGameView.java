package edu.ntnu.iir.bidata.view;

import javafx.scene.layout.StackPane;
import edu.ntnu.iir.bidata.object.Board;

public class BoardGameView {
  private Board board;
  private BoardView boardView;

  public BoardGameView(Board board) {
    this.board = board;
    this.boardView = new BoardView(board);
  }

  public StackPane createGameBoard() {
    return boardView.createGameBoard();
  }
}