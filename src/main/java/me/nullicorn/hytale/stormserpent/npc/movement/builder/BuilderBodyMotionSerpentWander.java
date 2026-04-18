package me.nullicorn.hytale.stormserpent.npc.movement.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSequenceValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import me.nullicorn.hytale.stormserpent.npc.movement.BodyMotionSerpentWander;

public final class BuilderBodyMotionSerpentWander extends BuilderBodyMotionBase {
    public static final String COMPONENT_ID = "StormSerpentWander";

    private final DoubleHolder relativeSpeed = new DoubleHolder();
    private final DoubleHolder radius = new DoubleHolder();
    private final NumberArrayHolder relativeAltitudeRange = new NumberArrayHolder();
    private final BooleanHolder useRelativeAltitude = new BooleanHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress; // TODO
    }

    @Override
    public String getShortDescription() {
        return "Fly randomly around a position";
    }

    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<BodyMotion> readConfig(final JsonElement data) {
        this.getDouble(data, "RelativeSpeed", this.relativeSpeed, 1.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Relative movement speed", null);
        this.getDouble(data, "Radius", this.radius, 80.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Horizontal radius around the target", null);
        this.getDoubleRange(data, "AltitudeRange", this.relativeAltitudeRange, new double[]{16, 100}, DoubleSequenceValidator.weaklyMonotonic(), BuilderDescriptorState.WorkInProgress, "Altitude range to stay in", null);
        this.getBoolean(data, "UseRelativeAltitude", this.useRelativeAltitude, true, BuilderDescriptorState.WorkInProgress, "Treat AltitudeRange as relative to the target, rather than as Y coordinates", null);
        this.requireFeature(Feature.AnyPosition);
        return this;
    }

    @Override
    public BodyMotion build(final BuilderSupport builderSupport) {
        return new BodyMotionSerpentWander(this, builderSupport);
    }

    public double getRelativeSpeed(final BuilderSupport support) {
        return this.relativeSpeed.get(support.getExecutionContext());
    }

    public double getRadius(final BuilderSupport support) {
        return this.radius.get(support.getExecutionContext());
    }

    public double[] getRelativeAltitudeRange(final BuilderSupport support) {
        return this.relativeAltitudeRange.get(support.getExecutionContext());
    }

    public boolean getUseRelativeAltitude(final BuilderSupport support) {
        return this.useRelativeAltitude.get(support.getExecutionContext());
    }
}
