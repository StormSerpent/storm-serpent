package me.nullicorn.hytale.stormserpent;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.npc.NPCPlugin;
import me.nullicorn.hytale.stormserpent.npc.action.BuilderActionSerpentCreateBody;
import me.nullicorn.hytale.stormserpent.npc.movement.MotionControllerSerpentFly;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderBodyMotionSerpentEncircle;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderBodyMotionSerpentWander;
import me.nullicorn.hytale.stormserpent.npc.movement.builder.BuilderMotionControllerSerpentFly;

import javax.annotation.Nonnull;

public final class StormSerpentPlugin extends JavaPlugin {
    public StormSerpentPlugin(@Nonnull final JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        // NPC motions.
        NPCPlugin.get().registerCoreComponentType("SerpentWander", BuilderBodyMotionSerpentWander::new);
        NPCPlugin.get().registerCoreComponentType("SerpentEncircle", BuilderBodyMotionSerpentEncircle::new);

        // NPC motion controllers.
        NPCPlugin.get().registerCoreComponentType(MotionControllerSerpentFly.NAME, BuilderMotionControllerSerpentFly::new);

        // NPC actions.
        NPCPlugin.get().registerCoreComponentType("SerpentCreateBody", BuilderActionSerpentCreateBody::new);
    }
}
