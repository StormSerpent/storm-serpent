package me.nullicorn.hytale.stormserpent.ui;

import com.hypixel.hytale.assetstore.map.IndexedAssetMap;
import com.hypixel.hytale.builtin.audio.components.ForcedMusicTracker;
import com.hypixel.hytale.builtin.audio.systems.ForcedMusicSystems;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.asset.type.musiccontainer.config.MusicContainer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.StormSerpentPlugin;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.system.StormSerpentSpatialSystem;

import javax.annotation.Nonnull;
import java.util.Set;

public final class StormSerpentMusicSystem extends TickingSystem<EntityStore> {
    private static final String FIGHT_MUSIC_CONTAINER_KEY = "Track_Serpent_Storm_Fight";

    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(
            new SystemDependency<>(Order.AFTER, StormSerpentSpatialSystem.class),
            new SystemDependency<>(Order.BEFORE, ForcedMusicSystems.Tick.class)
        );
    }

    @Override
    public void tick(final float dt, final int systemIndex, @Nonnull final Store<EntityStore> store) {
        final int fightMusicContainerIndex = MusicContainer.getAssetMap().getIndex(FIGHT_MUSIC_CONTAINER_KEY);
        if (fightMusicContainerIndex == IndexedAssetMap.NOT_FOUND) {
            return;
        }

        final var serpentSpatialResource = store.getResource(StormSerpentPlugin.get().getStormSerpentSpatialResourceType());

        // Check if a fight has been started with any serpent in the world.
        for (int i = 0; i < serpentSpatialResource.getSpatialData().size(); i++) {
            final Ref<EntityStore> ref = serpentSpatialResource.getSpatialData().getData(i);
            if (ref.isValid()) {
                final StormSerpent stormSerpent = store.getComponent(ref, StormSerpent.getComponentType());
                if (stormSerpent != null && stormSerpent.inCombat) {
                    // Start fight music. Does nothing if it is already playing.
                    playTrackGlobally(store, fightMusicContainerIndex);
                    return;
                }
            }
        }

        // No active fights, so stop any fight music already playing.
        stopTrackGlobally(store, fightMusicContainerIndex);
    }

    /**
     * Plays the music container with {@code containerIndex} to all recipients in the world.
     */
    private static void playTrackGlobally(final Store<EntityStore> store, final int containerIndex) {
        store.forEachEntityParallel(ForcedMusicTracker.getComponentType(), (index, archetypeChunk, _commandBuffer) -> {
            final var forcedMusicTracker = archetypeChunk.getComponent(index, ForcedMusicTracker.getComponentType());
            assert forcedMusicTracker != null;
            forcedMusicTracker.setCurrentContainerIndex(containerIndex);
        });
    }

    /**
     * Removes the forced music container with {@code containerIndex} from any recipients currently hearing it.
     */
    private static void stopTrackGlobally(final Store<EntityStore> store, final int containerIndex) {
        store.forEachEntityParallel(ForcedMusicTracker.getComponentType(), (index, archetypeChunk, _commandBuffer) -> {
            final var forcedMusicTracker = archetypeChunk.getComponent(index, ForcedMusicTracker.getComponentType());
            assert forcedMusicTracker != null;
            if (forcedMusicTracker.getCurrentContainerIndex() == containerIndex) {
                forcedMusicTracker.setCurrentContainerIndex(MusicContainer.EMPTY_ID);
            }
        });
    }
}
