package me.nullicorn.hytale.stormserpent.npc.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.MathUtil;
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

public class BodyMotionSerpentEncircle extends BodyMotionBase {
    private final double desiredRelativeAltitude;
    private final double desiredRelativeSpeed;
    private final double desiredRadius;

    public BodyMotionSerpentEncircle(final BuilderBodyMotionSerpentEncircle builder, final BuilderSupport support) {
        super(builder);
        this.desiredRelativeAltitude = builder.getRelativeAltitude(support);
        this.desiredRelativeSpeed = builder.getRelativeSpeed(support);
        this.desiredRadius = builder.getRadius(support);
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

        final TransformComponent transform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;
        final Vector3d currentPosition = transform.getPosition();
        final Vector3d relativePosition = new Vector3d(currentPosition).sub(targetPosition);
        final double currentHorizontalDistance = relativePosition.length();

        final double maxRotationSpeed = role.getActiveMotionController().getMaximumSpeed() / this.desiredRadius;
        //
        final double currentRotationSpeed = MathUtil.lerp(0.0, maxRotationSpeed, Math.min(currentHorizontalDistance / this.desiredRadius, this.desiredRadius));
        final Vector3d desiredPosition = new Vector3d(relativePosition.x, this.desiredRelativeAltitude, relativePosition.z)
            .rotateY(currentRotationSpeed * dt)
            .add(targetPosition);

        desiredSteering.setTranslation(new Vector3d(desiredPosition).sub(currentPosition).normalize(this.desiredRelativeSpeed));
        desiredSteering.setRelativeTurnSpeed(this.desiredRelativeSpeed);
        return true;
    }
}
