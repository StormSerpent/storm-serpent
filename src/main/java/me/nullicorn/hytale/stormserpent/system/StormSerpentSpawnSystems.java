package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.modules.entity.component.SnapshotBuffer;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entityui.UIComponentList;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.serpentine.component.Serpent;
import me.nullicorn.serpentine.component.SerpentBone;

import javax.annotation.Nonnull;

public final class StormSerpentSpawnSystems {
    private StormSerpentSpawnSystems() {
    }

    public static final class RoleReloadSystem extends HolderSystem<EntityStore> {
        @Override
        public Query<EntityStore> getQuery() {
            return StormSerpent.getComponentType();
        }

        @Override
        public void onEntityAdd(
            @Nonnull final Holder<EntityStore> holder,
            @Nonnull final AddReason reason,
            @Nonnull final Store<EntityStore> store
        ) {
            holder.removeComponent(StormSerpent.getComponentType());
            holder.tryRemoveComponent(StormSerpentBone.getComponentType());
            holder.tryRemoveComponent(Serpent.getComponentType());
            holder.tryRemoveComponent(SerpentBone.getComponentType());
        }

        @Override
        public void onEntityRemoved(
            @Nonnull final Holder<EntityStore> holder,
            @Nonnull final RemoveReason reason,
            @Nonnull final Store<EntityStore> store
        ) {
        }
    }

    public static final class BoneSpawnSystem extends HolderSystem<EntityStore> {
        @Override
        public Query<EntityStore> getQuery() {
            return SerpentBone.getComponentType();
        }

        @Override
        public void onEntityAdd(
            @Nonnull final Holder<EntityStore> holder,
            @Nonnull final AddReason reason,
            @Nonnull final Store<EntityStore> store
        ) {
            final SerpentBone bone = holder.getComponent(SerpentBone.getComponentType());
            assert bone != null;

            final Ref<EntityStore> serpent = bone.serpent();
            if (serpent == null || !serpent.isValid() || !store.getArchetype(bone.serpent()).contains(StormSerpent.getComponentType())) {
                return;
            }

            holder.ensureComponent(StormSerpentBone.getComponentType());

            // Required for processing damage.
            final SnapshotBuffer snapshotBuffer = holder.ensureAndGetComponent(SnapshotBuffer.getComponentType());
            snapshotBuffer.resize(1);

            // Required for processing damage.
            final EntityStatMap statMap = holder.ensureAndGetComponent(EntityStatMap.getComponentType());
            statMap.update();

            // Required for displaying entity UI (health bar and damage numbers).
            final UIComponentList uiComponents = holder.ensureAndGetComponent(UIComponentList.getComponentType());
            uiComponents.update();
        }

        @Override
        public void onEntityRemoved(
            @Nonnull final Holder<EntityStore> holder,
            @Nonnull final RemoveReason reason,
            @Nonnull final Store<EntityStore> store
        ) {
        }
    }
}
