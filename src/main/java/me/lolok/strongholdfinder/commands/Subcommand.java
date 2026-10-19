package me.lolok.strongholdfinder.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public interface Subcommand {
    String getName();

    String getDescription();

    default String getArguments() {
        return "";
    }

    default String getPermission() {
        return "";
    }

    void execute(CommandSender sender, String[] args);

    default List<String> suggest(String[] args) {
        return new ArrayList<>();
    }
}
