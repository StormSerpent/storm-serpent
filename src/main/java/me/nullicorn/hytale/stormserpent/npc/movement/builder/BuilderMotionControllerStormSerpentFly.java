package me.nullicorn.hytale.stormserpent.npc.movement.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.movement.MovementMode;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.movement.controllers.builders.BuilderMotionControllerBase;
import com.hypixel.hytale.server.spawning.SpawnTestResult;
import com.hypixel.hytale.server.spawning.SpawningContext;
import me.nullicorn.hytale.stormserpent.npc.movement.MotionControllerStormSerpentFly;

import javax.annotation.Nonnull;
import java.util.Set;

public final class BuilderMotionControllerStormSerpentFly extends BuilderMotionControllerBase {
    public static final String COMPONENT_ID = "StormSerpentFly";

    @Override
    public Class<? extends MotionController> getClassType() {
        return MotionControllerStormSerpentFly.class;
    }

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress; // TODO
    }

    @Override
    public String getShortDescription() {
        return ""; // TODO
    }

    @Override
    public String getLongDescription() {
        return ""; // TODO
    }

    @Override
    public Class<MotionController> category() {
        return MotionController.class;
    }

    @Nonnull
    @Override
    public SpawnTestResult canSpawn(@Nonnull final SpawningContext context) {
        return SpawnTestResult.TEST_OK; // TODO
    }

    @Nonnull
    @Override
    public Set<MovementMode> getSupportedMovementModes() {
        return Set.of(MovementMode.FLY);
    }

    @Override
    public void getMovementModes(
        @Nonnull final SpawningContext context,
        @Nonnull final Set<MovementMode> outSupportedMovementModes,
        @Nonnull final Set<MovementMode> outDefaultMovementModes,
        @Nonnull final Set<MovementMode> outSafeMovementModes
    ) {
        outSupportedMovementModes.add(MovementMode.FLY);
        outDefaultMovementModes.add(MovementMode.FLY);
        outSafeMovementModes.add(MovementMode.FLY);
    }

    @Override
    public Builder<MotionController> readConfig(final JsonElement data) {
        return super.readConfig(data); // TODO
    }

    @Override
    public MotionController build(final BuilderSupport builderSupport) {
        return new MotionControllerStormSerpentFly(builderSupport, this); // TODO
    }
}
