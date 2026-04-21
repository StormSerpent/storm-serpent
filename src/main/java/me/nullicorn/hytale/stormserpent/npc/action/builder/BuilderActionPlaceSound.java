package me.nullicorn.hytale.stormserpent.npc.action.builder;

import com.google.gson.JsonElement;
import com.hypixel.hytale.assetstore.map.AssetMapWithIndexes;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.asset.SoundEventExistsValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import me.nullicorn.hytale.stormserpent.npc.action.ActionPlaceSound;

public final class BuilderActionPlaceSound extends BuilderActionBase {
    public static final String COMPONENT_ID = "StormSerpentPlaceSound";

    private final AssetHolder soundEventId = new AssetHolder();

    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public String getShortDescription() {
        return "Plays a sound at the NPC's current position";
    }

    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public Builder<Action> readConfig(final JsonElement data) {
        this.requireAsset(data, "SoundEventId", this.soundEventId, SoundEventExistsValidator.required(), BuilderDescriptorState.WorkInProgress, "Sound event to play", null);
        return this;
    }

    @Override
    public Action build(final BuilderSupport builderSupport) {
        return new ActionPlaceSound(this, builderSupport);
    }

    public int getSoundEventIndex(final BuilderSupport support) {
        final String key = this.soundEventId.get(support.getExecutionContext());
        final int index = SoundEvent.getAssetMap().getIndex(key);
        if (index == AssetMapWithIndexes.NOT_FOUND) {
            throw new IllegalArgumentException("Unknown key! " + key);
        } else {
            return index;
        }
    }
}
