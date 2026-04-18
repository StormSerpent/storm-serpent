package me.nullicorn.hytale.stormserpent.npc.sensor.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.Feature;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import me.nullicorn.hytale.stormserpent.npc.sensor.SensorReadLeashPosition;

import javax.annotation.Nullable;

public final class BuilderSensorReadLeashPosition extends BuilderSensorBase {
    public static final String COMPONENT_ID = "StormSerpentReadLeashPosition";

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Read the leash position";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<Sensor> readConfig(final JsonElement data) {
        this.provideFeature(Feature.Position);
        return this;
    }

    @Override
    public Sensor build(final BuilderSupport builderSupport) {
        return new SensorReadLeashPosition(this, builderSupport);
    }
}
