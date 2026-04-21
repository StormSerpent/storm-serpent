package me.nullicorn.hytale.stormserpent.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.hypixel.hytale.server.npc.valuestore.ValueStore;
import me.nullicorn.hytale.stormserpent.npc.action.builder.BuilderActionCounterAdd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ActionCounterAdd extends ActionBase {
    private final int slot;
    private final int amount;

    public ActionCounterAdd(
        final BuilderActionCounterAdd builder,
        final BuilderSupport support
    ) {
        super(builder);
        this.slot = builder.getSlot(support);
        this.amount = builder.getAmount(support);
    }

    @Override
    public boolean execute(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        @Nullable final InfoProvider sensorInfo,
        final double dt,
        @Nonnull final Store<EntityStore> store
    ) {
        super.execute(ref, role, sensorInfo, dt, store);

        final ValueStore valueStore = store.getComponent(ref, ValueStore.getComponentType());
        if (valueStore != null) {
            valueStore.storeInt(this.slot, valueStore.readInt(this.slot) + this.amount);
            return true;
        }

        return false;
    }
}
