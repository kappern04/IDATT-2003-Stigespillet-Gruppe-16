package edu.ntnu.iir.bidata.file;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.model.TileAction;
import edu.ntnu.iir.bidata.model.LadderAction;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BoardFileReaderGson implements BoardFileReader {

  @Override
  public Board readBoard(InputStream inputStream) throws IOException {
    try (JsonReader reader = new JsonReader(new InputStreamReader(inputStream))) {
      JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

      String name = root.get("name").getAsString();
      String description = root.get("description").getAsString();

      JsonArray tilesArray = root.getAsJsonArray("tiles");
      List<Tile> tiles = new ArrayList<>();

      for (JsonElement tileElement : tilesArray) {
        JsonObject tileJson = tileElement.getAsJsonObject();
        Tile tile = getTileFromJson(tileJson);
        tiles.add(tile);
      }

      return new Board(name, description, tiles);
    }
  }

  private static Tile getTileFromJson(JsonObject tileJson) {
    int id = tileJson.get("id").getAsInt();
    int x = tileJson.get("x").getAsInt();
    int y = tileJson.get("y").getAsInt();

    TileAction action = new TileAction();
    if (tileJson.has("action")) {
      JsonObject actionJson = tileJson.getAsJsonObject("action");
      String actionType = actionJson.get("type").getAsString();

      if ("LadderAction".equals(actionType)) {
        int destinationTileIndex = actionJson.get("destinationTileIndex").getAsInt();
        action = new LadderAction(destinationTileIndex);
      }
    }

    return new Tile(id, x, y, action);
  }
}
