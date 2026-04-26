package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.system.ModelSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Hotfix for player bounding boxes being {@code Double.MAX_VALUE..=-Double.MAX_VALUE} when teleporting to new
 * instances, resulting in a crash when using the command {@code /npc spawn}.
 */
// TODO: Delete me once this bug is fixed in HytaleServer!!
public final class TransformNanHotfixSystem extends HolderSystem<EntityStore> {
    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(
            new SystemDependency<>(Order.BEFORE, ModelSystems.ModelSpawned.class)
        );
    }

    @Override
    public Query<EntityStore> getQuery() {
        return TransformComponent.getComponentType();
    }

    @Override
    public void onEntityAdd(
        @Nonnull final Holder<EntityStore> holder,
        @Nonnull final AddReason reason,
        @Nonnull final Store<EntityStore> store
    ) {
        final TransformComponent transform = holder.getComponent(TransformComponent.getComponentType());
        assert transform != null;

        // Replace any Infinity or NaN components with 0.

        final Vector3d position = transform.getPosition();
        if (!Double.isFinite(position.x)) position.x = 0;
        if (!Double.isFinite(position.y)) position.y = 0;
        if (!Double.isFinite(position.z)) position.z = 0;

        final Rotation3f rotation = transform.getRotation();
        if (!Float.isFinite(rotation.x)) rotation.x = 0;
        if (!Float.isFinite(rotation.y)) rotation.y = 0;
        if (!Float.isFinite(rotation.z)) rotation.z = 0;
    }

    @Override
    public void onEntityRemoved(
        @Nonnull final Holder<EntityStore> holder,
        @Nonnull final RemoveReason reason,
        @Nonnull final Store<EntityStore> store
    ) {
    }
}
