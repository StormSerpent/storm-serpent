package me.nullicorn.hytale.stormserpent;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.spatial.KDTree;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.ChunkPreLoadProcessEvent;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import me.nullicorn.hytale.stormserpent.command.StormSerpentCommand;
import me.nullicorn.hytale.stormserpent.component.HideBossBar;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.hytale.stormserpent.npc.action.builder.*;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.*;
import me.nullicorn.hytale.stormserpent.npc.sensor.builder.*;
import me.nullicorn.hytale.stormserpent.solver.EnteringBurrowJointSolver;
import me.nullicorn.hytale.stormserpent.system.*;
import me.nullicorn.hytale.stormserpent.ui.StormSerpentMusicSystem;
import me.nullicorn.hytale.stormserpent.ui.hud.StormSerpentBossBarHudSystem;
import me.nullicorn.hytale.stormserpent.ui.map.StormSerpentMapMarkerProvider;
import me.nullicorn.hytale.stormserpent.ui.map.StormSerpentMapMarkerSystem;
import me.nullicorn.hytale.stormserpent.ui.map.StormSerpentMapMarkersResource;
import me.nullicorn.serpentine.asset.*;
import me.nullicorn.serpentine.component.*;
import me.nullicorn.serpentine.solver.DefaultSerpentBoneSolver;
import me.nullicorn.serpentine.solver.DefaultSerpentJointSolver;
import me.nullicorn.serpentine.solver.SerpentBoneSolver;
import me.nullicorn.serpentine.solver.SerpentJointSolver;
import me.nullicorn.serpentine.system.*;
import org.joml.Vector2d;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import java.util.List;

public final class StormSerpentPlugin extends JavaPlugin {
    private static StormSerpentPlugin instance;

    public static StormSerpentPlugin get() {
        return instance;
    }

    // TODO: Temporarily copied from Serpentine since it's not published yet.
    private ComponentType<EntityStore, Serpent> serpentComponentType;
    private ComponentType<EntityStore, SerpentBone> serpentBoneComponentType;
    private ComponentType<EntityStore, SerpentBoneAutoApplyScale> serpentBoneAutoApplyScaleComponentType;
    private ComponentType<EntityStore, SerpentBoneAutoApplyModel> serpentBoneAutoApplyModelComponentType;
    private ComponentType<EntityStore, SerpentBoneAutoApplyTransform> serpentBoneAutoApplyTransformComponentType;

    private ComponentType<EntityStore, StormSerpent> stormSerpentComponentType;
    private ComponentType<EntityStore, StormSerpentBone> stormSerpentBoneComponentType;
    private ComponentType<EntityStore, HideBossBar> hideBossBarComponentType;
    private ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> spatialResourceType;
    private ResourceType<EntityStore, StormSerpentMapMarkersResource> mapMarkersResourceType;

    public StormSerpentPlugin(@Nonnull final JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;

        this.serpentineSetup();

        this.stormSerpentComponentType = this.getEntityStoreRegistry().registerComponent(StormSerpent.class, StormSerpent.COMPONENT_ID, StormSerpent.CODEC);
        this.stormSerpentBoneComponentType = this.getEntityStoreRegistry().registerComponent(StormSerpentBone.class, StormSerpentBone::new);
        this.hideBossBarComponentType = this.getEntityStoreRegistry().registerComponent(HideBossBar.class, HideBossBar::get);
        this.spatialResourceType = this.getEntityStoreRegistry().registerSpatialResource(() -> new KDTree<>(Ref::isValid));
        this.mapMarkersResourceType = this.getEntityStoreRegistry().registerResource(StormSerpentMapMarkersResource.class, StormSerpentMapMarkersResource::new);
        this.getEventRegistry().registerGlobal(AddWorldEvent.class, this::onWorldLoaded);
        this.getEventRegistry().registerGlobal(ChunkPreLoadProcessEvent.class, this::onChunkPreLoadProcess);

        this.getEntityStoreRegistry().registerSystem(new StormSerpentSpatialSystem(this.spatialResourceType));
        this.getEntityStoreRegistry().registerSystem(new StormSerpentMapMarkerSystem(this.spatialResourceType));
        this.getEntityStoreRegistry().registerSystem(new StormSerpentBossBarHudSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentMusicSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentSpawnSystems.RoleReloadSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentSpawnSystems.BoneSpawnSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentBurrowSystems.EnterBurrowBoneTickingSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentHealthSystems.DamageSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentHealthSystems.CopyHealthPreRegenSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentHealthSystems.RestoreHealthPostRegenSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentWiggleSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentAmbienceSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerOxygenDisableSystems.ChangeWorldSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerOxygenDisableSystems.RespawnSystem());
        this.getEntityStoreRegistry().registerSystem(new TransformNanHotfixSystem()); // TODO: Delete me once this bug is fixed in HytaleServer!! See docs for TransformNanHotfixSystem

        this.getCommandRegistry().registerCommand(new StormSerpentCommand());

        SerpentJointSolver.CODEC.register(EnteringBurrowJointSolver.COMPONENT_ID, EnteringBurrowJointSolver.class, EnteringBurrowJointSolver.CODEC);

        // NPC motions.
        NPCPlugin.get().registerCoreComponentType(BuilderBodyMotionSerpentWander.COMPONENT_ID, BuilderBodyMotionSerpentWander::new);
        NPCPlugin.get().registerCoreComponentType(BuilderBodyMotionSerpentEncircle.COMPONENT_ID, BuilderBodyMotionSerpentEncircle::new);
        NPCPlugin.get().registerCoreComponentType(BuilderBodyMotionSerpentDive.COMPONENT_ID, BuilderBodyMotionSerpentDive::new);
        NPCPlugin.get().registerCoreComponentType(BuilderBodyMotionSerpentBallistic.COMPONENT_ID, BuilderBodyMotionSerpentBallistic::new);
        NPCPlugin.get().registerCoreComponentType(BuilderHeadMotionSerpentMatchBodyMotion.COMPONENT_ID, BuilderHeadMotionSerpentMatchBodyMotion::new);

        // NPC motion controllers.
        NPCPlugin.get().registerCoreComponentType(BuilderMotionControllerStormSerpentFly.COMPONENT_ID, BuilderMotionControllerStormSerpentFly::new);

        // NPC sensors.
        NPCPlugin.get().registerCoreComponentType(BuilderSensorReadLeashPosition.COMPONENT_ID, BuilderSensorReadLeashPosition::new);
        NPCPlugin.get().registerCoreComponentType(BuilderSensorBelowY.COMPONENT_ID, BuilderSensorBelowY::new);
        NPCPlugin.get().registerCoreComponentType(BuilderSensorInBlocks.COMPONENT_ID, BuilderSensorInBlocks::new);
        NPCPlugin.get().registerCoreComponentType(BuilderSensorInBurrow.COMPONENT_ID, BuilderSensorInBurrow::new);
        NPCPlugin.get().registerCoreComponentType(BuilderSensorCounter.COMPONENT_ID, BuilderSensorCounter::new);

        // NPC actions.
        NPCPlugin.get().registerCoreComponentType(BuilderActionStormSerpentInitialize.COMPONENT_ID, BuilderActionStormSerpentInitialize::new);
        NPCPlugin.get().registerCoreComponentType(BuilderActionStormSerpentBeginFight.COMPONENT_ID, BuilderActionStormSerpentBeginFight::new);
        NPCPlugin.get().registerCoreComponentType(BuilderActionEnterBurrow.COMPONENT_ID, BuilderActionEnterBurrow::new);
        NPCPlugin.get().registerCoreComponentType(BuilderActionExitBurrow.COMPONENT_ID, BuilderActionExitBurrow::new);
        NPCPlugin.get().registerCoreComponentType(BuilderActionCounterSet.COMPONENT_ID, BuilderActionCounterSet::new);
        NPCPlugin.get().registerCoreComponentType(BuilderActionCounterAdd.COMPONENT_ID, BuilderActionCounterAdd::new);
        NPCPlugin.get().registerCoreComponentType(BuilderActionPlaceSound.COMPONENT_ID, BuilderActionPlaceSound::new);
    }

    public ComponentType<EntityStore, StormSerpent> getStormSerpentComponentType() {
        return this.stormSerpentComponentType;
    }

    public ComponentType<EntityStore, StormSerpentBone> getStormSerpentBoneComponentType() {
        return this.stormSerpentBoneComponentType;
    }

    public ComponentType<EntityStore, HideBossBar> getHideBossBarComponentType() {
        return this.hideBossBarComponentType;
    }

    public ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> getStormSerpentSpatialResourceType() {
        return this.spatialResourceType;
    }

    public ResourceType<EntityStore, StormSerpentMapMarkersResource> getStormSerpentMapMarkersResourceType() {
        return this.mapMarkersResourceType;
    }

    private void onWorldLoaded(final AddWorldEvent event) {
        event.getWorld()
            .getWorldMapManager()
            .addMarkerProvider(StormSerpentMapMarkerProvider.ID, StormSerpentMapMarkerProvider.INSTANCE);
    }

    private void onChunkPreLoadProcess(final ChunkPreLoadProcessEvent event) {
        if (!event.isNewlyGenerated()) {
            return;
        }

        final List<Vector2d> spawnpoints = StormSerpentSpawnSystems.getChunkSerpentSpawnpoints(event.getChunk());
        if (spawnpoints.isEmpty()) {
            return;
        }

        final Holder<ChunkStore> chunkHolder = event.getHolder();
        final var blockChunk = chunkHolder.getComponent(BlockChunk.getComponentType());
        final var world = event.getChunk().getWorld();

        for (final var spawnpoint : spawnpoints) {
            // Find the highest block's Y coordinate at the spawn's X and Z.
            final int spawnAltitude;
            if (blockChunk != null) {
                spawnAltitude = blockChunk.getHeight(ChunkUtil.indexColumn(MathUtil.floor(spawnpoint.x), MathUtil.floor(spawnpoint.y))) + 8;
            } else {
                spawnAltitude = ChunkUtil.HEIGHT;
            }

            final NPCEntity npc = new NPCEntity();
            npc.setRoleName("Serpent_Storm"); // TODO: Don't hardcode this.

            world.execute(() ->
                NPCPlugin.get().spawnEntity(world.getEntityStore().getStore(), NPCPlugin.get().getIndex("Serpent_Storm"), new Vector3d(spawnpoint.x, spawnAltitude, spawnpoint.y), new Rotation3f(), null, null, null)
            );
        }
    }

    // TODO: Temporarily copied from Serpentine since it's not published yet.
    private void serpentineSetup() {
        this.getAssetRegistry().register(
            HytaleAssetStore.builder(SerpentBoneConfig.class, new DefaultAssetMap<>())
                .setPath(SerpentBoneConfig.PATH)
                .setCodec(SerpentBoneConfig.CODEC)
                .setKeyFunction(SerpentBoneConfig::getId)
                .loadsAfter(ModelAsset.class).build()
        );
        this.getAssetRegistry().register(
            HytaleAssetStore.builder(SerpentConfig.class, new DefaultAssetMap<>())
                .setPath(SerpentConfig.PATH)
                .setCodec(SerpentConfig.CODEC)
                .setKeyFunction(SerpentConfig::getId)
                .loadsAfter(SerpentBoneConfig.class).build()
        );

        SerpentLayoutNode.CODEC.register(SerpentLayoutBone.ID, SerpentLayoutBone.class, SerpentLayoutBone.CODEC);
        SerpentLayoutNode.CODEC.register(SerpentLayoutSequence.ID, SerpentLayoutSequence.class, SerpentLayoutSequence.CODEC);
        SerpentLayoutNode.CODEC.register(SerpentLayoutRepeater.ID, SerpentLayoutRepeater.class, SerpentLayoutRepeater.CODEC);

        SerpentJointSolver.CODEC.register(DefaultSerpentJointSolver.ID, DefaultSerpentJointSolver.class, DefaultSerpentJointSolver.CODEC);
        SerpentBoneSolver.CODEC.register(DefaultSerpentBoneSolver.ID, DefaultSerpentBoneSolver.class, DefaultSerpentBoneSolver.CODEC);

        this.serpentComponentType = this.getEntityStoreRegistry().registerComponent(Serpent.class, Serpent.ID, Serpent.CODEC);
        this.serpentBoneComponentType = this.getEntityStoreRegistry().registerComponent(SerpentBone.class, () -> {
            throw new UnsupportedOperationException("Not implemented");
        });
        this.serpentBoneAutoApplyScaleComponentType = this.getEntityStoreRegistry().registerComponent(SerpentBoneAutoApplyScale.class, SerpentBoneAutoApplyScale::get);
        this.serpentBoneAutoApplyModelComponentType = this.getEntityStoreRegistry().registerComponent(SerpentBoneAutoApplyModel.class, SerpentBoneAutoApplyModel::get);
        this.serpentBoneAutoApplyTransformComponentType = this.getEntityStoreRegistry().registerComponent(SerpentBoneAutoApplyTransform.class, SerpentBoneAutoApplyTransform::get);

        this.getEntityStoreRegistry().registerSystem(new SerpentHeadSpawnSystems.SpawnRefSystem());
        this.getEntityStoreRegistry().registerSystem(new SerpentHeadSpawnSystems.SpawnRefChangeSystem());
        this.getEntityStoreRegistry().registerSystem(new SerpentSolverSystem());
        this.getEntityStoreRegistry().registerSystem(new SerpentBoneSpawnSystem());
        this.getEntityStoreRegistry().registerSystem(new SerpentBoneDespawnSystem());
        this.getEntityStoreRegistry().registerSystem(new SerpentBoneApplyScaleSystem());
        this.getEntityStoreRegistry().registerSystem(new SerpentBoneApplyModelSystem());
        this.getEntityStoreRegistry().registerSystem(new SerpentBoneApplyTransformSystem());
    }

    public ComponentType<EntityStore, Serpent> getSerpentComponentType() {
        return this.serpentComponentType;
    }

    public ComponentType<EntityStore, SerpentBone> getSerpentBoneComponentType() {
        return this.serpentBoneComponentType;
    }

    public ComponentType<EntityStore, SerpentBoneAutoApplyScale> getSerpentBoneAutoApplyScaleComponentType() {
        return this.serpentBoneAutoApplyScaleComponentType;
    }

    public ComponentType<EntityStore, SerpentBoneAutoApplyModel> getSerpentBoneAutoApplyModelComponentType() {
        return this.serpentBoneAutoApplyModelComponentType;
    }

    public ComponentType<EntityStore, SerpentBoneAutoApplyTransform> getSerpentBoneAutoApplyTransformComponentType() {
        return this.serpentBoneAutoApplyTransformComponentType;
    }
}
