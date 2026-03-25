package me.nullicorn.hytale.stormserpent.npc.action;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import me.nullicorn.hytale.stormserpent.npc.SerpentConfigExistsValidator;
import me.nullicorn.serpentine.asset.SerpentConfig;

// TODO: Move to Serpentine plugin.
public final class BuilderActionSerpentCreateBody extends BuilderActionBase {
    private final AssetHolder serpentConfig = new AssetHolder();

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
        this.requireAsset(
            data,
            "SerpentConfig",
            this.serpentConfig,
            SerpentConfigExistsValidator.required(),
            BuilderDescriptorState.WorkInProgress, // TODO
            "", // TODO
            "" // TODO
        );
        return super.readConfig(data);
    }

    @Override
    public Action build(final BuilderSupport builderSupport) {
        return new ActionSerpentCreateBody(this, builderSupport);
    }

    public SerpentConfig serpentConfig(final BuilderSupport support) {
        return SerpentConfig.getAssetMap().getAsset(this.serpentConfig.get(support.getExecutionContext()));
    }
}
