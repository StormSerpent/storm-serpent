package me.nullicorn.hytale.stormserpent.npc.action.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import me.nullicorn.hytale.stormserpent.npc.action.ActionStormSerpentBeginFight;
import me.nullicorn.serpentine.asset.SerpentConfig;

public final class BuilderActionStormSerpentBeginFight extends BuilderActionBase {
    public static final String COMPONENT_ID = "StormSerpentBeginFight";

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress; // TODO
    }

    @Override
    public String getShortDescription() {
        return ""; // TODO
    }

    @Override
    public String getLongDescription() {
        return ""; // TODO
    }

    @Override
    public Builder<Action> readConfig(final JsonElement data) {
        return this;
    }

    @Override
    public Action build(final BuilderSupport builderSupport) {
        return new ActionStormSerpentBeginFight(this, builderSupport);
    }
}
