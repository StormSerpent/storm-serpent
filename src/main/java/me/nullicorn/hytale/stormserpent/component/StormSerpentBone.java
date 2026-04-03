package me.nullicorn.hytale.stormserpent.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.StormSerpentPlugin;

public final class StormSerpentBone implements Component<EntityStore> {
    public static ComponentType<EntityStore, StormSerpentBone> getComponentType() {
        return StormSerpentPlugin.get().getStormSerpentBoneComponentType();
    }

    public StormSerpentBone() {
    }

    @Override
    public Component<EntityStore> clone() {
        return new StormSerpentBone();
    }
}
