package me.nullicorn.hytale.wip.npc.movement.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import me.nullicorn.hytale.wip.npc.movement.BodyMotionSerpentWander;

public final class BuilderBodyMotionSerpentWander extends BuilderBodyMotionBase {
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress; // TODO
    }

    @Override
    public String getShortDescription() {
        return "Fly randomly around the leash point";
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    public Builder<BodyMotion> readConfig(final JsonElement data) {
        return this;
    }

    @Override
    public BodyMotion build(final BuilderSupport builderSupport) {
        return new BodyMotionSerpentWander(this, builderSupport);
    }
}
