package edu.ntnu.iir.bidata.laddergame.model;

import edu.ntnu.iir.bidata.laddergame.util.ChanceEffectType;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class CosmicChanceAction implements TileAction {
    private final Random random = new Random();
    private final ChanceEffectType effectType;
    private static final Logger LOGGER = Logger.getLogger(CosmicChanceAction.class.getName());

    /**
     * Creates a new cosmic chance action with the specified effect type.
     * @param effectType the type of effect this action will have
     */
    public CosmicChanceAction(ChanceEffectType effectType) {
        this.effectType = effectType;
    }

    /**
     * Gets the effect type of this chance action.
     * @return the type of effect
     */
    public ChanceEffectType getEffectType() {
        return effectType;
    }

    /**
     * Gets the type of this chance action.
     * @return the type of effect
     */
    public ChanceEffectType getType() {
        return effectType;
    }

    @Override
    public void execute(Player player) {
        // This method should only be called after showing the animation and popup
        executeEffect(player);
    }

    /**
     * Executes the actual effect on the player
     */
    public void executeEffect(Player player) {
        LOGGER.info("Executing cosmic chance action: " + effectType);
        switch (effectType) {
            case FORWARD_SMALL:
                player.move(random.nextInt(3) + 1); // 1-3
                break;
            case FORWARD_MEDIUM:
                player.move(random.nextInt(3) + 4); // 4-6
                break;
            case FORWARD_LARGE:
                player.move(random.nextInt(4) + 7); // 7-10
                break;
            case BACKWARD_SMALL:
                player.move(-(random.nextInt(3) + 1)); // -1 to -3
                break;
            case BACKWARD_MEDIUM:
                player.move(-(random.nextInt(3) + 4)); // -4 to -6
                break;
            case BACKWARD_LARGE:
                player.move(-(random.nextInt(4) + 7)); // -7 to -10
                break;
            case EXTRA_TURN:
                player.setHasExtraTurn(true);
                break;
            case TELEPORT_RANDOM:
                // Get all players except the current one
                List<Player> otherPlayers = Player.getPlayers().stream()
                        .filter(p -> !p.equals(player))
                        .toList();

                if (!otherPlayers.isEmpty()) {
                    // Pick a random player to swap with
                    Player target = otherPlayers.get(random.nextInt(otherPlayers.size()));
                    int tempPos = player.getPositionIndex();
                    player.setPositionIndex(target.getPositionIndex());
                    target.setPositionIndex(tempPos);
                    LOGGER.info(player.getName() + " swapped places with " + target.getName());
                } else {
                    LOGGER.info("No other players to swap with.");
                }
                break;
            case SKIP_TURN:
                player.setSkipTurn(true);
                break;
            case RETURN_START:
                player.setPositionIndex(0);
                break;
            default:
                LOGGER.warning("Unknown effect type: " + effectType);
        }
    }

    @Override
    public String getDescription() {
        return "Cosmic Chance: " + effectType.getName() + " - " + effectType.getDescription();
    }
}