package edu.ntnu.iir.bidata.object.file;

import edu.ntnu.iir.bidata.object.Board;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.DirectoryChooser;

public class LocalBoardRegister {

  private String userSavePath;
  private List<Board> userBoards;
  private List<Board> nativeBoards;
  private List<Board> allBoards;
  private BoardFileReaderGson reader;

  public LocalBoardRegister() {
    chooseSavePath();
    this.reader = new BoardFileReaderGson();
    this.nativeBoards = new ArrayList<>();
    this.userBoards = new ArrayList<>();
    loadNativeBoards();
    loadUserBoards();
  }

  private void chooseSavePath() {
    if (userSavePath == null || userSavePath.isBlank()) {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      directoryChooser.setTitle("Choose Save Directory");

      File selectedDirectory = directoryChooser.showDialog(null);
      if (selectedDirectory != null) {
        userSavePath = selectedDirectory.getAbsolutePath();
        if (!selectedDirectory.exists() && !selectedDirectory.mkdirs()) {
          throw new RuntimeException("Could");
        }
      }
    }
  }

  private void loadNativeBoards() {
    nativeBoards = new ArrayList<>();
    File nativeBoardDir = new File("src/main/resources/boards");
    File[] nativeBoardFiles = nativeBoardDir.listFiles();
    for (File boardFile : nativeBoardFiles) {
      if (boardFile.isFile() && boardFile.getName().endsWith(".json")) {
        try {
          Board board = reader.readBoard(boardFile.getPath());
          nativeBoards.add(board);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    allBoards = new ArrayList<>(nativeBoards);
    if (userBoards != null) {
      allBoards.addAll(userBoards);
    }
  }

  private void loadUserBoards() {
    if (userSavePath == null || userSavePath.isBlank()) {
      return;
    }

    userBoards = new ArrayList<>();
    File userBoardDir = new File(userSavePath);
    File[] userBoardFiles = userBoardDir.listFiles();
    for (File boardFile : userBoardFiles) {
      if (boardFile.isFile() && boardFile.getName().endsWith(".json")) {
        try {
          Board board = reader.readBoard(boardFile.getPath());
          userBoards.add(board);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    allBoards = new ArrayList<>(nativeBoards);
    if (userBoards != null) {
      allBoards.addAll(userBoards);
    }
  }

  public List<Board> getAllBoards() {
    return allBoards;
  }

  public List<Board> getUserBoards() {
    return userBoards;
  }

  public List<Board> getNativeBoards() {
    return nativeBoards;
  }

}
