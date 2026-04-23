package me.nullicorn.hytale.stormserpent.ui.map;

import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.joml.Vector3d;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class StormSerpentMapMarkersResource implements Resource<EntityStore> {
    public final Map<UUID, Vector3d> markers = new ConcurrentHashMap<>();

    @Override
    public Resource<EntityStore> clone() {
        final StormSerpentMapMarkersResource clone = new StormSerpentMapMarkersResource();
        clone.markers.putAll(this.markers);
        return clone;
    }
}
