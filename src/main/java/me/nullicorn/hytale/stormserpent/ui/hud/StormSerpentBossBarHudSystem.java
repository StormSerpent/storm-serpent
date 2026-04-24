package me.nullicorn.hytale.stormserpent.ui.hud;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.StormSerpentPlugin;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.system.StormSerpentSpatialSystem;

import javax.annotation.Nonnull;
import java.util.Set;

public final class StormSerpentBossBarHudSystem extends EntityTickingSystem<EntityStore> {
    private static final String HUD_KEY = "stormSerpentBossBar";
    private static final float INTERPOLATION_RATE = 3.0f;

    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(
            new SystemDependency<>(Order.AFTER, StormSerpentSpatialSystem.class)
        );
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType(), TransformComponent.getComponentType());
    }

    @Override
    public void tick(
        final float dt,
        final int index,
        @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
        @Nonnull final Store<EntityStore> store,
        @Nonnull final CommandBuffer<EntityStore> commandBuffer
    ) {
        final var serpentSpatialResource = commandBuffer.getResource(StormSerpentPlugin.get().getStormSerpentSpatialResourceType());
        final var player = archetypeChunk.getComponent(index, Player.getComponentType());
        final var playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
        final var playerTransform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
        assert player != null && playerRef != null && playerTransform != null;
        final var hudManager = player.getHudManager();

        if (serpentSpatialResource.getSpatialData().size() == 0) {
            removeBossBarHud(hudManager, playerRef);
            return;
        }

        final Ref<EntityStore> serpentRef = serpentSpatialResource.getSpatialStructure().closest(playerTransform.getPosition());
        if (serpentRef != null && serpentRef.isValid()) {
            final var stormSerpent = store.getComponent(serpentRef, StormSerpent.getComponentType());
            if (stormSerpent != null && stormSerpent.inCombat) {
                final Float serpentHealth = tryGetHealthPercent(serpentRef, commandBuffer);
                if (serpentHealth != null) {
                    final var bossBarHud = ensureBossBarHud(hudManager, playerRef);
                    bossBarHud.set(serpentHealth);
                    bossBarHud.tick(dt * INTERPOLATION_RATE);
                    return;
                }
            }
        }
        removeBossBarHud(hudManager, playerRef);
    }

    private static StormSerpentBossBarHud ensureBossBarHud(final HudManager hudManager, final PlayerRef playerRef) {
        if (hudManager.getCustomHud(HUD_KEY) instanceof final StormSerpentBossBarHud bossBarHud) {
            return bossBarHud;
        }
        final var bossBarHud = new StormSerpentBossBarHud(playerRef, HUD_KEY, 0, MathUtil::lerp);
        hudManager.addCustomHud(playerRef, bossBarHud);
        hudManager.hideHudComponents(playerRef, HudComponent.Compass);
        return bossBarHud;
    }

    private static void removeBossBarHud(final HudManager hudManager, final PlayerRef playerRef) {
        hudManager.removeCustomHud(playerRef, HUD_KEY);
        hudManager.showHudComponents(playerRef, HudComponent.Compass);
    }

    private static Float tryGetHealthPercent(
        final Ref<EntityStore> ref,
        final ComponentAccessor<EntityStore> componentAccessor
    ) {
        final EntityStatMap stats = componentAccessor.getComponent(ref, EntityStatMap.getComponentType());
        if (stats == null) {
            return null;
        }
        final EntityStatValue healthValue = stats.get(DefaultEntityStatTypes.getHealth());
        if (healthValue == null) {
            return null;
        }
        final float healthPercent = healthValue.asPercentage();
        if (!Float.isFinite(healthPercent)) {
            return null;
        }
        return healthPercent;
    }
}
