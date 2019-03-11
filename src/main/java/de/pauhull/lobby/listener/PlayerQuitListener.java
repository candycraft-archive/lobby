package de.pauhull.lobby.listener;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.entity.EntityBalloon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerQuitListener implements Listener {

    private Lobby lobby;

    public PlayerQuitListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);

        StringBuilder builder = new StringBuilder();
        for (EntityBalloon entityBalloon : lobby.getBalloonManager().getActiveBalloons(player)) {
            if (builder.length() > 0) {
                builder.append("/");
            }
            builder.append(entityBalloon.getBalloon().name());
        }

        ItemStack stack = player.getInventory().getHelmet();
        if (stack != null && stack.getType() == Material.SKULL_ITEM && stack.getDurability() == 3) {
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            String owner = meta.getOwner();
            lobby.getSelectedGadgetsTable().saveSelectedGadget(player.getUniqueId(), "SKULL", owner);
        } else {
            lobby.getSelectedGadgetsTable().saveSelectedGadget(player.getUniqueId(), "SKULL", null);
        }

        lobby.getBalloonManager().removeAllBalloons(player);
        lobby.getLastLocationTable().saveLocation(player.getUniqueId(), player.getLocation());

    }

}
