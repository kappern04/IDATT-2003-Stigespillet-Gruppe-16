package edu.ntnu.iir.bidata.laddergame.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Board-setup helper that converts a share of a board's tiles into chance tiles.
 * Keeps chance-tile generation in the model layer rather than in a menu controller.
 */
public final class ChanceTilePlacer {

    private ChanceTilePlacer() {
        // Utility class: not instantiable.
    }

    /**
     * Converts a percentage of the board's eligible tiles into chance tiles.
     * Eligible tiles exclude the start tile, the last tile, ladder tiles, and
     * tiles that are the destination of a ladder.
     *
     * @param board            the board to modify
     * @param chancePercentage percentage (0-100) of eligible tiles to convert
     */
    public static void placeChanceTiles(Board board, int chancePercentage) {
        List<Tile> eligibleTiles = new ArrayList<>();
        for (Tile tile : board.getTiles()) {
            int index = tile.getIndex();
            if (index != 0 && index != board.getLastTile()
                    && !tile.hasLadderAction() && !tile.isDestinationOfLadder(board)) {
                eligibleTiles.add(tile);
            }
        }

        int tilesToConvert = (int) Math.ceil(eligibleTiles.size() * (chancePercentage / 100.0));
        Random random = new Random();
        ChanceEffectType[] effectTypes = ChanceEffectType.values();

        for (int i = 0; i < tilesToConvert && !eligibleTiles.isEmpty(); i++) {
            Tile selectedTile = eligibleTiles.remove(random.nextInt(eligibleTiles.size()));
            ChanceEffectType randomEffect = effectTypes[random.nextInt(effectTypes.length)];
            selectedTile.setTileAction(new CosmicChanceAction(randomEffect));
            selectedTile.setType("chance");
        }
    }
}
