package edu.ntnu.iir.bidata.file;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.LadderAction;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.model.TileAction;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BoardFileReader using Gson for JSON parsing.
 * Reads board definitions from JSON files and converts them into Board objects.
 */
public class BoardFileReaderGson implements BoardFileReader {

    /**
     * Reads a board definition from the given InputStream.
     *
     * @param inputStream The InputStream containing the JSON board definition.
     * @return A Board object representing the board definition.
     * @throws IOException If an error occurs while reading the InputStream.
     */
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

    /**
     * Converts a JSON object representing a tile into a Tile object.
     *
     * @param tileJson The JSON object representing the tile.
     * @return A Tile object created from the JSON data.
     */
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
