package me.lolok.strongholdfinder.listeners;

import me.lolok.strongholdfinder.handlers.StrongholdHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractionListener implements Listener {
    private final StrongholdHandler handler;

    public PlayerInteractionListener(StrongholdHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null || item.getType() != Material.ENDER_EYE || player.getWorld().getEnvironment() != World.Environment.NORMAL) return;

        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if (block != null && block.getType() == Material.END_PORTAL_FRAME) return;

        e.setCancelled(true);
        Location result = handler.getNearestLocation(player);
        handler.simulateEnderEye(player, result, e.getHand());
    }
}
