package me.nullicorn.hytale.stormserpent.ui.hud;

import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.Set;

public final class StormSerpentBossBarHudSystem extends EntityTickingSystem<EntityStore> {
    private static final String HUD_KEY = "stormSerpentBossBar";
    private static final float INTERPOLATION_RATE = 3.0f;

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
    }

    @Override
    public void tick(
        final float dt,
        final int index,
        @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull final Store<EntityStore> store,
        @Nonnull final CommandBuffer<EntityStore> commandBuffer
    ) {
        final Player player = archetypeChunk.getComponent(index, Player.getComponentType());
        assert player != null;

        final PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
        assert playerRef != null;

        if (player.getHudManager().getCustomHud(HUD_KEY) instanceof final StormSerpentBossBarHud bossBarHud) {
            // TODO: This just uses the % of unlocked memories as placeholder. This needs to be the serpent's health!
            bossBarHud.set((float) MemoriesPlugin.get().getRecordedMemories().size() / MemoriesPlugin.get().getAllMemories().values().stream().mapToInt(Set::size).sum());
            bossBarHud.tick(dt * INTERPOLATION_RATE);
        } else {
            final StormSerpentBossBarHud bossBarHud = new StormSerpentBossBarHud(playerRef, HUD_KEY, 0, MathUtil::lerp);
            player.getHudManager().addCustomHud(playerRef, bossBarHud);
            player.getHudManager().hideHudComponents(playerRef, HudComponent.Compass);
            bossBarHud.tick(dt * INTERPOLATION_RATE);
        }
    }
}
