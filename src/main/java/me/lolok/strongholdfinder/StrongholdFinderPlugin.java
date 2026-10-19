package me.lolok.strongholdfinder;

import lombok.Getter;
import me.lolok.strongholdfinder.commands.CommandManager;
import me.lolok.strongholdfinder.handlers.DefaultStrongholdHandler;
import me.lolok.strongholdfinder.handlers.StrongholdHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class StrongholdFinderPlugin extends JavaPlugin {
    @Getter
    private final StrongholdHandler handler = new DefaultStrongholdHandler(this);

    @Getter
    private final CommandManager commandManager = new CommandManager(this, "strongholdfinder");

    @Override
    public void onLoad() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        handler.enable();
        commandManager.enable();

        Bukkit.getConsoleSender().sendMessage("§a| Plugin: " + getDescription().getName());
        Bukkit.getConsoleSender().sendMessage("§a| Version: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        handler.disable();
    }
}