package su.nezushin.nminimap.layers;

/**
 * Enum representing different map layers (surface, underground, etc.)
 */
public enum MapLayer {
    SURFACE(0, "surface"),
    CAVE(1, "cave");

    private final int id;
    private final String name;

    MapLayer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * Get layer by ID
     */
    public static MapLayer byId(int id) {
        for (var layer : values()) {
            if (layer.id == id)
                return layer;
        }
        return SURFACE;
    }
}

