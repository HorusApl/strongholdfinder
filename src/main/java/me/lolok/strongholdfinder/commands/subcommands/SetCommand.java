package me.lolok.strongholdfinder.commands.subcommands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lolok.strongholdfinder.commands.CommandManager;
import me.lolok.strongholdfinder.commands.CommandValidator;
import me.lolok.strongholdfinder.commands.Subcommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class SetCommand implements Subcommand {
    private final CommandManager manager;

    @Getter
    private final String name = "set", description = "Set a new location", permission = "strongholdfinder.set";

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (CommandValidator.notPlayer(sender)) return;

        Player player = (Player) sender;
        Location location = player.getLocation();
        manager.getPlugin().getHandler().addLocation(location);
        sender.sendMessage(String.format("§2§l! §fLocation added! §a§lX: §f%d §a§lY: §f%d §a§lZ: §f%d", location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }
}
