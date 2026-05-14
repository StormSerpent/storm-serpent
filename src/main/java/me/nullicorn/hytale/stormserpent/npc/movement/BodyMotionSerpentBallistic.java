package me.nullicorn.hytale.stormserpent.npc.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.BodyMotionBase;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.util.NPCPhysicsMath;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderBodyMotionSerpentBallistic;
import org.joml.Vector2d;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BodyMotionSerpentBallistic extends BodyMotionBase {
    private final int targetSlot;
    private final double arcHeight;
    private final double arcPathSpeed;
    private final double relativeSpeed;
    private final double relativeTurnSpeed;

    @Nullable
    private Vector3d startPosition;
    @Nullable
    private Vector3d targetPosition;
    private double targetHorizontalDistance;
    private double peakHorizontalDistance;
    private Vector3d horizontalDirection;
    private double t;

    public BodyMotionSerpentBallistic(final BuilderBodyMotionSerpentBallistic builder, final BuilderSupport support) {
        super(builder);
        this.targetSlot = builder.getTargetSlot(support);
        this.arcHeight = builder.getArcHeight(support);
        this.arcPathSpeed = builder.getArcPathSpeed(support);
        this.relativeSpeed = builder.getRelativeSpeed(support);
        this.relativeTurnSpeed = builder.getRelativeTurnSpeed(support);
    }

    @Override
    public void activate(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        final TransformComponent selfTransform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        if (selfTransform == null) {
            return;
        }
        final Ref<EntityStore> targetRef = role.getMarkedEntitySupport().getMarkedEntityRef(this.targetSlot);
        if (targetRef == null || !targetRef.isValid()) {
            return;
        }
        final TransformComponent targetTransform = componentAccessor.getComponent(targetRef, TransformComponent.getComponentType());
        if (targetTransform == null) {
            return;
        }
        this.startPosition = new Vector3d(selfTransform.getPosition());
        this.targetPosition = new Vector3d(targetTransform.getPosition());
        this.targetHorizontalDistance = Vector2d.distance(this.startPosition.x, this.startPosition.z, this.targetPosition.x, this.targetPosition.z);
        this.peakHorizontalDistance = this.targetHorizontalDistance * Math.sqrt(Math.abs(this.arcHeight)) / (Math.sqrt(Math.abs(this.arcHeight)) + Math.sqrt(Math.abs(this.startPosition.y + this.arcHeight - this.targetPosition.y)));
        this.horizontalDirection = new Vector3d(this.targetPosition.x, 0, this.targetPosition.z).sub(this.startPosition.x, 0, this.startPosition.z).normalize();
        this.t = 0;

        if (role.getActiveMotionController() instanceof final MotionControllerStormSerpentFly motionController) {
            motionController.currentMoveDirection.set(Vector3dUtil.UP);
            motionController.currentMoveAngles.setPitch((float)(Math.PI) / 2);
            motionController.currentMoveAngles.setYaw(NPCPhysicsMath.headingFromDirection(this.horizontalDirection.x, this.horizontalDirection.z, 0.0f));
        }
    }

    @Nullable
    @Override
    public Ref<EntityStore> getDesiredTargetEntity() {
        return super.getDesiredTargetEntity();
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
        if (this.startPosition == null || this.targetPosition == null) {
            return false;
        }

        this.t += dt * this.arcPathSpeed;

        final TransformComponent transform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;

        // Formulas come from 'Quadratic Interpolation' by harleenk on GeeksforGeeks.org:
        // https://www.geeksforgeeks.org/maths/quadratic-interpolation/
        final double x = this.t;
        final double x0 = 0;
        final double x1 = this.peakHorizontalDistance;
        final double x2 = this.targetHorizontalDistance;
        final double y0 = this.startPosition.y;
        final double y1 = Math.max(this.startPosition.y, this.targetPosition.y) + this.arcHeight;
        final double y2 = this.targetPosition.y;
        final double l0 = ((x - x1) * (x - x2)) / ((x0 - x1) * (x0 - x2));
        final double l1 = ((x - x0) * (x - x2)) / ((x1 - x0) * (x1 - x2));
        final double l2 = ((x - x0) * (x - x1)) / ((x2 - x0) * (x2 - x1));
        final double y = y0 * l0 + y1 * l1 + y2 * l2;

        final Vector3d destination = new Vector3d(this.horizontalDirection).mul(this.t).add(this.startPosition);
        destination.y = y;

        final Vector3d difference = new Vector3d(destination).sub(transform.getPosition());
        final double speed;
        if (difference.length() < 40) {
            speed = this.relativeSpeed * (difference.length() / 40);
        } else {
            speed = this.relativeSpeed;
        }
        desiredSteering.setTranslation(new Vector3d(difference).normalize());
        desiredSteering.setTranslationRelativeSpeed(speed);
        desiredSteering.setRelativeTurnSpeed(this.relativeTurnSpeed);

        return true;
    }
}
