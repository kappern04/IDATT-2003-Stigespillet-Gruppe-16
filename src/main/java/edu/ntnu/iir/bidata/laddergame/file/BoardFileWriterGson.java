package edu.ntnu.iir.bidata.laddergame.file;

import com.google.gson.*;
import edu.ntnu.iir.bidata.laddergame.model.Board;
import edu.ntnu.iir.bidata.laddergame.model.LadderAction;
import edu.ntnu.iir.bidata.laddergame.model.Tile;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Implementation of BoardFileWriter using Gson for JSON serialization.
 * Writes board definitions to JSON files.
 */
public class BoardFileWriterGson implements BoardFileWriter {

    /**
     * Writes a board definition to a file in JSON format.
     *
     * @param board The Board object to be written to the file.
     * @param filePath The path of the file where the board definition will be written.
     * @throws IOException If an error occurs while writing to the file.
     */
  @Override
  public void writeBoard(Board board, String filePath) throws IOException {
    JsonObject root = new JsonObject();
    root.addProperty("name", board.getBoardName());
    root.addProperty("description", board.getDescription());

    JsonArray tilesArray = new JsonArray();

    for (Tile tile : board.getTiles()) {
      JsonObject tileJson = getJsonObject(tile);

      tilesArray.add(tileJson);
    }

    root.add("tiles", tilesArray);

    try (FileWriter writer = new FileWriter(filePath)) {
      writer.write(root.toString());
    }
  }

    /**
     * Converts a Tile object into a JSON object.
     *
     * @param tile The Tile object to be converted.
     * @return A JsonObject representing the tile.
     */
  private static JsonObject getJsonObject(Tile tile) {
    JsonObject tileJson = new JsonObject();
    tileJson.addProperty("id", tile.getIndex());
    tileJson.addProperty("x", tile.getX());
    tileJson.addProperty("y", tile.getY());

    if (tile.hasLadderAction()) {
      JsonObject actionJson = new JsonObject();
      actionJson.addProperty("type", "LadderAction");
      actionJson.addProperty("destinationTileIndex", ((LadderAction) tile.getTileAction()).getDestinationTileIndex());
      tileJson.add("action", actionJson);
    }
    return tileJson;
  }


}
