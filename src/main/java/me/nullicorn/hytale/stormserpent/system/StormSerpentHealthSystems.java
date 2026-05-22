package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.event.events.ecs.BreathingCheckEvent;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsSystems;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.systems.NPCSystems;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.serpentine.component.SerpentBone;

import javax.annotation.Nonnull;
import java.util.Set;

public final class StormSerpentHealthSystems {
    /**
     * Transfers inflicted damage from body segments to the head.
     */
    public static final class SegmentDamageSystem extends DamageEventSystem {
        @Override
        public SystemGroup<EntityStore> getGroup() {
            return DamageModule.get().getFilterDamageGroup();
        }

        @Nonnull
        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Set.of(
                new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getGatherDamageGroup())
            );
        }

        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(StormSerpentBone.getComponentType(), SerpentBone.getComponentType());
        }

        @Override
        public void handle(
            final int index,
            @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer,
            @Nonnull final Damage damage
        ) {
            final SerpentBone serpentBone = archetypeChunk.getComponent(index, SerpentBone.getComponentType());
            assert serpentBone != null;

            // TODO: This is a band-aid fix to cancel void damage. Find a more flexible way to implement this.
            final DamageCause cause = DamageCause.getAssetMap().getAsset(damage.getDamageCauseIndex());
            if (cause != null && cause.getId().equals("OutOfWorld")) {
                damage.setCancelled(true);
                return;
            }

            if (serpentBone.index() == 0) {
                // Bone 0 is the head NPC itself so we don't need to transfer the damage anywhere.
                return;
            }

            final Ref<EntityStore> serpentRef = serpentBone.serpent();
            if (serpentRef == null || !serpentRef.isValid()) {
                return;
            }

            damage.setCancelled(true);

            // TODO: Figure out how to copy the damage's MetaStore over to our new instance.
            final Damage npcDamage = new Damage(damage.getSource(), damage.getDamageCauseIndex(), damage.getInitialAmount());
            npcDamage.setAmount(damage.getAmount());
            DamageSystems.executeDamage(serpentRef, commandBuffer, npcDamage);
        }
    }

    /**
     * Prevents Storm Serpents from suffocating inside solid blocks and fluids.
     */
    public static final class DisableSuffocationSystem extends EntityEventSystem<EntityStore, BreathingCheckEvent> {
        public DisableSuffocationSystem() {
            super(BreathingCheckEvent.class);
        }

        @Nonnull
        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Set.of(
                // Run after the normal NPC breathing check so that we can overwrite its result.
                new SystemDependency<>(Order.AFTER, NPCSystems.BreathingCheckEventSystem.class)
            );
        }

        @Override
        public Query<EntityStore> getQuery() {
            return StormSerpent.getComponentType();
        }

        @Override
        public void handle(
            final int index,
            @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer,
            @Nonnull final BreathingCheckEvent event
        ) {
            event.setCanBreathe(true);
        }
    }

    /**
     * Runs each tick before {@link NPCPlugin.NPCEntityRegenerateStatsSystem} to save each serpent's health. After
     * {@link NPCPlugin.NPCEntityRegenerateStatsSystem} runs, the saved value is restored by
     * {@link RestoreHealthPostRegenSystem} to revert the regeneration.
     */
    public static final class CopyHealthPreRegenSystem extends EntityTickingSystem<EntityStore> {
        @Nonnull
        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Set.of(
                new SystemDependency<>(Order.BEFORE, NPCPlugin.NPCEntityRegenerateStatsSystem.class, OrderPriority.CLOSEST)
            );
        }

        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(StormSerpent.getComponentType(), EntityStatMap.getComponentType());
        }

        @Override
        public void tick(
            final float dt,
            final int index,
            @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer
        ) {
            final StormSerpent stormSerpent = archetypeChunk.getComponent(index, StormSerpent.getComponentType());
            final EntityStatMap statMap = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
            assert stormSerpent != null && statMap != null;

            final EntityStatValue healthValue = statMap.get(DefaultEntityStatTypes.getHealth());
            if (healthValue != null) {
                stormSerpent.health = healthValue.get();
            } else {
                stormSerpent.health = null;
            }
        }
    }

    public static final class RestoreHealthPostRegenSystem extends EntityTickingSystem<EntityStore> implements EntityStatsSystems.StatModifyingSystem {
        @Nonnull
        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Set.of(
                new SystemDependency<>(Order.AFTER, NPCPlugin.NPCEntityRegenerateStatsSystem.class, OrderPriority.CLOSEST)
            );
        }

        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(StormSerpent.getComponentType(), EntityStatMap.getComponentType());
        }

        @Override
        public void tick(
            final float dt,
            final int index,
            @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer
        ) {
            final StormSerpent stormSerpent = archetypeChunk.getComponent(index, StormSerpent.getComponentType());
            final EntityStatMap statMap = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
            assert stormSerpent != null && statMap != null;
            if (stormSerpent.health != null) {
                stormSerpent.health = statMap.setStatValue(DefaultEntityStatTypes.getHealth(), stormSerpent.health);
            }
        }
    }

    private StormSerpentHealthSystems() {
    }
}
