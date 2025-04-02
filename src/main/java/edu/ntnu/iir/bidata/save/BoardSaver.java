package edu.ntnu.iir.bidata.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.iir.bidata.object.Board;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BoardSaver {
  private static final String SAVE_DIRECTORY = "saves";
  private static final String BOARD_FILE = "board.json";

  public boolean saveBoard(Board board, String saveName) {
    try {
      File saveDir = new File(SAVE_DIRECTORY + "/" + saveName);
      if (!saveDir.exists() && !saveDir.mkdirs()) {
        return false;
      }

      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      String json = gson.toJson(board);

      Files.write(Paths.get(SAVE_DIRECTORY + "/" + saveName + "/" + BOARD_FILE),
          json.getBytes());
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public Board loadBoard(String saveName) {
    try {
      String json = new String(Files.readAllBytes(
          Paths.get(SAVE_DIRECTORY + "/" + saveName + "/" + BOARD_FILE)));

      Gson gson = new Gson();
      return gson.fromJson(json, Board.class);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}