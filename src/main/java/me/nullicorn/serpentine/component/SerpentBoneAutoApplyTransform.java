package me.nullicorn.serpentine.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.StormSerpentPlugin;

public final class SerpentBoneAutoApplyTransform implements Component<EntityStore> {
    private static final SerpentBoneAutoApplyTransform INSTANCE = new SerpentBoneAutoApplyTransform();

    public static SerpentBoneAutoApplyTransform get() {
        return INSTANCE;
    }

    public static ComponentType<EntityStore, SerpentBoneAutoApplyTransform> getComponentType() {
        return StormSerpentPlugin.get().getSerpentBoneAutoApplyTransformComponentType();
    }

    private SerpentBoneAutoApplyTransform() {
    }

    @Override
    public Component<EntityStore> clone() {
        return get();
    }
}
