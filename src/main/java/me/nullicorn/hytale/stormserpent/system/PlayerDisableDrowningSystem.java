package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.builtin.hytalegenerator.plugin.Handle;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreathingCheckEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Prevents players from drowning in Storm Serpent worlds.
 */
public final class PlayerDisableDrowningSystem extends EntityEventSystem<EntityStore, BreathingCheckEvent> {
    // TODO: Don't hardcode this.
    private static final String WORLD_STRUCTURE_NAME = "Serpent_Storm";

    private static boolean isStormSerpentWorld(final World world) {
        return world.getChunkStore().getGenerator() instanceof final Handle worldGen
            && worldGen.getProfile().worldStructureName().equals(WORLD_STRUCTURE_NAME);
    }

    public PlayerDisableDrowningSystem() {
        super(BreathingCheckEvent.class);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @Override
    public void handle(
        final int index,
        @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull final Store<EntityStore> store,
        @Nonnull final CommandBuffer<EntityStore> commandBuffer,
        @Nonnull final BreathingCheckEvent event
    ) {
        // In Storm Serpent worlds only suffocate inside solid blocks; skip the Fluid ID check.
        if (!event.canBreathe() && event.getBreathingMaterial() == BlockMaterial.Empty && isStormSerpentWorld(store.getExternalData().getWorld())) {
            event.setCanBreathe(true);
        }
    }
}
