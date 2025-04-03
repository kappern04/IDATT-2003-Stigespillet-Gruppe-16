package edu.ntnu.iir.bidata.save;

import edu.ntnu.iir.bidata.object.Player;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerSaver {
  private static final String SAVE_DIRECTORY = "saves";
  private static final String PLAYER_FILE = "players.csv";

  public boolean savePlayers(List<Player> players, String saveName) {
    File saveDir = new File(SAVE_DIRECTORY + "/" + saveName);
    if (!saveDir.exists() && !saveDir.mkdirs()) {
      return false;
    }

    try (PrintWriter writer = new PrintWriter(
        new FileWriter(SAVE_DIRECTORY + "/" + saveName + "/" + PLAYER_FILE))) {

      // Write header
      writer.println("name,position");

      // Write player data
      for (Player player : players) {
        writer.println(player.getName() + "," +
            player.getPositionIndex());
      }
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public List<Player> loadPlayers(String saveName) {
    List<Player> players = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(
        new FileReader(SAVE_DIRECTORY + "/" + saveName + "/" + PLAYER_FILE))) {

      // Skip header
      String line = reader.readLine();

      // Read player data
      while ((line = reader.readLine()) != null) {
        String[] data = line.split(",");
        if (data.length >= 2) {
          String name = data[0];
          int position = Integer.parseInt(data[1]);

          Player player = new Player(name);
          player.setPositionIndex(position);
          players.add(player);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return players;
  }
}