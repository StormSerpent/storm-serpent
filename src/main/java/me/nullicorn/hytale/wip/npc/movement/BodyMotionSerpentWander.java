package me.nullicorn.hytale.wip.npc.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.BodyMotionBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.wip.npc.movement.builder.BuilderBodyMotionSerpentWander;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class BodyMotionSerpentWander extends BodyMotionBase {
    /**
     * Bounding volume for randomly choosing {@link #targetPosition} within, relative to the NPC's leash point.
     */
    private final Box wanderTargetZone = new Box(
        /* min: */ new Vector3d().assign(-200.0),
        /* max: */ new Vector3d(200.0, 50.0, 200.0)
    );
    /**
     * Minimum amount of time to try reaching a given {@link #targetPosition} before choosing a new one.
     * <p>
     * Used as the lower bound for choosing a random time.
     * <p>
     * Unit is seconds.
     */
    private final double wanderMinTargetTimer = 0.5;
    /**
     * Max amount of time to try reaching a given {@link #targetPosition} before choosing a new one.
     * <p>
     * Used as the upper bound for choosing a random time.
     * <p>
     * Unit is seconds.
     */
    private final double wanderMaxTargetTimer = 2.5;
    /**
     * How close the NPC must be to {@link #targetPosition} to reach it and choose a new one.
     * <p>
     * Unit is meters.
     */
    private final double wanderTargetRadius = 10.0;

    /**
     * The point the NPC is currently being steered toward.
     * <p>
     * This is {@code null} before {@link #computeSteering} is first run.
     */
    @Nullable
    private Vector3d targetPosition;
    /**
     * Time left until {@link #targetPosition} is reselected if the NPC doesn't reach it.
     * <p>
     * Unit is seconds.
     */
    private double targetReselectTimer;

    public BodyMotionSerpentWander(
        @Nonnull final BuilderBodyMotionSerpentWander builder,
        final BuilderSupport support
    ) {
        super(builder);
    }

    @Override
    public boolean computeSteering(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        @Nullable final InfoProvider sensorInfo,
        final double dt,
        @Nonnull final Steering desiredSteering,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        desiredSteering.clear();

        final TransformComponent transform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        final NPCEntity npc = componentAccessor.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        assert transform != null;
        assert npc != null;

        final Vector3d target = this.tickTargetSelection(dt, transform, npc);
        final Vector3d directionToTarget = target.clone().subtract(transform.getPosition()).normalize();
        desiredSteering.setTranslation(directionToTarget);

        return true;
    }

    private Vector3d tickTargetSelection(final double dt, final TransformComponent transform, final NPCEntity npc) {
        this.targetReselectTimer -= dt;

        if (this.targetPosition != null && transform.getPosition().distanceSquaredTo(this.targetPosition) < this.wanderTargetRadius * this.wanderTargetRadius) {
            this.targetPosition = null;
        }

        if (this.targetPosition == null || this.targetReselectTimer <= 0) {
            this.targetPosition = npc.getLeashPoint().clone().add(
                MathUtil.randomDouble(this.wanderTargetZone.min.x, this.wanderTargetZone.max.x),
                MathUtil.randomDouble(this.wanderTargetZone.min.y, this.wanderTargetZone.max.y),
                MathUtil.randomDouble(this.wanderTargetZone.min.z, this.wanderTargetZone.max.z)
            );
            this.targetReselectTimer = ThreadLocalRandom.current().nextDouble(this.wanderMinTargetTimer, this.wanderMaxTargetTimer);
        }

        return this.targetPosition;
    }
}
