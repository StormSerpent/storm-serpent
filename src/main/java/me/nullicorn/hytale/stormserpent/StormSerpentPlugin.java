package me.nullicorn.hytale.stormserpent;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.hytale.stormserpent.npc.action.builder.*;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.*;
import me.nullicorn.hytale.stormserpent.npc.sensor.builder.*;
import me.nullicorn.hytale.stormserpent.solver.EnteringBurrowJointSolver;
import me.nullicorn.hytale.stormserpent.system.StormSerpentBurrowSystems;
import me.nullicorn.hytale.stormserpent.system.StormSerpentSpawnSystems;
import me.nullicorn.hytale.stormserpent.ui.hud.StormSerpentBossBarHudSystem;
import me.nullicorn.serpentine.solver.SerpentJointSolver;

import javax.annotation.Nonnull;

public final class StormSerpentPlugin extends JavaPlugin {
    private static StormSerpentPlugin instance;

    public static StormSerpentPlugin get() {
        return instance;
    }

    private ComponentType<EntityStore, StormSerpent> stormSerpentComponentType;
    private ComponentType<EntityStore, StormSerpentBone> stormSerpentBoneComponentType;

    public StormSerpentPlugin(@Nonnull final JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;

        this.stormSerpentComponentType = this.getEntityStoreRegistry().registerComponent(StormSerpent.class, StormSerpent.COMPONENT_ID, StormSerpent.CODEC);
        this.stormSerpentBoneComponentType = this.getEntityStoreRegistry().registerComponent(StormSerpentBone.class, StormSerpentBone::new);

        this.getEntityStoreRegistry().registerSystem(new StormSerpentSpawnSystems.BoneHolderSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentBossBarHudSystem());
        this.getEntityStoreRegistry().registerSystem(new StormSerpentBurrowSystems.EnterBurrowBoneTickingSystem());

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
        NPCPlugin.get().registerCoreComponentType(BuilderActionStormSerpentAddCombatants.COMPONENT_ID, BuilderActionStormSerpentAddCombatants::new);
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
}
