package me.nullicorn.hytale.stormserpent.command;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

import javax.annotation.Nonnull;

public final class StormSerpentCommand extends AbstractCommandCollection {
    public StormSerpentCommand() {
        super("storm-serpent", "storm_serpent_server.commands.storm-serpent.desc");
        this.addSubCommand(new BossBarCommand());
    }
}
