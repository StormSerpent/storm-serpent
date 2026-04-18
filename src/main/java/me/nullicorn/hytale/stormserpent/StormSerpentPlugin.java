package me.nullicorn.hytale.stormserpent;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.hytale.stormserpent.npc.action.builder.BuilderActionStormSerpentAddCombatants;
import me.nullicorn.hytale.stormserpent.npc.action.builder.BuilderActionStormSerpentBeginFight;
import me.nullicorn.hytale.stormserpent.npc.action.builder.BuilderActionStormSerpentInitialize;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderBodyMotionSerpentEncircle;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderBodyMotionSerpentWander;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderHeadMotionSerpentMatchBodyMotion;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderMotionControllerStormSerpentFly;
import me.nullicorn.hytale.stormserpent.npc.sensor.builder.BuilderSensorReadLeashPosition;
import me.nullicorn.hytale.stormserpent.system.StormSerpentSpawnSystems;
import me.nullicorn.hytale.stormserpent.ui.hud.StormSerpentBossBarHudSystem;

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

        // NPC motions.
        NPCPlugin.get().registerCoreComponentType(BuilderBodyMotionSerpentWander.COMPONENT_ID, BuilderBodyMotionSerpentWander::new);
        NPCPlugin.get().registerCoreComponentType(BuilderBodyMotionSerpentEncircle.COMPONENT_ID, BuilderBodyMotionSerpentEncircle::new);
        NPCPlugin.get().registerCoreComponentType(BuilderHeadMotionSerpentMatchBodyMotion.COMPONENT_ID, BuilderHeadMotionSerpentMatchBodyMotion::new);

        // NPC motion controllers.
        NPCPlugin.get().registerCoreComponentType(BuilderMotionControllerStormSerpentFly.COMPONENT_ID, BuilderMotionControllerStormSerpentFly::new);

        // NPC sensors.
        NPCPlugin.get().registerCoreComponentType(BuilderSensorReadLeashPosition.COMPONENT_ID, BuilderSensorReadLeashPosition::new);

        // NPC actions.
        NPCPlugin.get().registerCoreComponentType(BuilderActionStormSerpentInitialize.COMPONENT_ID, BuilderActionStormSerpentInitialize::new);
        NPCPlugin.get().registerCoreComponentType(BuilderActionStormSerpentBeginFight.COMPONENT_ID, BuilderActionStormSerpentBeginFight::new);
        NPCPlugin.get().registerCoreComponentType(BuilderActionStormSerpentAddCombatants.COMPONENT_ID, BuilderActionStormSerpentAddCombatants::new);
    }

    public ComponentType<EntityStore, StormSerpent> getStormSerpentComponentType() {
        return this.stormSerpentComponentType;
    }

    public ComponentType<EntityStore, StormSerpentBone> getStormSerpentBoneComponentType() {
        return this.stormSerpentBoneComponentType;
    }
}
