package me.nullicorn.hytale.stormserpent.npc.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.HeadMotionBase;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderHeadMotionBase;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class HeadMotionSerpentMatchBodyMotion extends HeadMotionBase {
    public HeadMotionSerpentMatchBodyMotion(final BuilderHeadMotionBase builder, final BuilderSupport support) {
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
        // FIXME: This relies on head motion being computed after body motion (see Role#computeActionsAndSteering).
        // FIXME: The rotation we compute here can become wrong if the MotionController changes body steering afterward.

        final Velocity velocity = componentAccessor.getComponent(ref, Velocity.getComponentType());
        if (velocity != null && velocity.getSpeed() > 0.001) {
            final Rotation3f headRotation = Rotation3f.lookAt(velocity.getVelocity());
            desiredSteering.setPitch(headRotation.pitch());
            desiredSteering.setYaw(headRotation.yaw());
            desiredSteering.setRoll(headRotation.roll());
            return true;
        }

        final Steering bodySteering = role.getBodySteering();
        if (bodySteering.hasTranslation()) {
            final Rotation3f headRotation = Rotation3f.lookAt(bodySteering.getTranslation());
            desiredSteering.setPitch(headRotation.pitch());
            desiredSteering.setYaw(headRotation.yaw());
            desiredSteering.setRoll(headRotation.roll());
        }

        return true;
    }
}
