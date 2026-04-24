package me.nullicorn.hytale.stormserpent.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.npc.action.builder.BuilderActionStormSerpentBeginFight;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ActionStormSerpentBeginFight extends ActionBase {
    public ActionStormSerpentBeginFight(
        final BuilderActionStormSerpentBeginFight builder,
        final BuilderSupport support
    ) {
        super(builder);
    }

    @Override
    public boolean execute(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        @Nullable final InfoProvider sensorInfo,
        final double dt,
        @Nonnull final Store<EntityStore> store
    ) {
        if (!super.execute(ref, role, sensorInfo, dt, store)) {
            return false;
        }

        final StormSerpent stormSerpent = store.getComponent(ref, StormSerpent.getComponentType());
        if (stormSerpent != null) {
            stormSerpent.inCombat = true;
            return true;
        }

        return false;
    }
}
