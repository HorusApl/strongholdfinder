package me.lolok.strongholdfinder.commands.subcommands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lolok.strongholdfinder.commands.CommandManager;
import me.lolok.strongholdfinder.commands.Subcommand;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class ReloadCommand implements Subcommand {
    private final CommandManager manager;

    @Getter
    private final String name = "reload", description = "Reload config", permission = "strongholdfinder.reload";

    @Override
    public void execute(CommandSender sender, String[] args) {
        manager.getPlugin().getHandler().reload();
        sender.sendMessage("§2§l! §aConfig reload successfully!");
    }
}
