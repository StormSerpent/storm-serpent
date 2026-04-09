package me.nullicorn.hytale.stormserpent.npc.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.movement.NavState;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.movement.constraints.RelaxedConstraint;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.movement.controllers.ProbeMoveData;
import com.hypixel.hytale.server.npc.movement.controllers.builders.BuilderMotionControllerBase;
import com.hypixel.hytale.server.npc.role.Role;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderMotionControllerStormSerpentFly;
import org.joml.AxisAngle4d;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public final class MotionControllerStormSerpentFly implements MotionController {
    /**
     * How fast the serpent will normally move.
     * <p>
     * Unit is meters per second.
     */
    private final double maxMoveSpeed = 40.0;
    /**
     * How fast the serpent can pivot its head.
     * <p>
     * Unit is radians per second.
     */
    private final double maxLookSpeed = Math.toRadians(65.0);

    private Role role;
    private final Box collisionBox = new Box();
    private final Vector3d componentSelector = new Vector3d(1.0, 1.0, 1.0);
    private final Vector3d planarComponentSelector = new Vector3d(1.0, 1.0, 1.0);
    private final EnumSet<RelaxedConstraint> relaxedMoveConstraints = EnumSet.noneOf(RelaxedConstraint.class);
    private NavState navState;
    private double throttleDuration;
    private double targetDeltaSquared;
    private double heightOverGround;

    public MotionControllerStormSerpentFly(
        @Nonnull BuilderSupport builderSupport,
        @Nonnull BuilderMotionControllerBase builder
    ) {
    }

    @Override
    public String getType() {
        return BuilderMotionControllerStormSerpentFly.COMPONENT_ID;
    }

    @Override
    public Role getRole() {
        return this.role;
    }

    @Override
    public void setRole(final Role role) {
        this.role = role;
    }

    @Override
    public void spawned() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public double steer(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        @Nonnull final Steering bodySteering,
        @Nonnull final Steering headSteering,
        final double interval,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        final TransformComponent transform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        final HeadRotation headRotation = componentAccessor.getComponent(ref, HeadRotation.getComponentType());
        assert transform != null;
        assert headRotation != null;

        final Ref<ChunkStore> chunkRef = transform.getChunkRef();
        if (chunkRef != null && chunkRef.isValid()) {
            final WorldChunk chunk = chunkRef.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
            if (chunk != null) {
                final double npcY = transform.getPosition().y + this.collisionBox.min.y;
                final int x = MathUtil.floor(transform.getPosition().x);
                final int z = MathUtil.floor(transform.getPosition().z);
                // `1 +` gets us the y-value at the top of the block.
                final int groundY = 1 + chunk.getHeight(x, z);
                this.heightOverGround = npcY - groundY;
            }
        }

        // Fill in pitch and yaw for `headSteering` if none were given. Face in the direction the NPC is moving.
        if (bodySteering.hasTranslation() && (!headSteering.hasPitch() || !headSteering.hasYaw())) {
            // Steer the head in the direction the body is moving.
            final Vector3d moveDirection = new Vector3d(bodySteering.getTranslation()).normalize();
            if (!headSteering.hasPitch()) {
                headSteering.setPitch((float) Math.asin(moveDirection.y));
            }
            if (!headSteering.hasYaw()) {
                headSteering.setYaw((float) (Math.atan2(moveDirection.x, moveDirection.z) + Math.PI));
            }
        }

        final Vector3d desiredLookDirection = Vector3dUtil.setYawPitch(
            headSteering.hasYaw() ? headSteering.getYaw() : headRotation.getRotation().yaw(),
            headSteering.hasPitch() ? headSteering.getPitch() : headRotation.getRotation().pitch(),
            new Vector3d()
        );
        // Constrain how quickly the NPC's head can rotate.
        final Vector3d lookDirection = limitAngle(
            /* from:  */ headRotation.getDirection(),
            /* to:    */ desiredLookDirection,
            /* limit: */ this.maxLookSpeed * bodySteering.getRelativeTurnSpeed() * interval
        );

        // Move the entity to its new position.
        if (bodySteering.hasTranslation()) {
            // Move forward in whichever direction we're looking.
            bodySteering.setTranslation(lookDirection);
            transform.getPosition().add(bodySteering.getTranslation().mul(this.maxMoveSpeed * interval, new Vector3d()));
        }

        // Rotate the entity's head to its new orientation.
        // TODO: Compute roll dynamically.
        headRotation.getRotation().setRoll(headSteering.getRoll());
        headRotation.getRotation().setPitch((float) Math.asin(lookDirection.y));
        headRotation.getRotation().setYaw((float) (Math.atan2(lookDirection.x, lookDirection.z) + Math.PI));

        return interval;
    }

    /**
     * Constrains the maximum angle between two vectors, returning the result as a new vector.
     * <p>
     * {@code limit} must be at least {@code >= 0}.
     * {@code limit} must not exceed {@link Math#PI}.
     * <p>
     * {@code from} and {@code to} are not modified by this function.
     *
     * @param from  Reference vector for the angle.
     * @param to    Target vector for the angle.
     * @param limit Maximum angle, in radians, between {@code from} and the result.
     * @return A new vector with the same length and axis of rotation as {@code to}, but constrained to be no more than
     * {@code limit} radians from {@code from}.
     */
    private static Vector3d limitAngle(final Vector3d from, final Vector3d to, final double limit) {
        if (limit < 0 || limit > Math.PI) {
            throw new IllegalArgumentException("limit must be in the range 0..=π");
        }

        final Vector3d limited = new Vector3d(to);

        // Normalize both vectors to make dot and cross product work correctly.
        final Vector3d fromNorm = new Vector3d(from).normalize();
        final Vector3d toNorm = new Vector3d(to).normalize();

        // Get the angle between the `from` and `to`, in radian.
        final double angle = Math.acos(Math.clamp(fromNorm.dot(toNorm), -1.0, 1.0));
        if (angle > limit) {
            // Get the rotation axis; perpendicular to the plane formed by `from` and `to`.
            final Vector3d axis = fromNorm.cross(toNorm).normalize();
            // Rotate `limited` (which is a clone of `to`) back toward `from` by however much the angle limit is
            // exceeded.
            new Matrix4d()
                .rotate(new AxisAngle4d(-(angle - limit), axis))
                .transformDirection(limited);
        }

        return limited;
    }

    @Override
    public double probeMove(
        @Nonnull final Ref<EntityStore> ref,
        final Vector3d position,
        final Vector3d direction,
        final ProbeMoveData probeMoveData,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        probeMoveData.setPosition(position);
        probeMoveData.setDirection(direction);
        return this.probeMove(ref, probeMoveData, componentAccessor);
    }

    @Override
    public double probeMove(
        @Nonnull final Ref<EntityStore> ref,
        final ProbeMoveData probeMoveData,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        return 0; // TODO (should return distance)
    }

    @Override
    public void setInertia(final double inertia) {
        // TODO
    }

    @Override
    public void setKnockbackScale(final double knockbackScale) {
        // TODO
    }

    @Override
    public double getGravity() {
        return 0;
    }

    @Override
    public void setHeadPitchAngleRange(@Nullable final float[] headPitchAngleRange) {
        // TODO
    }

    @Override
    public void updateModelParameters(
        @Nullable final Ref<EntityStore> ref,
        final Model model,
        final Box boundingBox,
        @Nullable final ComponentAccessor<EntityStore> componentAccessor
    ) {
        this.collisionBox.assign(boundingBox);
        if (ref != null && componentAccessor != null) {
            final EntityScaleComponent entityScaleComponent = componentAccessor.getComponent(ref, EntityScaleComponent.getComponentType());
            if (entityScaleComponent != null) {
                this.collisionBox.scale(entityScaleComponent.getScale());
            }
        }
    }

    @Override
    public void updatePhysicsValues(final PhysicsValues values) {
    }

    @Override
    public void constrainRotations(final Role role, final TransformComponent transform) {
    }

    @Override
    public double getCurrentMaxBodyRotationSpeed() {
        return this.maxLookSpeed;
    }

    @Override
    public void updateMovementState(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final MovementStates movementStates,
        @Nonnull final Steering steering,
        @Nonnull final Vector3d velocity,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {

    }

    @Override
    public boolean isValidPosition(final Vector3d position, final ComponentAccessor<EntityStore> componentAccessor) {
        return true;
    }

    @Override
    public boolean canSteer(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        return true;
    }

    @Nullable
    @Override
    public String canSteerFailReason(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        return null;
    }

    @Override
    public boolean isInProgress() {
        return false;
    }

    @Override
    public boolean isObstructed() {
        return false;
    }

    @Override
    public boolean inAir() {
        return true;
    }

    @Override
    public boolean inWater() {
        return false;
    }

    @Override
    public boolean onGround() {
        return false;
    }

    @Override
    public boolean standingOnBlockOfType(final int blockSet) {
        return false;
    }

    @Override
    public double getMaximumSpeed() {
        return this.maxMoveSpeed;
    }

    @Override
    public double getCurrentSpeed() {
        return this.maxMoveSpeed; // TODO
    }

    @Override
    public boolean estimateVelocity(final Steering steering, final Vector3d velocityOut) {
        return false;
    }

    @Override
    public double getCurrentTurnRadius() {
        return 0.0;
    }

    @Override
    public double waypointDistance(final Vector3dc p, final Vector3dc q) {
        return p.distance(q);
    }

    @Override
    public double waypointDistanceSquared(final Vector3dc p, final Vector3dc q) {
        return p.distanceSquared(q);
    }

    @Override
    public double waypointDistance(
        @Nonnull final Ref<EntityStore> ref,
        final Vector3d p,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        final TransformComponent transform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;
        return transform.getPosition().distance(p);
    }

    @Override
    public double waypointDistanceSquared(
        @Nonnull final Ref<EntityStore> ref,
        final Vector3d p,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        final TransformComponent transform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;
        return transform.getPosition().distanceSquared(p);
    }

    @Override
    public float getMaxClimbAngle() {
        return (float) Math.toRadians(90);
    }

    @Override
    public float getMaxSinkAngle() {
        return (float) Math.toRadians(90);
    }

    @Override
    public boolean translateToAccessiblePosition(
        final Vector3d position,
        final Box boundingBox,
        final double minYValue,
        final double maxYValue,
        final ComponentAccessor<EntityStore> componentAccessor
    ) {
        return true;
    }

    @Override
    public Vector3d getComponentSelector() {
        return this.componentSelector;
    }

    @Override
    public Vector3d getPlanarComponentSelector() {
        return this.planarComponentSelector;
    }

    @Override
    public void setComponentSelector(final Vector3d componentSelector) {
        this.componentSelector.set(componentSelector);
    }

    @Override
    public boolean is2D() {
        return false;
    }

    /**
     * The direction considered "up" by the NPC.
     */
    @Override
    public Vector3dc getWorldNormal() {
        return Vector3dUtil.UP;
    }

    /**
     * The direction considered "down" by the NPC.
     */
    @Override
    public Vector3dc getWorldAntiNormal() {
        return Vector3dUtil.DOWN;
    }

    @Override
    public void addForce(@Nonnull final Vector3d force, @Nullable final VelocityConfig velocityConfig) {

    }

    @Override
    public Vector3d getForce() {
        return null;
    }

    @Override
    public void forceVelocity(
        @Nonnull final Vector3dc velocity,
        @Nullable final VelocityConfig velocityConfig,
        final boolean ignoreDamping
    ) {
        // TODO
    }

    @Override
    public VerticalRange getDesiredVerticalRange(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        final TransformComponent transform = componentAccessor.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;
        final double y = transform.getPosition().y;
        final VerticalRange range = new VerticalRange();
        range.set(y, y, y); // TODO
        return range;
    }

    @Override
    public double getWanderVerticalMovementRatio() {
        return 0;
    }

    @Override
    public boolean isAvoidingBlockDamage() {
        return false;
    }

    @Override
    public boolean willReceiveBlockDamage() {
        return false;
    }

    @Override
    public void requirePreciseMovement(final Vector3d positionHint) {

    }

    @Override
    public void enableHeadingBlending(final double heading, final Vector3d targetPosition, final double blendLevel) {

    }

    @Override
    public void enableHeadingBlending() {

    }

    @Override
    public void setRelaxedMoveConstraints(@Nonnull final EnumSet<RelaxedConstraint> constraints) {
        this.relaxedMoveConstraints.clear();
        this.relaxedMoveConstraints.addAll(constraints);
    }

    @Nonnull
    @Override
    public EnumSet<RelaxedConstraint> getRelaxedConstraints() {
        return this.relaxedMoveConstraints;
    }

    @Override
    public NavState getNavState() {
        return this.navState;
    }

    @Override
    public double getThrottleDuration() {
        return this.throttleDuration;
    }

    @Override
    public double getTargetDeltaSquared() {
        return this.targetDeltaSquared;
    }

    @Override
    public void setNavState(final NavState navState, final double throttleDuration, final double targetDeltaSquared) {
        this.navState = navState;
        this.throttleDuration = throttleDuration;
        this.targetDeltaSquared = targetDeltaSquared;
    }

    @Override
    public void setForceRecomputePath(final boolean recomputePath) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean isForceRecomputePath() {
        return false;
    }

    @Override
    public boolean canRestAtPlace() {
        // Always stay in motion, since we're flying.
        return false;
    }

    @Override
    public void beforeInstructionSensorsAndActions(final double physicsTickDuration) {
    }

    @Override
    public void beforeInstructionMotion(final double physicsTickDuration) {
    }

    @Override
    public double getDesiredAltitudeWeight() {
        // Balance horizontal and vertical movement.
        return 0.5;
    }

    @Override
    public double getHeightOverGround() {
        return this.heightOverGround;
    }
}
