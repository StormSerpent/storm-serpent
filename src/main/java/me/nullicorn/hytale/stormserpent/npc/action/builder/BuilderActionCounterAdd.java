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
import me.nullicorn.hytale.stormserpent.npc.action.ActionCounterAdd;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.ToIntFunction;

public final class BuilderActionCounterAdd extends BuilderActionBase {
    public static final String COMPONENT_ID = "StormSerpentCounterAdd";

    @Nullable
    private String name;
    @Nullable
    private ToIntFunction<BuilderSupport> slot;
    private final IntHolder amount = new IntHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Adds a value to a counter";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<Action> readConfig(final JsonElement data) {
        this.requireString(data, "Name", name -> this.name = name, StringNotEmptyValidator.get(), BuilderDescriptorState.WorkInProgress, "Value store slot of the counter", null);
        this.requireInt(data, "Amount", this.amount, null, BuilderDescriptorState.WorkInProgress, "Amount to add to the counter", null);
        if (!this.isCreatingDescriptor() && this.name != null) {
            this.slot = this.requireIntValueStoreParameter(this.name, ValueStoreValidator.UseType.WRITE);
        }
        return this;
    }

    @Override
    public Action build(final BuilderSupport builderSupport) {
        return new ActionCounterAdd(this, builderSupport);
    }

    public int getSlot(final BuilderSupport support) {
        return Objects.requireNonNull(this.slot).applyAsInt(support);
    }

    public int getAmount(final BuilderSupport support) {
        return this.amount.get(support.getExecutionContext());
    }
}
