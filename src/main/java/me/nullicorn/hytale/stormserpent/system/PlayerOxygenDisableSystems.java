package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.assetstore.map.IndexedAssetMap;
import com.hypixel.hytale.builtin.hytalegenerator.plugin.Handle;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.protocol.packets.interface_.HudComponent;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PlayerOxygenDisableSystems {
    // TODO: Don't hardcode these.
    private static final String WORLD_STRUCTURE_NAME = "Serpent_Storm";
    private static final String OXYGEN_IMMUNITY_EFFECT_KEY = "Immunity_Oxygen";

    private static boolean isStormSerpentWorld(final World world) {
        return world.getChunkStore().getGenerator() instanceof final Handle worldGen && worldGen.getProfile().worldStructureName().equals(WORLD_STRUCTURE_NAME);
    }

    private static void applyOxygenImmunityEffect(
        final EffectControllerComponent effectController,
        final Ref<EntityStore> ref,
        final ComponentAccessor<EntityStore> componentAccessor
    ) {
        // Apply total resistance to "Suffocation" and "Drowning" damage types.
        final var oxygenImmunityEffect = EntityEffect.getAssetMap().getAsset(OXYGEN_IMMUNITY_EFFECT_KEY);
        if (oxygenImmunityEffect != null) {
            effectController.addEffect(ref, oxygenImmunityEffect, componentAccessor);
        }
    }

    /**
     * Applies immunity to new players entering the world, and removes it from players leaving.
     */
    public static final class ChangeWorldSystem extends RefSystem<EntityStore> {
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType(), PlayerRef.getComponentType(), EntityStatMap.getComponentType(), EffectControllerComponent.getComponentType());
        }


        @Override
        public void onEntityAdded(
            @Nonnull final Ref<EntityStore> ref,
            @Nonnull final AddReason reason,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer
        ) {
            if (!isStormSerpentWorld(store.getExternalData().getWorld())) {
                return;
            }

            final var player = commandBuffer.getComponent(ref, Player.getComponentType());
            final var playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
            final var effectController = commandBuffer.getComponent(ref, EffectControllerComponent.getComponentType());
            assert player != null && playerRef != null && effectController != null;

            // Hide oxygen HUD.
            player.getHudManager().hideHudComponents(playerRef, HudComponent.Oxygen);

            applyOxygenImmunityEffect(effectController, ref, commandBuffer);
        }

        @Override
        public void onEntityRemove(
            @Nonnull final Ref<EntityStore> ref,
            @Nonnull final RemoveReason reason,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer
        ) {
            if (!isStormSerpentWorld(store.getExternalData().getWorld())) {
                return;
            }

            final var player = commandBuffer.getComponent(ref, Player.getComponentType());
            final var playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
            final var effectController = commandBuffer.getComponent(ref, EffectControllerComponent.getComponentType());
            assert player != null && playerRef != null && effectController != null;

            // Re-enable oxygen HUD.
            player.getHudManager().showHudComponents(playerRef, HudComponent.Oxygen);

            // Remove resistance to "Suffocation" and "Drowning" damage types.
            final var oxygenImmunityEffectIndex = EntityEffect.getAssetMap().getIndex(OXYGEN_IMMUNITY_EFFECT_KEY);
            if (oxygenImmunityEffectIndex != IndexedAssetMap.NOT_FOUND) {
                effectController.removeEffect(ref, oxygenImmunityEffectIndex, commandBuffer);
            }
        }
    }

    /**
     * Applies immunity to players when they respawn after dying.
     */
    public static final class RespawnSystem extends RefChangeSystem<EntityStore, DeathComponent> {
        @Nonnull
        @Override
        public ComponentType<EntityStore, DeathComponent> componentType() {
            return DeathComponent.getComponentType();
        }

        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType(), EffectControllerComponent.getComponentType());
        }

        @Override
        public void onComponentAdded(
            @Nonnull final Ref<EntityStore> ref,
            @Nonnull final DeathComponent component,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer
        ) {
        }

        @Override
        public void onComponentSet(
            @Nonnull final Ref<EntityStore> ref,
            @Nullable final DeathComponent oldComponent,
            @Nonnull final DeathComponent newComponent,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer
        ) {
        }

        @Override
        public void onComponentRemoved(
            @Nonnull final Ref<EntityStore> ref,
            @Nonnull final DeathComponent component,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer
        ) {
            if (isStormSerpentWorld(store.getExternalData().getWorld())) {
                final var effectController = commandBuffer.getComponent(ref, EffectControllerComponent.getComponentType());
                assert effectController != null;
                applyOxygenImmunityEffect(effectController, ref, commandBuffer);
            }
        }
    }

    private PlayerOxygenDisableSystems() {
    }
}
