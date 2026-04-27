package me.nullicorn.hytale.stormserpent.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.nullicorn.hytale.stormserpent.component.HideBossBar;

import javax.annotation.Nonnull;

public final class BossBarCommand extends AbstractPlayerCommand {
    private final RequiredArg<Boolean> enableArg;

    public BossBarCommand() {
        super("boss-bar", "storm_serpent_server.commands.storm-serpent.boss-bar.desc");
        this.enableArg = this.withRequiredArg("enable", "storm_serpent_server.commands.storm-serpent.boss-bar.enable.desc", ArgTypes.BOOLEAN);
    }

    @Override
    protected void execute(
        @Nonnull final CommandContext context,
        @Nonnull final Store<EntityStore> store,
        @Nonnull final Ref<EntityStore> ref,
        @Nonnull final PlayerRef playerRef,
        @Nonnull final World world
    ) {
        final boolean enable = this.enableArg.get(context);
        world.execute(() -> {
            if (enable) {
                store.tryRemoveComponent(ref, HideBossBar.getComponentType());
                context.sendMessage(Message.translation("storm_serpent_server.commands.storm-serpent.boss-bar.enabled"));
            } else {
                store.ensureComponent(ref, HideBossBar.getComponentType());
                context.sendMessage(Message.translation("storm_serpent_server.commands.storm-serpent.boss-bar.disabled"));
            }
        });
    }
}
