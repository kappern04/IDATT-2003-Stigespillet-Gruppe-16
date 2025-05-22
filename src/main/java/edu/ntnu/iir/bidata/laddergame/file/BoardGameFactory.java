package edu.ntnu.iir.bidata.laddergame.file;

import edu.ntnu.iir.bidata.laddergame.controller.BoardGameController;
import edu.ntnu.iir.bidata.laddergame.controller.board.DieController;
import edu.ntnu.iir.bidata.laddergame.controller.board.PlayerController;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.Die;
import edu.ntnu.iir.bidata.laddergame.view.board.DieView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class BoardGameFactory {
  private final BoardFileReader reader;
  private final BoardFileWriter writer;

  public BoardGameFactory() {
    this.reader = new BoardFileReaderGson();
    this.writer = new BoardFileWriterGson();
  }

  public BoardGameController createBoardGameFromFile(InputStream inputStream) throws IOException {
    Board board = reader.readBoard(inputStream);
    PlayerController playerController = new PlayerController(board, new ArrayList<>());
    DieView dieView = new DieView();
    DieController dieController = new DieController(new Die(), dieView);
    return new BoardGameController(board, playerController, dieController);
  }

  public BoardGameController createDefaultBoardGame() {
    return new BoardGameController();
  }

  public void saveBoardGame(BoardGameController boardGameController, String filePath) throws IOException {
    writer.writeBoard(boardGameController.getBoard(), filePath);
  }

  public BoardGameController createBoardGameFromStream(InputStream inputStream) throws IOException {
    Board board = reader.readBoard(inputStream);
    PlayerController playerController = new PlayerController(board, new ArrayList<>());
    DieView dieView = new DieView();
    DieController dieController = new DieController(new Die(), dieView);
    return new BoardGameController(board, playerController, dieController);
  }
}