package Master.Utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {
    private static final Map<UUID, BukkitTask> playerTimers = new HashMap<>();

    /**
     * Creates and displays a countdown timer above a player's hotbar
     *
     * @param plugin  The plugin instance
     * @param player  The player to show the timer to
     * @param seconds The duration of the timer in seconds
     * @param message The message to display to the player when timer ends
     */
    public static void startTimer(Plugin plugin, Player player, int seconds, String message) {
        stopTimer(player); // Stop any existing timer for this player

        final int[] timeLeft = {seconds};

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (timeLeft[0] <= 0) {
                    stopTimer(player);
                    player.sendMessage(ChatColor.RED + message);
                    return;
                }
                sendActionBar(player, formatTime(timeLeft[0]));
                timeLeft[0]--; // Decrement the timer
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second (20 ticks)

        playerTimers.put(player.getUniqueId(), task);
    }

    /**
     * Stops a player's active timer if one exists
     *
     * @param player The player whose timer should be stopped
     */
    public static void stopTimer(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (playerTimers.containsKey(playerUUID)) {
            playerTimers.get(playerUUID).cancel();
            playerTimers.remove(playerUUID);
        }
    }

    /**
     * Formats seconds into a mm:ss format
     *
     * @param seconds Total seconds to format
     * @return Formatted time string
     */
    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    /**
     * Sends an action bar message to a player
     * This is the text that appears above the hotbar
     *
     * @param player  The player to send the message to
     * @param message The message to display
     */
    public static void sendActionBar(Player player, String message) {
        try {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(message));
        } catch (Exception e) {
            // Fallback to a chat message if action bar fails
            player.sendMessage(message);
        }
    }
}