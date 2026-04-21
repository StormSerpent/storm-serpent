package me.nullicorn.hytale.stormserpent.npc.action.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.IntHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import com.hypixel.hytale.server.npc.valuestore.ValueStoreValidator;
import me.nullicorn.hytale.stormserpent.npc.action.ActionCounterSet;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.ToIntFunction;

public final class BuilderActionCounterSet extends BuilderActionBase {
    public static final String COMPONENT_ID = "StormSerpentCounterSet";

    @Nullable
    private String name;
    @Nullable
    private ToIntFunction<BuilderSupport> slot;
    private final IntHolder value = new IntHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Sets a counter to a value";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<Action> readConfig(final JsonElement data) {
        this.requireString(data, "Name", name -> this.name = name, StringNotEmptyValidator.get(), BuilderDescriptorState.WorkInProgress, "Value store slot of the counter", null);
        this.requireInt(data, "Value", this.value, null, BuilderDescriptorState.WorkInProgress, "Value to set the counter to", null);
        if (!this.isCreatingDescriptor() && this.name != null) {
            this.slot = this.requireIntValueStoreParameter(this.name, ValueStoreValidator.UseType.WRITE);
        }
        return this;
    }

    @Override
    public Action build(final BuilderSupport builderSupport) {
        return new ActionCounterSet(this, builderSupport);
    }

    public int getSlot(final BuilderSupport support) {
        return Objects.requireNonNull(this.slot).applyAsInt(support);
    }

    public int getValue(final BuilderSupport support) {
        return this.value.get(support.getExecutionContext());
    }
}
