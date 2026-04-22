package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.*;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.serpentine.component.SerpentBone;

import javax.annotation.Nonnull;
import java.util.Set;

public final class StormSerpentHealthSystems {
    public static final class DamageSystem extends DamageEventSystem {
        private static final Set<String> IMMUNE_DAMAGE_CAUSES = Set.of("Suffocation", "OutOfWorld");

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

            // Always ignore certain types of damage.
            // TODO: This is a band-aid fix for void and suffocation damage. Find a more flexible way to implement this.
            final DamageCause cause = DamageCause.getAssetMap().getAsset(damage.getDamageCauseIndex());
            if (cause != null && IMMUNE_DAMAGE_CAUSES.contains(cause.getId())) {
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

    private StormSerpentHealthSystems() {
    }
}
