package me.nullicorn.hytale.stormserpent.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.StormSerpentPlugin;

public final class StormSerpent implements Component<EntityStore> {
    public static final String COMPONENT_ID = "StormSerpent";
    public static final BuilderCodec<StormSerpent> CODEC = BuilderCodec.builder(StormSerpent.class, StormSerpent::new)
        .build();

    public BurrowStatus burrowStatus = BurrowStatus.NOT_IN_BURROW;

    public static ComponentType<EntityStore, StormSerpent> getComponentType() {
        return StormSerpentPlugin.get().getStormSerpentComponentType();
    }

    @Override
    public Component<EntityStore> clone() {
        return new StormSerpent();
    }

    public enum BurrowStatus {
        NOT_IN_BURROW,
        ENTERING,
        IN_BURROW,
    }
}
