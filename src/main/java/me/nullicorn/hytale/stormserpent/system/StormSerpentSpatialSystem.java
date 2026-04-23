package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.spatial.SpatialSystem;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.StormSerpentPlugin;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import org.joml.Vector3d;

import javax.annotation.Nonnull;

/**
 * Updates the positions of {@link StormSerpent} entities in a {@link SpatialResource}.
 *
 * @see StormSerpentPlugin#getStormSerpentSpatialResourceType()
 */
public final class StormSerpentSpatialSystem extends SpatialSystem<EntityStore> {
    public StormSerpentSpatialSystem(final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> resourceType) {
        super(resourceType);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(StormSerpent.getComponentType(), TransformComponent.getComponentType());
    }

    @Override
    public Vector3d getPosition(@Nonnull final ArchetypeChunk<EntityStore> archetypeChunk, final int index) {
        final var transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        assert transform != null;
        return transform.getPosition();
    }
}
