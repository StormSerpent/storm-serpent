package me.nullicorn.hytale.stormserpent.npc.movement.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderHeadMotionBase;
import com.hypixel.hytale.server.npc.instructions.HeadMotion;
import me.nullicorn.hytale.stormserpent.npc.movement.HeadMotionSerpentMatchBodyMotion;

import javax.annotation.Nullable;

public final class BuilderHeadMotionSerpentMatchBodyMotion extends BuilderHeadMotionBase {
    public static final String COMPONENT_ID = "StormSerpentMatchBodyMotion";

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Face in the direction the NPC is moving";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public HeadMotion build(final BuilderSupport builderSupport) {
        return new HeadMotionSerpentMatchBodyMotion(this, builderSupport);
    }
}
