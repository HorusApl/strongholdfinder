package me.lolok.strongholdfinder.commands.subcommands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lolok.strongholdfinder.commands.CommandManager;
import me.lolok.strongholdfinder.commands.Subcommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class HelpCommand implements Subcommand {
    private final CommandManager manager;

    @Getter
    private final String name = "help", description = "Show the command list", permission = "strongholdfinder.help";

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§m----§b§m----§3§l[ §b§lSTRONGHOLD FINDER §3§l]§b§m----§f§m-----");
        manager.getRegisteredCommands().forEach(subcommand -> sendCommand(sender, subcommand));
    }

    private void sendCommand(CommandSender sender, Subcommand subcommand) {
        TextComponent component = new TextComponent("§3• §b/sf " + subcommand.getName() + " " + (subcommand.getArguments().isEmpty() ? "" : subcommand.getArguments() + " ") + "§8- §f" + subcommand.getDescription());
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/sf %s ", subcommand.getName())));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§3§l> §bExecute this command")));
        sender.spigot().sendMessage(component);
    }
}
