package su.nezushin.nminimap.layers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import su.nezushin.nminimap.util.config.Config;
import su.nezushin.nminimap.util.config.UndergroundLayer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manager for handling map layers based on WorldGuard regions
 */
public class LayerManager {

    /**
     * Get the active underground layer for a player based on WorldGuard regions
     * Returns null if player is not in any configured underground region (shows surface)
     */
    public static UndergroundLayer getPlayerUndergroundLayer(Player player) {
        if (!Config.layersEnabled || !hasWorldGuardPlugin()) {
            return null;
        }

        UndergroundLayer bestLayer = null;

        // Get all regions player is in
        var playerRegions = getPlayerRegions(player);

        if (playerRegions.isEmpty()) {
            return null;
        }

        // Find matching layer with highest priority (or lowest render-y if no priority)
        for (var layer : Config.undergroundLayers) {
            for (var regionName : playerRegions) {
                if (layer.wgRegions().stream().anyMatch(i -> i.equalsIgnoreCase(regionName))) {
                    if (bestLayer == null) {
                        bestLayer = layer;
                    } else {
                        if (layer.priority() > bestLayer.priority()) {
                            bestLayer = layer;
                        } else if (layer.priority() == bestLayer.priority() &&
                                   layer.renderFromY() < bestLayer.renderFromY()) {
                            bestLayer = layer;
                        }
                    }
                    break;
                }
            }
        }

        return bestLayer;
    }

    /**
     * Get render Y level for player (where to start rendering)
     * Returns Integer.MAX_VALUE for surface (render from top)
     */
    public static int getRenderYForPlayer(Player player) {
        UndergroundLayer layer = getPlayerUndergroundLayer(player);
        if (layer == null) {
            return Integer.MAX_VALUE; // Surface - render from top
        }
        return layer.renderFromY();
    }

    /**
     * Get list of WorldGuard region names player is currently in
     */
    private static List<String> getPlayerRegions(Player player) {
        try {
            if (player == null || player.getLocation() == null) {
                return Collections.emptyList();
            }

            var loc = player.getLocation();

            try {
                Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
                Method getInstance = worldGuardClass.getMethod("getInstance");
                Object worldGuard = getInstance.invoke(null);
                Object platform = worldGuard.getClass().getMethod("getPlatform").invoke(worldGuard);
                Object regionContainer = platform.getClass().getMethod("getRegionContainer").invoke(platform);

                Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
                Method adapt = bukkitAdapterClass.getMethod("adapt", World.class);
                Object weWorld = adapt.invoke(null, loc.getWorld());

                Method getRegionManager = regionContainer.getClass().getMethod("get", Class.forName("com.sk89q.worldedit.world.World"));
                Object regionManager = getRegionManager.invoke(regionContainer, weWorld);
                if (regionManager == null) {
                    return Collections.emptyList();
                }

                Class<?> blockVectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                Method at = blockVectorClass.getMethod("at", int.class, int.class, int.class);
                Object vector = at.invoke(null, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

                Object applicableRegions = regionManager.getClass().getMethod("getApplicableRegions", blockVectorClass).invoke(regionManager, vector);

                List<String> result = new ArrayList<>();
                if (applicableRegions instanceof Iterable<?> iterable) {
                    for (var region : iterable) {
                        result.add(String.valueOf(region.getClass().getMethod("getId").invoke(region)).toLowerCase());
                    }
                }
                return result;
            } catch (Exception e) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Check if WorldGuard plugin is available
     */
    private static boolean hasWorldGuardPlugin() {
        return org.bukkit.Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    /**
     * Check if layer switching is enabled
     */
    public static boolean isLayerSwitchingEnabled() {
        return Config.layersEnabled;
    }
}
