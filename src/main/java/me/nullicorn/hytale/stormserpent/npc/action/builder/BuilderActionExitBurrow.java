package me.nullicorn.hytale.stormserpent.npc.action.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.NumberArrayHolder;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSequenceValidator;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNotEmptyValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import me.nullicorn.hytale.stormserpent.npc.action.ActionExitBurrow;

import javax.annotation.Nullable;

public final class BuilderActionExitBurrow extends BuilderActionBase {
    public static final String COMPONENT_ID = "StormSerpentExitBurrow";

    private final StringHolder targetSlot = new StringHolder();
    private final NumberArrayHolder distanceRange = new NumberArrayHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Emerge from being burrowed";
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<Action> readConfig(final JsonElement data) {
        this.requireString(data, "TargetSlot", this.targetSlot, StringNotEmptyValidator.get(), BuilderDescriptorState.WorkInProgress, "Target to find an exit position around", null);
        this.getDoubleRange(data, "DistanceRange", this.distanceRange, new double[]{200, 300}, DoubleSequenceValidator.betweenWeaklyMonotonic(0.0, Double.POSITIVE_INFINITY), BuilderDescriptorState.WorkInProgress, "Distance away from the target to emerge from", null);
        return this;
    }

    @Override
    public Action build(final BuilderSupport builderSupport) {
        return new ActionExitBurrow(this, builderSupport);
    }

    public int getTargetSlot(final BuilderSupport support) {
        return support.getTargetSlot(this.targetSlot.get(support.getExecutionContext()));
    }

    public double[] getDistanceRange(final BuilderSupport support) {
        return this.distanceRange.get(support.getExecutionContext());
    }
}
