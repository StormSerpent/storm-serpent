package me.nullicorn.hytale.stormserpent.npc.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.util.TrigMathUtil;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.BodyMotionBase;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.IPositionProvider;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderBodyMotionSerpentEncircle;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BodyMotionSerpentEncircle extends BodyMotionBase {
    private final double desiredRelativeAltitude;
    private final double desiredRelativeSpeed;
    private final double desiredRadius;
    private final double oscillateRadiusFrequency;
    private final double oscillateRadiusAmplitude;
    private final double oscillateYFrequency;
    private final double oscillateYAmplitude;
    private double oscillationTime = 0;

    public BodyMotionSerpentEncircle(
        final BuilderBodyMotionSerpentEncircle builder,
        final BuilderSupport support
    ) {
        super(builder);
        this.desiredRelativeAltitude = builder.getRelativeAltitude(support);
        this.desiredRelativeSpeed = builder.getRelativeSpeed(support);
        this.desiredRadius = builder.getRadius(support);
        this.oscillateRadiusFrequency = builder.getOscillateRadiusFrequency(support);
        this.oscillateRadiusAmplitude = builder.getOscillateRadiusAmplitude(support);
        this.oscillateYFrequency = builder.getOscillateYFrequency(support);
        this.oscillateYAmplitude = builder.getOscillateYAmplitude(support);
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
        if (sensorInfo == null || !sensorInfo.hasPosition()) {
            return false;
        }

        final IPositionProvider positionProvider = sensorInfo.getPositionProvider();
        final Vector3d targetPosition = new Vector3d();
        if (positionProvider == null || !positionProvider.hasPosition() || !positionProvider.providePosition(targetPosition)) {
            return false;
        }

        this.oscillationTime += dt;
        final double targetRadius = this.desiredRadius + TrigMathUtil.sin(this.oscillationTime * this.oscillateRadiusFrequency) * this.oscillateRadiusAmplitude;
        final double targetRelativeAltitude = this.desiredRelativeAltitude + TrigMathUtil.sin(this.oscillationTime * this.oscillateYFrequency) * this.oscillateYAmplitude;

        final TransformComponent transform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;
        final Vector3d currentPosition = transform.getPosition();
        final Vector3d relativePosition = new Vector3d(currentPosition).sub(targetPosition);
        final double currentHorizontalDistance = new Vector3d(relativePosition.x, 0, relativePosition.z).length();

        final Vector3d relativeDirection = new Vector3d(relativePosition.x, 0, relativePosition.z).normalize();
        if (!relativeDirection.isFinite()) {
            // `relativeDirection` can be NaN when the NPC and target are at the same coordinate (like when spawned via
            // command). In this case we just pick an arbitrary direction.
            relativeDirection.set(Vector3dUtil.NEG_Z);
        }

        // Get the length of the arc we want to travel along this tick.
        final double maxRotationSpeed = role.getActiveMotionController().getMaximumSpeed() / targetRadius;
        // Move faster the closer we are to the desired radius.
        final double currentRotationSpeed = MathUtil.lerp(0.0, maxRotationSpeed, Math.min(currentHorizontalDistance, targetRadius) / targetRadius);
        final Vector3d desiredPosition = new Vector3d(relativeDirection)
            .mul(targetRadius)
            .add(0, targetRelativeAltitude, 0)
            .rotateY(currentRotationSpeed * dt * (this.oscillationTime % 60 < 30 ? 1 : -1)) // Periodically flip directions.
            .add(targetPosition);

        desiredSteering.setTranslation(new Vector3d(desiredPosition).sub(currentPosition).normalize(this.desiredRelativeSpeed));
        desiredSteering.setRelativeTurnSpeed(this.desiredRelativeSpeed);
        return true;
    }
}
