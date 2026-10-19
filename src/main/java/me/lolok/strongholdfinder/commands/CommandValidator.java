package me.lolok.strongholdfinder.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandValidator {
    public static boolean notPlayer(CommandSender sender) {
        boolean isPlayer = sender instanceof Player;
        if (!isPlayer)
            sender.sendMessage("§c§lERROR! §7Execute this command as §fplayer");
        return !isPlayer;
    }

    public static boolean isCorrectUsage(CommandSender sender, String usage, String[] args) {
        int requirements = 0;
        for (String s : usage.split(" "))
            if (s.startsWith("<") && s.endsWith(">")) ++requirements;

        boolean valid = args.length >= requirements;
        if (!valid)
            sender.sendMessage(String.format("§c§lERROR! §7Wrong usage! Use: §f%s", usage));
        return valid;
    }
}
