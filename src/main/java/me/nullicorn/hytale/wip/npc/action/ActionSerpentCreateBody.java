package me.nullicorn.hytale.wip.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.serpent.asset.SerpentConfig;
import me.nullicorn.hytale.serpent.component.Serpent;

import javax.annotation.Nonnull;

// TODO: Move to hytale-serpent plugin.
public final class ActionSerpentCreateBody extends ActionBase {
    private final SerpentConfig serpentConfig;

    public ActionSerpentCreateBody(final BuilderActionSerpentCreateBody builder, final BuilderSupport support) {
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

        return true;
    }
}
