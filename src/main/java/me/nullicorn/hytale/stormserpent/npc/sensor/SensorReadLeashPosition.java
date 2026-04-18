package me.nullicorn.hytale.stormserpent.npc.sensor;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.sensorinfo.PositionProvider;
import me.nullicorn.hytale.stormserpent.npc.sensor.builder.BuilderSensorReadLeashPosition;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class SensorReadLeashPosition extends SensorBase {
    private final PositionProvider positionProvider = new PositionProvider();

    public SensorReadLeashPosition(final BuilderSensorReadLeashPosition builder, final BuilderSupport support) {
        super(builder);
    }

    @Override
    public InfoProvider getSensorInfo() {
        return this.positionProvider;
    }

    @Override
    public boolean matches(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        final double dt,
        @Nonnull final Store<EntityStore> store
    ) {
        if (!super.matches(ref, role, dt, store)) {
            this.positionProvider.clear();
            return false;
        }

        final NPCEntity npc = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        assert npc != null;

        this.positionProvider.setTarget(npc.getLeashPoint());
        return true;
    }
}
