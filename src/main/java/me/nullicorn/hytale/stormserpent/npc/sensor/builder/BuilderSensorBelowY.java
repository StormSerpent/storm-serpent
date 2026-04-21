package me.nullicorn.hytale.stormserpent.npc.sensor.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import me.nullicorn.hytale.stormserpent.npc.sensor.SensorBelowY;

import javax.annotation.Nullable;

public final class BuilderSensorBelowY extends BuilderSensorBase {
    public static final String COMPONENT_ID = "StormSerpentBelowY";

    private final DoubleHolder y = new DoubleHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Test if the NPC's y coordinate is below a certain threshold";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<Sensor> readConfig(final JsonElement data) {
        this.requireDouble(data, "Y", this.y, null, BuilderDescriptorState.WorkInProgress, "y coordinate to test against", null);
        return this;
    }

    @Override
    public Sensor build(final BuilderSupport builderSupport) {
        return new SensorBelowY(this, builderSupport);
    }

    public double getY(final BuilderSupport support) {
        return this.y.get(support.getExecutionContext());
    }
}
