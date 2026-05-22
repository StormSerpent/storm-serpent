package me.nullicorn.hytale.stormserpent.npc.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.movement.MovementMode;
import com.hypixel.hytale.server.npc.movement.NavState;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.movement.constraints.RelaxedConstraint;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.movement.controllers.ProbeMoveData;
import com.hypixel.hytale.server.npc.role.Role;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderMotionControllerStormSerpentFly;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public final class MotionControllerStormSerpentFly implements MotionController {
    /**
     * How fast to move when travelling horizontally.
     * <p>
     * Unit: meters per second
     */
    private final double maxHorizontalSpeed;
    /**
     * How fast to move when travelling upward.
     * <p>
     * Unit: meters per second
     */
    private final double maxClimbSpeed;
    /**
     * How fast to move when travelling downward.
     * <p>
     * Unit: meters per second
     */
    private final double maxSinkSpeed;
    /**
     * How long acceleration should take when changing movement speed.
     * <p>
     * Unit: seconds
     */
    private final double moveSpeedChangeDuration;
    /**
     * How long turning should take when changing movement direction.
     * <p>
     * Unit: seconds
     */
    private final double moveDirectionChangeDuration;
    private final double headTurnDuration;
    private final double epsilonSpeed;

    private Role role;
    private final Box collisionBox = new Box();
    private final Vector3d componentSelector = new Vector3d(1.0, 1.0, 1.0);
    private final Vector3d planarComponentSelector = new Vector3d(1.0, 1.0, 1.0);
    private final EnumSet<RelaxedConstraint> relaxedMoveConstraints = EnumSet.noneOf(RelaxedConstraint.class);
    @Nullable
    private NavState navState;
    private double throttleDuration;
    private double targetDeltaSquared;
    private double heightOverGround;
    private double currentMoveSpeed = 0;
    public final Vector3d currentMoveDirection = new Vector3d();
    public final Rotation3f currentMoveAngles = new Rotation3f();

    public MotionControllerStormSerpentFly(
        @Nonnull final BuilderSupport builderSupport,
        @Nonnull final BuilderMotionControllerStormSerpentFly builder
    ) {
        this.maxHorizontalSpeed = builder.getMaxHorizontalSpeed(builderSupport);
        this.maxClimbSpeed = builder.getMaxClimbSpeed(builderSupport);
        this.maxSinkSpeed = builder.getMaxSinkSpeed(builderSupport);
        this.moveSpeedChangeDuration = builder.getSpeedChangeDuration(builderSupport);
        this.moveDirectionChangeDuration = builder.getDirectionChangeDuration(builderSupport);
        this.headTurnDuration = builder.getHeadTurnDuration(builderSupport);
        this.epsilonSpeed = builder.getEpsilonSpeed();
    }

    @Nonnull
    @Override
    public String getType() {
        return BuilderMotionControllerStormSerpentFly.COMPONENT_ID;
    }

    @Nonnull
    @Override
    public Set<MovementMode> getSupportedMovementModes() {
        return Set.of(MovementMode.FLY);
    }

    @Nonnull
    @Override
    public Set<MovementMode> getDefaultSpawnMovementModes() {
        return Set.of(MovementMode.FLY);
    }

    @Nonnull
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

        if (bodySteering.hasTranslation()) {
            if (this.currentMoveDirection.length() < this.epsilonSpeed) {
                this.currentMoveDirection.set(headRotation.getDirection());
            }

            final double targetSpeedMultiplier = bodySteering.getSpeed();
            final Vector3d targetDirection = targetSpeedMultiplier >= this.epsilonSpeed
                ? new Vector3d(bodySteering.getTranslation()).div(targetSpeedMultiplier) // Normalizing
                : new Vector3d(this.currentMoveDirection);

            // Accelerate our movement direction toward `targetDirection` using lerp.
            final Rotation3f targetAngles = Rotation3f.lookAt(targetDirection);
            this.currentMoveAngles.set(Rotation3f.lerpAngle(this.currentMoveAngles, targetAngles, (float) (interval * this.moveDirectionChangeDuration * bodySteering.getRelativeTurnSpeed())));

            // Update `currentMoveDirection` to match the direction of `currentMoveAngles`.
            this.currentMoveDirection.set(Vector3dUtil.FORWARD);
            this.currentMoveAngles.transform(this.currentMoveDirection);

            final double targetSpeed;
            if (this.currentMoveAngles.pitch() > 0) {
                targetSpeed = targetSpeedMultiplier * MathUtil.lerp(this.maxHorizontalSpeed, this.maxClimbSpeed, this.currentMoveAngles.pitch() / this.getMaxClimbAngle());
            } else if (this.currentMoveAngles.pitch() < 0) {
                // Note: For `getMaxSinkAngle()` positive means downward, so we negate it to match the negative
                // `pitch()`.
                targetSpeed = targetSpeedMultiplier * MathUtil.lerp(this.maxHorizontalSpeed, this.maxSinkSpeed, this.currentMoveAngles.pitch() / -this.getMaxSinkAngle());
            } else {
                targetSpeed = 0;
            }
            // Accelerate to `targetSpeed` using lerp.
            this.currentMoveSpeed = MathUtil.lerp(this.currentMoveSpeed, targetSpeed, interval * this.moveSpeedChangeDuration);

            transform.getPosition().add(new Vector3d(this.currentMoveDirection).mul(this.currentMoveSpeed * interval));
        }

        if (headSteering.hasRollOrDirection()) {
            headRotation.getRotation().setRoll(MathUtil.lerpAngle(headRotation.getRotation().roll(), headSteering.getRollOrDirection(), (float) (interval * this.headTurnDuration * headSteering.getRelativeTurnSpeed())));
        }
        if (headSteering.hasPitchOrDirection()) {
            headRotation.getRotation().setPitch(MathUtil.lerpAngle(headRotation.getRotation().pitch(), headSteering.getPitchOrDirection(), (float) (interval * this.headTurnDuration * headSteering.getRelativeTurnSpeed())));
        }
        if (headSteering.hasYawOrDirection()) {
            headRotation.getRotation().setYaw(MathUtil.lerpAngle(headRotation.getRotation().yaw(), headSteering.getYawOrDirection(), (float) (interval * this.headTurnDuration * headSteering.getRelativeTurnSpeed())));
        }

        return interval;
    }

    @Override
    public double probeMove(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Vector3dc position,
        @Nonnull final Vector3dc direction,
        @Nonnull final ProbeMoveData probeMoveData,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        probeMoveData.setPosition(position);
        probeMoveData.setDirection(direction);
        return this.probeMove(ref, probeMoveData, componentAccessor);
    }

    @Override
    public double probeMove(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final ProbeMoveData probeMoveData,
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
    }

    @Override
    public void updatePhysicsValues(final PhysicsValues values) {
    }

    @Override
    public void constrainRotations(final Role role, final TransformComponent transform) {
    }

    @Override
    public double getCurrentMaxBodyRotationSpeed() {
        return 0; // TODO
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
    public boolean isValidPosition(
        @Nonnull final Vector3dc position,
        final ComponentAccessor<EntityStore> componentAccessor
    ) {
        return true;
    }

    @Override
    public boolean canSteer(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        return true;
    }

    @Override
    public boolean isForcePushed() {
        return false;
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
        return this.maxHorizontalSpeed;
    }

    @Override
    public double getCurrentSpeed() {
        return this.currentMoveSpeed;
    }

    @Override
    public boolean estimateVelocity(final Steering steering, final Vector3d velocityOut) {
        if (steering.hasTranslation()) {
            velocityOut.set(steering.getTranslation()).mul(this.getCurrentSpeed());
        }
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
    public void addVelocity(@Nonnull final Vector3d force, @Nullable final VelocityConfig velocityConfig) {
        // TODO
    }

    @Override
    public Vector3d getExternalVelocity() {
        return new Vector3d();
    }

    @Override
    public void setVelocity(
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

    @Nullable
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
