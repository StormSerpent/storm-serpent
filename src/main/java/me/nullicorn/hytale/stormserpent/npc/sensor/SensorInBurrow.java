package me.nullicorn.hytale.stormserpent.npc.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.npc.sensor.builder.BuilderSensorInBurrow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SensorInBurrow extends SensorBase {
    public SensorInBurrow(final BuilderSensorInBurrow builder, final BuilderSupport support) {
        super(builder);
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

        final StormSerpent stormSerpent = store.getComponent(ref, StormSerpent.getComponentType());
        if (stormSerpent == null) {
            return false;
        }

        return stormSerpent.burrowStatus == StormSerpent.BurrowStatus.IN_BURROW;
    }
}
