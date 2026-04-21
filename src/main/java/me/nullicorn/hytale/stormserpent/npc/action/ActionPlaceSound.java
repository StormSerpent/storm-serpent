package me.nullicorn.hytale.stormserpent.npc.action;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import me.nullicorn.hytale.stormserpent.npc.action.builder.BuilderActionPlaceSound;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ActionPlaceSound extends ActionBase {
    private final int soundEventIndex;

    public ActionPlaceSound(final BuilderActionPlaceSound builder, final BuilderSupport support) {
        super(builder);
        this.soundEventIndex = builder.getSoundEventIndex(support);
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

        final TransformComponent transform = store.getComponent(ref, TransformComponent.getComponentType());
        assert transform != null;

        SoundUtil.playSoundEvent3d(this.soundEventIndex, SoundCategory.SFX, new Vector3d(transform.getPosition()).add(0, 10, 0), store);
        return true;
    }
}
