package me.nullicorn.hytale.stormserpent.ui.map;

import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.StormSerpentPlugin;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public final class StormSerpentMapMarkersResource implements Resource<EntityStore> {
    public static ResourceType<EntityStore, StormSerpentMapMarkersResource> getResourceType() {
        return StormSerpentPlugin.get().getStormSerpentMapMarkersResourceType();
    }

    private final Map<UUID, Vector3d> markers = new HashMap<>();

    public synchronized void setMarkers(final Map<UUID, Vector3d> markers) {
        this.markers.clear();
        this.markers.putAll(markers);
    }

    public synchronized void forEachMarker(final BiConsumer<UUID, Vector3d> consumer) {
        for (final Map.Entry<UUID, Vector3d> entry : this.markers.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Resource<EntityStore> clone() {
        final StormSerpentMapMarkersResource clone = new StormSerpentMapMarkersResource();
        clone.markers.putAll(this.markers);
        return clone;
    }
}
