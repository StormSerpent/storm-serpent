package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.assetstore.map.IndexedAssetMap;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;

import javax.annotation.Nonnull;

public class StormSerpentAmbienceSystem extends EntityTickingSystem<EntityStore> {
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(StormSerpentBone.getComponentType(), NetworkId.getComponentType());
    }

    @Override
    public void tick(
        final float dt,
        final int index,
        @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull final Store<EntityStore> store,
        @Nonnull final CommandBuffer<EntityStore> commandBuffer
    ) {
        final int soundEventIndex = SoundEvent.getAssetMap().getIndex("SFX_Serpent_Storm_Drone");
        if (soundEventIndex == IndexedAssetMap.NOT_FOUND) {
            return;
        }

        // TODO: This needs changed! Right now it constantly broadcasts the sound to every player in the world, every
        //       tick, for every bone. This is temporary, just to get things working.
        final var networkId = archetypeChunk.getComponent(index, NetworkId.getComponentType());
        assert networkId != null;
        SoundUtil.playSoundEventEntity(soundEventIndex, networkId.getId(), commandBuffer);
    }
}
