package me.nullicorn.hytale.stormserpent.npc.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.valuestore.ValueStore;
import me.nullicorn.hytale.stormserpent.npc.sensor.builder.BuilderSensorCounter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SensorCounter extends SensorBase {
    private final int slot;
    private final int[] range;

    public SensorCounter(@Nonnull final BuilderSensorCounter builder, final BuilderSupport support) {
        super(builder);
        this.slot = builder.getSlot(support);
        this.range = builder.getRange(support);
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

        final ValueStore valueStore = store.getComponent(ref, ValueStore.getComponentType());
        if (valueStore == null) {
            return false;
        }

        final int value = valueStore.readInt(this.slot);
        return this.range[0] <= value && value <= this.range[1];
    }
}
