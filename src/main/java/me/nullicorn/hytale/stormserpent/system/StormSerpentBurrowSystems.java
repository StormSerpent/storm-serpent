package me.nullicorn.hytale.stormserpent.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.hytale.stormserpent.solver.EnteringBurrowJointSolver;
import me.nullicorn.serpentine.component.Serpent;
import me.nullicorn.serpentine.component.SerpentBone;
import me.nullicorn.serpentine.component.SerpentBoneAutoApplyScale;
import me.nullicorn.serpentine.system.SerpentSolverSystem;

import javax.annotation.Nonnull;
import java.util.Set;

public final class StormSerpentBurrowSystems {
    public static final class EnterBurrowBoneTickingSystem extends EntityTickingSystem<EntityStore> {
        @Nonnull
        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Set.of(
                new SystemDependency<>(Order.AFTER, SerpentSolverSystem.class)
            );
        }

        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(
                StormSerpentBone.getComponentType(),
                SerpentBone.getComponentType(),
                TransformComponent.getComponentType()
            );
        }

        @Override
        public void tick(
            final float dt,
            final int index,
            @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk,
            @Nonnull final Store<EntityStore> store,
            @Nonnull final CommandBuffer<EntityStore> commandBuffer
        ) {
            final SerpentBone bone = archetypeChunk.getComponent(index, SerpentBone.getComponentType());
            assert bone != null;
            final Ref<EntityStore> serpentRef = bone.serpent();
            if (!serpentRef.isValid()) {
                return;
            }
            final Serpent serpent = commandBuffer.getComponent(serpentRef, Serpent.getComponentType());
            if (serpent == null) {
                return;
            }
            final StormSerpent stormSerpent = commandBuffer.getComponent(bone.serpent(), StormSerpent.getComponentType());
            if (stormSerpent == null) {
                return;
            }

            if (stormSerpent.burrowStatus == StormSerpent.BurrowStatus.ENTERING && serpent.jointSolver() instanceof final EnteringBurrowJointSolver jointSolver) {
                final Ref<EntityStore> boneRef = archetypeChunk.getReferenceTo(index);
                final Serpent.Bone boneInfo = serpent.bones().get(bone.index());
                final double distance = jointSolver.getDistance(bone.index() + 1);
                final double boneLength = boneInfo.baseLength() * boneInfo.scale();

                boneInfo.setAutoSpawn(false);
                commandBuffer.tryRemoveComponent(boneRef, SerpentBoneAutoApplyScale.getComponentType());

                if (distance <= 0 && archetypeChunk.getComponent(index, StormSerpent.getComponentType()) == null) {
                    commandBuffer.removeEntity(boneRef, RemoveReason.REMOVE);
                } else if (distance < boneLength) {
                    // FIXME: Capping at 0.2 is a band-aid fix for specifically the head entity not shrinking all the
                    //        way to 0. Look into this.
                    final double scale = Math.max(distance / boneInfo.baseLength(), 0.2);
                    commandBuffer.putComponent(boneRef, EntityScaleComponent.getComponentType(), new EntityScaleComponent((float) scale));
                }

                if (distance <= 0 && bone.index() == serpent.bones().size() - 1) {
                    stormSerpent.burrowStatus = StormSerpent.BurrowStatus.IN_BURROW;
                }
            }
        }
    }

    private StormSerpentBurrowSystems() {
    }
}
