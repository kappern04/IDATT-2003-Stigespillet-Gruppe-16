package edu.ntnu.iir.bidata.laddergame.model;

import edu.ntnu.iir.bidata.laddergame.util.ChanceEffectType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Helper class to randomly assign chance card actions to normal tiles.
 */
public class ChanceTileInitializer {
    private static final Random random = new Random();

    /**
     * Adds chance card actions to a percentage of normal tiles on the board.
     *
     * @param board             The game board
     * @param percentageOfTiles Percentage of normal tiles to convert (0-100)
     */
    public static void addChanceTilesToBoard(Board board, int percentageOfTiles) {
        if (board == null || percentageOfTiles <= 0) {
            return;
        }

        List<Tile> tiles = board.getTiles();
        int lastTileIndex = board.getLastTile();

        // Create list of eligible tiles (not ladders and not the last tile)
        List<Tile> eligibleTiles = new ArrayList<>();
        for (Tile tile : tiles) {
            if (!tile.hasLadderAction() && tile.getIndex() != lastTileIndex) {
                eligibleTiles.add(tile);
            }
        }

        // Calculate how many chance tiles to create
        int numberOfTilesToConvert = Math.max(1, (int) (eligibleTiles.size() * percentageOfTiles / 100.0));
        int count = 0;

        // Randomly select tiles from eligible list
        while (count < numberOfTilesToConvert && !eligibleTiles.isEmpty()) {
            int randomIndex = random.nextInt(eligibleTiles.size());
            Tile selectedTile = eligibleTiles.get(randomIndex);

            // Randomly select a chance effect type
            ChanceEffectType[] effectTypes = ChanceEffectType.values();
            ChanceEffectType randomEffect = effectTypes[random.nextInt(effectTypes.length)];

            selectedTile.setTileAction(new CosmicChanceAction(randomEffect));
            selectedTile.setType("chance");
            count++;

            // Remove from eligible list to avoid selecting it again
            eligibleTiles.remove(randomIndex);

            System.out.println("Chance tile added at position: " + selectedTile.getIndex() + " with effect: " + randomEffect);
        }
    }
}