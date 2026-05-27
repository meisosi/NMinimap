package su.nezushin.nminimap.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import su.nezushin.nminimap.NMinimap;
import su.nezushin.nminimap.util.config.Config;

public class PlayerListener implements Listener {


    @EventHandler
    public void join(PlayerJoinEvent e) {
        var p = e.getPlayer();

        NMinimap.getInstance().loadPlayer(p);

        if (p.hasPermission("nminimap.admin"))
            NMinimap.getInstance().getUpdateCheckerManager().notifyIfHasNewVersion(p);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        var player = e.getPlayer();

        NMinimap.async(() -> {
            NMinimap.getInstance().getPlayersWithMap().removeIf(i -> {
                if (i.getPlayer().equals(player)) {
                    i.onQuit();
                    return true;
                }
                return false;
            });
        });
    }

    @EventHandler
    public void teleport(PlayerTeleportEvent e) {
        var p = e.getPlayer();

        var player = NMinimap.getInstance().getPlayersWithMap().stream().filter(i -> i.getPlayer().equals(p)).findFirst().orElse(null);

        if (player != null && player.isEnabled()) {
            NMinimap.getInstance().getPacketManager().spawnEntities(p);
            if (Config.layersEnabled) {
                player.updateRenderHeightBasedOnRegions();
            }
        }
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        // Only check render height change if blocks have changed or coordinates changed significantly
        if (!Config.layersEnabled) {
            return;
        }

        // Throttle checks to every 5 blocks moved to avoid performance issues
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
            e.getFrom().getBlockZ() == e.getTo().getBlockZ() &&
            Math.abs(e.getFrom().getBlockY() - e.getTo().getBlockY()) < 5) {
            return;
        }

        var p = e.getPlayer();
        var player = NMinimap.getInstance().getPlayersWithMap().stream().filter(i -> i.getPlayer().equals(p)).findFirst().orElse(null);

        if (player != null && player.isEnabled()) {
            player.updateRenderHeightBasedOnRegions();
        }
    }

}
