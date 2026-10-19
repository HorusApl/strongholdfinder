package me.lolok.strongholdfinder.handlers;

import lombok.Getter;
import me.lolok.strongholdfinder.utils.LocationsParser;
import me.lolok.strongholdfinder.handlers.enums.LookupMode;
import me.lolok.strongholdfinder.StrongholdFinderPlugin;
import me.lolok.strongholdfinder.listeners.PlayerInteractionListener;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StructureSearchResult;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class DefaultStrongholdHandler implements StrongholdHandler {
    private final StrongholdFinderPlugin plugin;

    private LookupMode mode = LookupMode.STATIC;
    private final HashMap<String, List<String>> locations = new HashMap<>();
    private int radius;
    private String nearbyLocationMessage, notFoundMessage;
    private boolean displayLocation, explosionNotFound;
    private double explosionChance;

    public DefaultStrongholdHandler(StrongholdFinderPlugin plugin) {
        this.plugin = plugin;
        loadConfig(plugin.getConfig());
    }

    @Override
    public void enable() {
        Listener listener = new PlayerInteractionListener(this);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(plugin);
    }

    @Override
    public @Nullable Location getNearestLocation(Player player) {
        if (mode == LookupMode.AUTO) {
            StructureSearchResult result = player.getWorld().locateNearestStructure(player.getLocation(), StructureType.STRONGHOLD, radius, false);
            if (result != null)
                return result.getLocation();
        } else {
            if (locations.containsKey(player.getWorld().getName())) {
                List<String> list = locations.get(player.getWorld().getName());
                Location nearest = null;
                for (String s : list) {
                    Location location = LocationsParser.parse(player.getWorld(), s);
                    if (location == null) continue;
                    if (nearest == null || player.getLocation().distance(nearest) > player.getLocation().distance(location))
                        nearest = location;
                }
                return nearest;
            }
        }
        return null;
    }

    @Override
    public void simulateEnderEye(Player player, Location result, EquipmentSlot slot) {
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack eye = slot == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand();
            eye.setAmount(eye.getAmount() - 1);
        }

        if (result == null)
            player.sendMessage(notFoundMessage);
        else if (displayLocation)
            player.sendMessage(nearbyLocationMessage.replace("%x%", String.valueOf(result.getBlockX())).replace("%y%", String.valueOf(result.getBlockY())).replace("%z%", String.valueOf(result.getBlockZ())));

        Location location = player.getLocation();
        location.setY(location.getY() + player.getEyeHeight());
        boolean drop = player.getGameMode() != GameMode.CREATIVE && ((result == null && !explosionNotFound) || (result != null && new Random().nextDouble(100) > explosionChance));

        EnderSignal signal = player.getWorld().spawn(location, EnderSignal.class);
        signal.setTargetLocation(result == null ? location.add(location.getDirection().multiply(2)) : result);
        signal.setDropItem(drop);
        player.getWorld().playSound(location, Sound.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 10.0F, 1.0F);
    }

    @Override
    public void addLocation(Location location) {
        String world = Objects.requireNonNull(location.getWorld()).getName();
        String s = String.format("%d:%d:%d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
        List<String> locations = this.locations.containsKey(world) ? this.locations.get(world) : new ArrayList<>();
        locations.add(s);
        this.locations.put(world, locations);
        plugin.getConfig().set(String.format("static.locations.%s", world), locations);
        plugin.saveConfig();
    }

    @Override
    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig(plugin.getConfig());
    }

    private void loadConfig(FileConfiguration config) {
        //Load lookup mode
        String name = config.getString("mode", "STATIC");
        try {
            this.mode = LookupMode.valueOf(name);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning(String.format("Invalid lookup mode '%s'. STATIC mode is automatically set", name));
        }

        //Load display location option
        this.displayLocation = config.getBoolean("display-location", false);

        //Load exploding options
        this.explosionNotFound = config.getBoolean("explosion.not-found", true);
        this.explosionChance = Double.parseDouble(config.getString("explosion.chance", "20").replace("%", ""));

        //Load automatic lookup radius
        this.radius = config.getInt("auto.lookup-radius", 16);

        //Load messages
        this.nearbyLocationMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.nearby-location", "&3&l! &fNearest stronghold: &bX: %x% Y: %y% Z: %z%"));
        this.notFoundMessage = ChatColor.translateAlternateColorCodes('&', config.getString("messages.not-found", "&6&l! &eThere appears to be no stronghold nearby..."));

        //Load locations
        ConfigurationSection locations = config.getConfigurationSection("static.locations");
        if (locations == null) {
            plugin.getLogger().warning("Unable to find the locations config section");
            return;
        }
        for (String world : locations.getKeys(false))
            this.locations.put(world, config.getStringList(String.format("static.locations.%s", world)));
    }
}
