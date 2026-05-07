package me.nullicorn.hytale.stormserpent.npc.movement.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import me.nullicorn.hytale.stormserpent.npc.movement.BodyMotionSerpentBallistic;

import javax.annotation.Nullable;

public final class BuilderBodyMotionSerpentBallistic extends BuilderBodyMotionBase {
    public static final String COMPONENT_ID = "StormSerpentBallistic";

    private final StringHolder targetSlot = new StringHolder();
    private final DoubleHolder arcHeight = new DoubleHolder();
    private final DoubleHolder arcPathSpeed = new DoubleHolder();
    private final DoubleHolder relativeSpeed = new DoubleHolder();
    private final DoubleHolder relativeTurnSpeed = new DoubleHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Launch in a trajectory toward a target";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<BodyMotion> readConfig(final JsonElement data) {
        this.requireString(data, "TargetSlot", this.targetSlot, StringNotEmptyValidator.get(), BuilderDescriptorState.WorkInProgress, "Target to aim for", null);
        this.requireDouble(data, "ArcHeight", this.arcHeight, null, BuilderDescriptorState.WorkInProgress, "Peak height of the arc relative to the starting OR target position, whichever is higher", null);
        this.requireDouble(data, "ArcPathSpeed", this.arcPathSpeed, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Horizontal speed that the arc is followed at", null);
        this.getDouble(data, "RelativeSpeed", this.relativeSpeed, 1.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Relative movement speed", null);
        this.getDouble(data, "RelativeTurnSpeed", this.relativeTurnSpeed, 1.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Relative turning speed", null);
        return this;
    }

    @Override
    public BodyMotion build(final BuilderSupport builderSupport) {
        return new BodyMotionSerpentBallistic(this, builderSupport);
    }

    public int getTargetSlot(final BuilderSupport support) {
        return support.getTargetSlot(this.targetSlot.get(support.getExecutionContext()));
    }

    public double getArcHeight(final BuilderSupport support) {
        return this.arcHeight.get(support.getExecutionContext());
    }

    public double getArcPathSpeed(final BuilderSupport support) {
        return this.arcPathSpeed.get(support.getExecutionContext());
    }

    public double getRelativeSpeed(final BuilderSupport support) {
        return this.relativeSpeed.get(support.getExecutionContext());
    }

    public double getRelativeTurnSpeed(final BuilderSupport support) {
        return this.relativeTurnSpeed.get(support.getExecutionContext());
    }
}
