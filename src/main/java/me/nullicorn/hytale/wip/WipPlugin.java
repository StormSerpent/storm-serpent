package me.nullicorn.hytale.wip;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.npc.NPCPlugin;
import me.nullicorn.hytale.wip.npc.action.BuilderActionSerpentCreateBody;

import javax.annotation.Nonnull;

public final class WipPlugin extends JavaPlugin {
    public WipPlugin(@Nonnull final JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        // NPC actions.
        NPCPlugin.get().registerCoreComponentType("SerpentCreateBody", BuilderActionSerpentCreateBody::new);
    }
}
