package edu.ntnu.iir.bidata.file;

import com.google.gson.*;
import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Tile;
import edu.ntnu.iir.bidata.model.LadderAction;

import java.io.FileWriter;
import java.io.IOException;

public class BoardFileWriterGson implements BoardFileWriter {

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

  private static JsonObject getJsonObject(Tile tile) {
    JsonObject tileJson = new JsonObject();
    tileJson.addProperty("id", tile.getIndex());
    tileJson.addProperty("x", tile.getX());
    tileJson.addProperty("y", tile.getY());

    if (tile.getTileAction() instanceof LadderAction) {
      JsonObject actionJson = new JsonObject();
      actionJson.addProperty("type", "LadderAction");
      actionJson.addProperty("destinationTileIndex", ((LadderAction) tile.getTileAction()).getDestinationTileIndex());
      tileJson.add("action", actionJson);
    }
    return tileJson;
  }


}
