package edu.ntnu.iir.bidata.laddergame.model;

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

    @Override
    public void execute(Player player) {
        // This method should only be called after showing the animation and popup
        executeEffect(player);
    }

    /**
     * Executes the actual effect on the player. Convenience overload for effects
     * that do not depend on the other players (a player swap will be a no-op).
     */
    public void executeEffect(Player player) {
        executeEffect(player, java.util.Collections.emptyList());
    }

    /**
     * Executes the actual effect on the player.
     *
     * @param player     the player who triggered the chance tile
     * @param allPlayers all players currently in the game, used by effects that
     *                   interact with other players (e.g. TELEPORT_RANDOM)
     */
    public void executeEffect(Player player, List<Player> allPlayers) {
        // Apply the effect this tile was assigned at board setup, so the effect
        // shown in the popup matches what actually happens to the player.
        LOGGER.info("Executing cosmic chance action: " + effectType);

        switch (effectType) {
            case FORWARD_SMALL:
                player.move(random.nextInt(3) + 1);
                break;
            case FORWARD_MEDIUM:
                player.move(random.nextInt(3) + 4);
                break;
            case FORWARD_LARGE:
                player.move(random.nextInt(4) + 7);
                break;
            case BACKWARD_SMALL:
                player.move(-(random.nextInt(3) + 1));
                break;
            case BACKWARD_MEDIUM:
                player.move(-(random.nextInt(3) + 4));
                break;
            case BACKWARD_LARGE:
                player.move(-(random.nextInt(4) + 7));
                break;
            case SWAP_RANDOM_PLAYER:
                List<Player> otherPlayers = allPlayers.stream()
                        .filter(p -> !p.equals(player))
                        .toList();
                if (!otherPlayers.isEmpty()) {
                    Player target = otherPlayers.get(random.nextInt(otherPlayers.size()));
                    int tempPos = player.getPositionIndex();
                    player.setPositionIndex(target.getPositionIndex());
                    target.setPositionIndex(tempPos);
                    // The swap is part of this single chance activation, so neither
                    // player's destination should trigger another chance effect now.
                    player.setChanceActivatedThisTurn(true);
                    target.setChanceActivatedThisTurn(true);
                    LOGGER.info(player.getName() + " swapped places with " + target.getName());
                } else {
                    LOGGER.info("No other players to swap with.");
                }
                break;
            case RETURN_START:
                player.setPositionIndex(0);
                break;
            case EXTRA_TURN:
                player.setExtraTurn(true);
                LOGGER.info(player.getName() + " received an extra turn!");
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