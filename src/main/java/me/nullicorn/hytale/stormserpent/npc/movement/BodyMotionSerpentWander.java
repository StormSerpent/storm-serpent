package me.nullicorn.hytale.stormserpent.npc.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.BodyMotionBase;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.IPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderBodyMotionSerpentWander;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public final class BodyMotionSerpentWander extends BodyMotionBase {
    private final double relativeSpeed;
    private final double radius;
    private final double[] relativeAltitudeRange;
    private final boolean useRelativeAltitude;

    /**
     * Minimum amount of time to try reaching a given {@link #destination} before choosing a new one.
     * <p>
     * Used as the lower bound for choosing a random time.
     * <p>
     * Unit is seconds.
     */
    private final double wanderMinTargetTimer = 0.5;
    /**
     * Max amount of time to try reaching a given {@link #destination} before choosing a new one.
     * <p>
     * Used as the upper bound for choosing a random time.
     * <p>
     * Unit is seconds.
     */
    private final double wanderMaxTargetTimer = 2.5;
    /**
     * How close the NPC must be to {@link #destination} to reach it and choose a new one.
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
    private Vector3d destination;
    /**
     * Time left until {@link #destination} is reselected if the NPC doesn't reach it.
     * <p>
     * Unit is seconds.
     */
    private double destinationReselectTimer;

    public BodyMotionSerpentWander(
        @Nonnull final BuilderBodyMotionSerpentWander builder,
        final BuilderSupport support
    ) {
        super(builder);
        this.relativeSpeed = builder.getRelativeSpeed(support);
        this.radius = builder.getRadius(support);
        this.relativeAltitudeRange = builder.getRelativeAltitudeRange(support);
        this.useRelativeAltitude = builder.getUseRelativeAltitude(support);
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

        final Vector3d target = readSensorPosition(sensorInfo);
        if (target == null) {
            return true;
        }

        final TransformComponent transform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;

        final Vector3d destination = this.tickDestinationSelection(dt, target, transform);
        final Vector3d directionToDestination = new Vector3d(destination).sub(transform.getPosition()).normalize();
        desiredSteering.setTranslation(directionToDestination);
        desiredSteering.setTranslationRelativeSpeed(this.relativeSpeed);

        return true;
    }

    private Vector3d tickDestinationSelection(
        final double dt,
        final Vector3d target,
        final TransformComponent transform
    ) {
        this.destinationReselectTimer -= dt;

        if (this.destination != null && transform.getPosition().distanceSquared(this.destination) < this.wanderTargetRadius * this.wanderTargetRadius) {
            this.destination = null;
        }

        if (this.destination == null || this.destinationReselectTimer <= 0) {
            final double angle = MathUtil.randomDouble(0, Math.TAU);
            final double distance = MathUtil.randomDouble(0, this.radius);
            this.destination = new Vector3d(
                Math.cos(angle) * distance,
                MathUtil.randomDouble(this.relativeAltitudeRange[0], this.relativeAltitudeRange[1]),
                Math.sin(angle) * distance
            ).add(
                target.x,
                this.useRelativeAltitude ? target.y : 0,
                target.z
            );
            this.destination.y = Math.max(this.destination.y, ChunkUtil.MIN_ENTITY_Y);
            this.destinationReselectTimer = ThreadLocalRandom.current().nextDouble(this.wanderMinTargetTimer, this.wanderMaxTargetTimer);
        }

        return this.destination;
    }

    @Nullable
    private static Vector3d readSensorPosition(@Nullable final InfoProvider sensorInfo) {
        if (sensorInfo == null || !sensorInfo.hasPosition()) {
            return null;
        }
        final IPositionProvider positionProvider = sensorInfo.getPositionProvider();
        final Vector3d position = new Vector3d();
        if (positionProvider == null || !positionProvider.hasPosition() || !positionProvider.providePosition(position)) {
            return null;
        }
        return position;
    }
}
