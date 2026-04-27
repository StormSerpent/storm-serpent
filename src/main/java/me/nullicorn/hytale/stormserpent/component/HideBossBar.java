package me.nullicorn.hytale.stormserpent.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.StormSerpentPlugin;

public final class HideBossBar implements Component<EntityStore> {
    private static final HideBossBar INSTANCE = new HideBossBar();

    public static ComponentType<EntityStore, HideBossBar> getComponentType() {
        return StormSerpentPlugin.get().getHideBossBarComponentType();
    }

    public static HideBossBar get() {
        return INSTANCE;
    }

    private HideBossBar() {
    }

    @Override
    public Component<EntityStore> clone() {
        return get();
    }
}
