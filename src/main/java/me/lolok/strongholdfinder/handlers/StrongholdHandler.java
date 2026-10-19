package me.lolok.strongholdfinder.handlers;

import me.lolok.strongholdfinder.Service;
import me.lolok.strongholdfinder.handlers.enums.LookupMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nullable;

public interface StrongholdHandler extends Service {
    @Nullable
    Location getNearestLocation(Player player);

    LookupMode getMode();

    int getRadius();

    boolean isDisplayLocation();

    String getNotFoundMessage();

    void simulateEnderEye(Player player, Location result, EquipmentSlot slot);

    void addLocation(Location location);

    void reloadConfig();
}
