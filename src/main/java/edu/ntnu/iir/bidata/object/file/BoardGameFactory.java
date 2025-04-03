package edu.ntnu.iir.bidata.object.file;

import edu.ntnu.iir.bidata.controller.BoardGame;
import edu.ntnu.iir.bidata.object.Board;
import java.io.IOException;

public class BoardGameFactory {

  private final BoardFileReader reader;
  private final BoardFileWriter writer;

  public BoardGameFactory() {
    this.reader = new BoardFileReaderGson();
    this.writer = new BoardFileWriterGson();
  }

  public BoardGame createBoardGameFromFile(String filePath) throws IOException {
    Board board = reader.readBoard(filePath);
    return new BoardGame(board);
  }

  public BoardGame createDefaultBoardGame() {
    return new BoardGame();
  }

  public void saveBoardGame(BoardGame boardGame, String filePath) throws IOException {
    writer.writeBoard(boardGame.getBoard(), filePath);
  }
}