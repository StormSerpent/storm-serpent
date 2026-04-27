package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3d;
import com.hypixel.hytale.builtin.hytalegenerator.plugin.Handle;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.builtin.hytalegenerator.rng.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.workerindexer.WorkerIndexer;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.modules.entity.component.SnapshotBuffer;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entityui.UIComponentList;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.serpentine.component.Serpent;
import me.nullicorn.serpentine.component.SerpentBone;
import me.nullicorn.serpentine.solver.DefaultSerpentBoneSolver;
import me.nullicorn.serpentine.solver.DefaultSerpentJointSolver;
import org.joml.Vector2d;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class StormSerpentSpawnSystems {
    public static List<Vector2d> getChunkSerpentSpawnpoints(final WorldChunk chunk) {
        if (!(chunk.getWorld().getChunkStore().getGenerator() instanceof final Handle worldGen)) {
            return Collections.emptyList();
        }
        if (!worldGen.getProfile().worldStructureName().equals("Serpent_Storm")) {
            return Collections.emptyList();
        }
        final var providerAsset = PositionProviderAsset.getExportedAsset("AtollPositions");
        if (providerAsset == null) {
            return Collections.emptyList();
        }
        final var providerArgs = new PositionProviderAsset.Argument(new SeedBox(worldGen.getProfile().seed()), new ReferenceBundle(), WorkerIndexer.Id.MAIN);
        final PositionProvider provider = providerAsset.build(providerArgs);
        final var bounds = new Bounds3d(
            new Vector3d(
                ChunkUtil.minBlock(chunk.getX()),
                ChunkUtil.MIN_ENTITY_Y,
                ChunkUtil.minBlock(chunk.getZ())
            ),
            new Vector3d(
                ChunkUtil.maxBlock(chunk.getX()),
                ChunkUtil.HEIGHT,
                ChunkUtil.maxBlock(chunk.getZ())
            )
        );
        final var spawnPositions = new ArrayList<Vector2d>();
        final var context = new PositionProvider.Context(bounds, (position, _control) -> spawnPositions.add(new Vector2d(position.x, position.z)), null);
        provider.generate(context);
        return spawnPositions;
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
            final StormSerpent stormSerpent = holder.getComponent(StormSerpent.getComponentType());
            assert stormSerpent != null;
            if (stormSerpent.burrowStatus != StormSerpent.BurrowStatus.NOT_IN_BURROW) {
                stormSerpent.burrowStatus = StormSerpent.BurrowStatus.NOT_IN_BURROW;

                final Serpent serpent = holder.getComponent(Serpent.getComponentType());
                if (serpent != null) {
                    serpent.setBoneSolver(new DefaultSerpentBoneSolver());
                    serpent.setJointSolver(new DefaultSerpentJointSolver());
                    for (final Serpent.Bone bone : serpent.bones()) {
                        bone.setRef(null);
                        bone.setAutoSpawn(true);
                    }
                }
            }
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

    private StormSerpentSpawnSystems() {
    }
}
