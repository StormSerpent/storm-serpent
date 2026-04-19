package me.nullicorn.hytale.stormserpent.npc.movement;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3dUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.BodyMotionBase;
import com.hypixel.hytale.server.npc.movement.Steering;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderBodyMotionSerpentDive;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BodyMotionSerpentDive extends BodyMotionBase {
    private final double relativeSpeed;

    public BodyMotionSerpentDive(final BuilderBodyMotionSerpentDive builder, final BuilderSupport support) {
        super(builder);
        this.relativeSpeed = builder.getRelativeSpeed(support);
    }

    @Override
    public boolean computeSteering(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        @Nullable final InfoProvider sensorInfo,
        final double dt,
        @Nonnull final Steering steering,
        @Nonnull final ComponentAccessor<EntityStore> componentAccessor
    ) {
        steering.clear();
        steering.setTranslation(new Vector3d(Vector3dUtil.DOWN).mul(this.relativeSpeed));
        return true;
    }
}
