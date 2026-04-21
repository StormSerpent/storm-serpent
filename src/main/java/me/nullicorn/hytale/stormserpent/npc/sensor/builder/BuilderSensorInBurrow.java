package me.nullicorn.hytale.stormserpent.npc.sensor.builder;

import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import me.nullicorn.hytale.stormserpent.npc.sensor.SensorInBurrow;

import javax.annotation.Nullable;

public final class BuilderSensorInBurrow extends BuilderSensorBase {
    public static final String COMPONENT_ID = "StormSerpentInBurrow";

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Sensor build(final BuilderSupport builderSupport) {
        return new SensorInBurrow(this, builderSupport);
    }
}
