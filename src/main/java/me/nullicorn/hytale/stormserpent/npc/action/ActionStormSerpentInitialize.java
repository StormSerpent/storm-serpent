package me.nullicorn.hytale.stormserpent.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.component.StormSerpent;
import me.nullicorn.hytale.stormserpent.component.StormSerpentBone;
import me.nullicorn.hytale.stormserpent.npc.action.builder.BuilderActionStormSerpentInitialize;
import me.nullicorn.serpentine.asset.SerpentConfig;
import me.nullicorn.serpentine.component.Serpent;

import javax.annotation.Nonnull;

public final class ActionStormSerpentInitialize extends ActionBase {
    private final SerpentConfig serpentConfig;

    public ActionStormSerpentInitialize(
        final BuilderActionStormSerpentInitialize builder,
        final BuilderSupport support
    ) {
        super(builder);
        this.serpentConfig = builder.serpentConfig(support);
    }

    @Override
    public boolean execute(
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final Role role,
        final InfoProvider sensorInfo,
        final double dt,
        @Nonnull final Store<EntityStore> store
    ) {
        super.execute(ref, role, sensorInfo, dt, store);

        if (this.serpentConfig == null) {
            return false;
        }

        Serpent serpent = store.getComponent(ref, Serpent.getComponentType());
        if (serpent != null) {
            return false;
        }

        final TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        if (transform == null) {
            return false;
        }

        serpent = new Serpent(new Transform(transform.getPosition()), this.serpentConfig.layout().chooseBones());
        store.addComponent(ref, Serpent.getComponentType(), serpent);
        store.addComponent(ref, StormSerpent.getComponentType());
        store.addComponent(ref, StormSerpentBone.getComponentType());

        return true;
    }
}
