package me.nullicorn.hytale.stormserpent.npc.movement.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderBodyMotionBase;
import com.hypixel.hytale.server.npc.instructions.BodyMotion;
import me.nullicorn.hytale.stormserpent.npc.movement.BodyMotionSerpentEncircle;

import javax.annotation.Nullable;

public class BuilderBodyMotionStormSerpentEncircle extends BuilderBodyMotionBase {
    public static final String COMPONENT_ID = "StormSerpentEncircle";

    private final DoubleHolder relativeAltitude = new DoubleHolder();
    private final DoubleHolder relativeSpeed = new DoubleHolder();
    private final DoubleHolder radius = new DoubleHolder();
    private final DoubleHolder oscillateRadiusFrequency = new DoubleHolder();
    private final DoubleHolder oscillateRadiusAmplitude = new DoubleHolder();
    private final DoubleHolder oscillateYFrequency = new DoubleHolder();
    private final DoubleHolder oscillateYAmplitude = new DoubleHolder();

    @Nullable
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Nullable
    @Override
    public String getShortDescription() {
        return "Move in a circle around a target";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    public Builder<BodyMotion> readConfig(final JsonElement data) {
        this.getDouble(data, "RelativeAltitude", this.relativeAltitude, 0.0, null, BuilderDescriptorState.WorkInProgress, "Altitude to maintain relative to the target", null);
        this.getDouble(data, "RelativeSpeed", this.relativeSpeed, 1.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Movement speed multiplier", null);
        this.getDouble(data, "Radius", this.radius, 16.0, DoubleSingleValidator.greater0(), BuilderDescriptorState.WorkInProgress, "Distance to maintain from the target", null);
        this.getDouble(data, "OscillateRadiusFrequency", this.oscillateRadiusFrequency, 1.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Rate at which Radius is oscillated around its base value", null);
        this.getDouble(data, "OscillateRadiusAmplitude", this.oscillateRadiusAmplitude, 0.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Amount that Radius is oscillated around its base value", null);
        this.getDouble(data, "OscillateYFrequency", this.oscillateYFrequency, 1.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Rate at which Y position is oscillated around its base value", null);
        this.getDouble(data, "OscillateYAmplitude", this.oscillateYAmplitude, 0.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Amount that Y position is oscillated around its base value", null);
        return this;
    }

    @Nullable
    @Override
    public BodyMotion build(final BuilderSupport builderSupport) {
        return new BodyMotionSerpentEncircle(this, builderSupport);
    }

    public double getRelativeAltitude(final BuilderSupport support) {
        return this.relativeAltitude.get(support.getExecutionContext());
    }

    public double getRelativeSpeed(final BuilderSupport support) {
        return this.relativeSpeed.get(support.getExecutionContext());
    }

    public double getRadius(final BuilderSupport support) {
        return this.radius.get(support.getExecutionContext());
    }

    public double getOscillateRadiusFrequency(final BuilderSupport support) {
        return this.oscillateRadiusFrequency.get(support.getExecutionContext());
    }

    public double getOscillateRadiusAmplitude(final BuilderSupport support) {
        return this.oscillateRadiusAmplitude.get(support.getExecutionContext());
    }

    public double getOscillateYFrequency(final BuilderSupport support) {
        return this.oscillateYFrequency.get(support.getExecutionContext());
    }

    public double getOscillateYAmplitude(final BuilderSupport support) {
        return this.oscillateYAmplitude.get(support.getExecutionContext());
    }
}
