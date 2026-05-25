package su.nezushin.nminimap.layers;

import java.util.List;

/**
 * Represents an underground layer with custom render height
 */
public class UndergroundLayer {
    
    private final String name;
    private final List<String> worldguardRegions;
    private final int renderFromY;
    private final int priority;
    
    public UndergroundLayer(String name, List<String> worldguardRegions, int renderFromY, int priority) {
        this.name = name;
        this.worldguardRegions = worldguardRegions;
        this.renderFromY = renderFromY;
        this.priority = priority;
    }
    
    public String getName() {
        return name;
    }
    
    public List<String> getWorldGuardRegions() {
        return worldguardRegions;
    }
    
    public int getRenderFromY() {
        return renderFromY;
    }
    
    public int getPriority() {
        return priority;
    }
    
    @Override
    public String toString() {
        return "UndergroundLayer{" +
                "name='" + name + '\'' +
                ", renderFromY=" + renderFromY +
                ", priority=" + priority +
                ", regions=" + worldguardRegions +
                '}';
    }
}

