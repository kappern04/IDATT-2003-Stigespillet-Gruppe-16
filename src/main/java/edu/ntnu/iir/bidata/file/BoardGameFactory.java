package edu.ntnu.iir.bidata.file;

import edu.ntnu.iir.bidata.controller.BoardGameController;
import edu.ntnu.iir.bidata.model.Board;
import java.io.IOException;
import java.io.InputStream;

public class BoardGameFactory {

  private final BoardFileReader reader;
  private final BoardFileWriter writer;

  public BoardGameFactory() {
    this.reader = new BoardFileReaderGson();
    this.writer = new BoardFileWriterGson();
  }

  public BoardGameController createBoardGameFromFile(InputStream inputStream) throws IOException {
    Board board = reader.readBoard(inputStream);
    return new BoardGameController(board);
  }

  public BoardGameController createDefaultBoardGame() {
    return new BoardGameController();
  }

  public void saveBoardGame(BoardGameController boardGameController, String filePath) throws IOException {
    writer.writeBoard(boardGameController.getBoard(), filePath);
  }

  public BoardGameController createBoardGameFromStream(InputStream inputStream) throws IOException {
    Board board = reader.readBoard(inputStream);
    return new BoardGameController(board);
  }
}