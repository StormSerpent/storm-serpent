package me.nullicorn.hytale.stormserpent.ui.map;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.system.StormSerpentSpatialSystem;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class StormSerpentMapMarkerSystem extends TickingSystem<EntityStore> {
    private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> spatialResourceType;

    public StormSerpentMapMarkerSystem(final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> spatialResourceType) {
        this.spatialResourceType = spatialResourceType;
    }

    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(
            new SystemDependency<>(Order.AFTER, StormSerpentSpatialSystem.class)
        );
    }

    @Override
    public void tick(final float dt, final int systemIndex, @Nonnull final Store<EntityStore> store) {
        final var spatialResource = store.getResource(this.spatialResourceType);
        final var spatialData = spatialResource.getSpatialData();
        final var markersResource = store.getResource(StormSerpentMapMarkersResource.getResourceType());

        final Map<UUID, Vector3d> markers = new HashMap<>();
        for (int i = 0; i < spatialData.size(); i++) {
            final Ref<EntityStore> ref = spatialData.getData(i);
            final UUIDComponent uuidComponent = store.getComponent(ref, UUIDComponent.getComponentType());
            if (uuidComponent == null) {
                continue;
            }

            final Vector3d position = spatialData.getVector(i);
            markers.put(uuidComponent.getUuid(), new Vector3d(position));
        }
        markersResource.setMarkers(markers);
    }
}
