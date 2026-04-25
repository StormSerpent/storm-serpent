package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.system.TransformSystems;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.serpentine.component.Serpent;
import me.nullicorn.serpentine.component.SerpentBone;
import me.nullicorn.serpentine.system.SerpentBoneApplyTransformSystem;

import javax.annotation.Nonnull;
import java.util.Set;

public class StormSerpentWiggleSystem extends EntityTickingSystem<EntityStore> {
    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(
            new SystemDependency<>(Order.AFTER, SerpentBoneApplyTransformSystem.class),
            new SystemDependency<>(Order.BEFORE, TransformSystems.EntityTrackerUpdate.class)
        );
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(StormSerpentBone.getComponentType(), SerpentBone.getComponentType(), TransformComponent.getComponentType());
    }

    @Override
    public void tick(
        final float dt,
        final int index,
        @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull final Store<EntityStore> store,
        @Nonnull final CommandBuffer<EntityStore> commandBuffer
    ) {
        final var time = commandBuffer.getResource(WorldTimeResource.getResourceType()).getGameTime().toEpochMilli() / 1000.0;
        final var serpentBone = archetypeChunk.getComponent(index, SerpentBone.getComponentType());
        assert serpentBone != null;

        final var serpentRef = serpentBone.serpent();
        if (!serpentRef.isValid()) {
            return;
        }

        final var serpent = commandBuffer.getComponent(serpentRef, Serpent.getComponentType());
        if (serpent == null) {
            return;
        }

        final double offset = ((double) serpentBone.index() / (serpent.bones().size() - 1));
        final double roll = Math.sin(offset * 10 - time * 0.1) * MathUtil.lerp(0.1, 0.5, offset);
        if (serpentBone.index() == 0) {
            final var headRotation = archetypeChunk.getComponent(index, HeadRotation.getComponentType());
            assert headRotation != null;
            headRotation.getRotation().setRoll((float) roll);
        } else {
            final var transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
            assert transform != null;
            transform.getRotation().setRoll((float) roll);
        }
    }
}
