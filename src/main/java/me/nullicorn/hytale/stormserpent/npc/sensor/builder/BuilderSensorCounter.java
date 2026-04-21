package me.nullicorn.hytale.stormserpent.npc.sensor.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntSequenceValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import com.hypixel.hytale.server.npc.valuestore.ValueStoreValidator;
import me.nullicorn.hytale.stormserpent.npc.sensor.SensorCounter;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.ToIntFunction;

public final class BuilderSensorCounter extends BuilderSensorBase {
    public static final String COMPONENT_ID = "StormSerpentCounter";

    @Nullable
    private String name;
    @Nullable
    private ToIntFunction<BuilderSupport> slot;
    private final NumberArrayHolder range = new NumberArrayHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Test if a counter matches a range";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<Sensor> readConfig(final JsonElement data) {
        this.requireString(data, "Name", name -> this.name = name, StringNotEmptyValidator.get(), BuilderDescriptorState.WorkInProgress, "Value store slot of the counter", null);
        this.requireIntRange(data, "Range", this.range, IntSequenceValidator.betweenWeaklyMonotonic(Integer.MIN_VALUE, Integer.MAX_VALUE), BuilderDescriptorState.WorkInProgress, "Inclusive range to test against the counter", null);
        if (!this.isCreatingDescriptor() && this.name != null) {
            this.slot = this.requireIntValueStoreParameter(this.name, ValueStoreValidator.UseType.READ);
        }
        return this;
    }

    @Override
    public Sensor build(final BuilderSupport builderSupport) {
        return new SensorCounter(this, builderSupport);
    }

    public int getSlot(final BuilderSupport support) {
        return Objects.requireNonNull(this.slot).applyAsInt(support);
    }

    public int[] getRange(final BuilderSupport support) {
        return this.range.getIntArray(support.getExecutionContext());
    }
}
