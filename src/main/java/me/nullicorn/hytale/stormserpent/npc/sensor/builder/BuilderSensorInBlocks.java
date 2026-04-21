package me.nullicorn.hytale.stormserpent.npc.sensor.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import me.nullicorn.hytale.stormserpent.npc.sensor.SensorInBlocks;

import javax.annotation.Nullable;

public final class BuilderSensorInBlocks extends BuilderSensorBase {
    public static final String COMPONENT_ID = "StormSerpentInBlocks";

    private final DoubleHolder threshold = new DoubleHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Test how much of the NPC hitbox intersects solid blocks";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<Sensor> readConfig(final JsonElement data) {
        this.getDouble(data, "Threshold", this.threshold, 0.5, DoubleSingleValidator.greater0(), BuilderDescriptorState.WorkInProgress, "Percent of the hitbox intersection solid blocks", null);
        return this;
    }

    @Override
    public Sensor build(final BuilderSupport builderSupport) {
        return new SensorInBlocks(this, builderSupport);
    }

    public double getThreshold(final BuilderSupport support) {
        return this.threshold.get(support.getExecutionContext());
    }
}
