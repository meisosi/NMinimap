package su.nezushin.nminimap.chunks;

import org.bukkit.Location;
import org.bukkit.World;
import su.nezushin.nminimap.util.config.Config;

import java.io.File;

public record ChunkEntry(World w, int x, int z, int renderFromY) {

    public ChunkEntry(World w, int x, int z) {
        this(w, x, z, Integer.MAX_VALUE);
    }

    public File getAsFile() {
        String renderPart = renderFromY == Integer.MAX_VALUE ? "" : "." + renderFromY;
        return new File(Config.cacheFolder, w.getName() + "." + x + "." + z + renderPart + ".bin.gz");
    }

    public boolean isInsideWorldBorder() {
        return w.getWorldBorder().isInside(new Location(w, x * 16, 0, z * 16));
    }
}

