package me.nullicorn.hytale.stormserpent.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.npc.action.builder.BuilderActionEnterBurrow;
import me.nullicorn.hytale.stormserpent.solver.EnteringBurrowJointSolver;
import me.nullicorn.serpentine.component.Serpent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ActionEnterBurrow extends ActionBase {
    private final double relativeSpeed;

    public ActionEnterBurrow(final BuilderActionEnterBurrow builder, final BuilderSupport support) {
        super(builder);
        this.relativeSpeed = builder.getRelativeSpeed(support);
    }

    @Override
    public boolean canExecute(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        @Nullable final InfoProvider sensorInfo,
        final double dt,
        @Nonnull final Store<EntityStore> store
    ) {
        if (!super.canExecute(ref, role, sensorInfo, dt, store)) {
            return false;
        }

        final StormSerpent stormSerpent = store.getComponent(ref, StormSerpent.getComponentType());
        return stormSerpent != null && stormSerpent.burrowStatus == StormSerpent.BurrowStatus.NOT_IN_BURROW;
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

        final StormSerpent stormSerpent = store.getComponent(ref, StormSerpent.getComponentType());
        assert stormSerpent != null;

        final TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;

        final Serpent serpent = store.getComponent(ref, Serpent.getComponentType());
        assert serpent != null;

        serpent.setJointSolver(new EnteringBurrowJointSolver(role.getActiveMotionController().getMaximumSpeed() * this.relativeSpeed));
        stormSerpent.burrowStatus = StormSerpent.BurrowStatus.ENTERING;
        return true;
    }
}
