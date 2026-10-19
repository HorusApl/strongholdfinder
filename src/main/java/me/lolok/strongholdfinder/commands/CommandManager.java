package me.lolok.strongholdfinder.commands;

import lombok.Getter;
import me.lolok.strongholdfinder.Service;
import me.lolok.strongholdfinder.StrongholdFinderPlugin;
import me.lolok.strongholdfinder.commands.subcommands.HelpCommand;
import me.lolok.strongholdfinder.commands.subcommands.ReloadCommand;
import me.lolok.strongholdfinder.commands.subcommands.SetCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor, Service, TabCompleter {
    @Getter
    private final StrongholdFinderPlugin plugin;
    private final String baseCommand;

    @Getter
    private final Set<Subcommand> registeredCommands = new LinkedHashSet<>();

    public CommandManager(StrongholdFinderPlugin plugin, String baseCommand) {
        this.plugin = plugin;
        this.baseCommand = baseCommand;
        initialize();
    }

    @Override
    public void enable() {
        Objects.requireNonNull(plugin.getServer().getPluginCommand(baseCommand)).setExecutor(this);
    }

    @Override
    public void disable() {
    }

    private void initialize() {
        register(new HelpCommand(this));
        register(new SetCommand(this));
        register(new ReloadCommand(this));
    }

    private void register(Subcommand subcommand) {
        registeredCommands.add(subcommand);
    }

    protected Optional<Subcommand> getSubcommand(String name) {
        return registeredCommands.stream().filter(subcommand -> subcommand.getName().toLowerCase().startsWith(name.toLowerCase())).findAny();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length == 0) {
            Bukkit.dispatchCommand(sender, baseCommand + " help");
            return true;
        }

        Optional<Subcommand> subcommand = getSubcommand(args[0]);
        subcommand.ifPresentOrElse(cmd -> {
            if (!cmd.getPermission().isEmpty() && !sender.hasPermission(cmd.getPermission())) {
                sender.sendMessage("§c§lERROR! §7You don't have §fpermission§7!");
                return;
            }
            String[] param = Arrays.copyOfRange(args, 1, args.length);
            cmd.execute(sender, param);
        }, () -> sender.sendMessage("§c§lERROR! §7Unknown command! Type §f/pr help"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, @NotNull String[] args) {
        if (args.length <= 1)
            return registeredCommands.stream().map(Subcommand::getName).filter(name -> args.length == 0 || name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        else {
            Optional<Subcommand> subcommand = getSubcommand(args[0]);
            if (subcommand.isPresent()) {
                String[] param = Arrays.copyOfRange(args, 1, args.length);
                return subcommand.get().suggest(param);
            } else
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
        }
    }
}
