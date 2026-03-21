package me.nullicorn.hytale.wip.npc.movement.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.movement.controllers.builders.BuilderMotionControllerBase;
import com.hypixel.hytale.server.spawning.SpawnTestResult;
import com.hypixel.hytale.server.spawning.SpawningContext;
import me.nullicorn.hytale.wip.npc.movement.MotionControllerSerpentFly;

import javax.annotation.Nonnull;

public final class BuilderMotionControllerSerpentFly extends BuilderMotionControllerBase {
    @Override
    public Class<? extends MotionController> getClassType() {
        return MotionControllerSerpentFly.class;
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

    @Override
    public Builder<MotionController> readConfig(final JsonElement data) {
        return super.readConfig(data); // TODO
    }

    @Override
    public MotionController build(final BuilderSupport builderSupport) {
        return new MotionControllerSerpentFly(builderSupport, this); // TODO
    }
}
