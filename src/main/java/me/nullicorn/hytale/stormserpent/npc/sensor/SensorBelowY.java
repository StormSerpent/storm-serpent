package me.nullicorn.hytale.stormserpent.npc.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.npc.sensor.builder.BuilderSensorBelowY;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SensorBelowY extends SensorBase {
    private final double y;

    public SensorBelowY(final BuilderSensorBelowY builder, final BuilderSupport support) {
        super(builder);
        this.y = builder.getY(support);
    }

    @Nullable
    @Override
    public InfoProvider getSensorInfo() {
        return null;
    }

    @Override
    public boolean matches(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        final double dt,
        @Nonnull final Store<EntityStore> store
    ) {
        if (!super.matches(ref, role, dt, store)) {
            return false;
        }

        final TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;

        return transform.getPosition().y < this.y;
    }
}
