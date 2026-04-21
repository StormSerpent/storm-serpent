package me.nullicorn.hytale.stormserpent.npc.action.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.DoubleHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.DoubleSingleValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import me.nullicorn.hytale.stormserpent.npc.action.ActionEnterBurrow;

import javax.annotation.Nullable;

public final class BuilderActionEnterBurrow extends BuilderActionBase {
    public static final String COMPONENT_ID = "StormSerpentEnterBurrow";

    private final DoubleHolder relativeSpeed = new DoubleHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return ""; // TODO
    }

    @Nullable
    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<Action> readConfig(final JsonElement data) {
        this.getDouble(data, "RelativeSpeed", this.relativeSpeed, 1.0, DoubleSingleValidator.greaterEqual0(), BuilderDescriptorState.WorkInProgress, "Relative movement speed", null);
        return this;
    }

    @Override
    public Action build(final BuilderSupport builderSupport) {
        return new ActionEnterBurrow(this, builderSupport);
    }

    public double getRelativeSpeed(final BuilderSupport support) {
        return this.relativeSpeed.get(support.getExecutionContext());
    }
}
