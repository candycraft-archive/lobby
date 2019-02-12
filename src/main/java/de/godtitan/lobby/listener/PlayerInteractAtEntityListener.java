package de.godtitan.lobby.listener;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import de.godtitan.lobby.command.BuildCommand;
import de.godtitan.lobby.shop.BetaPass;
import de.pauhull.uuidfetcher.common.communication.message.ConnectMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class PlayerInteractAtEntityListener implements Listener {

    private Lobby lobby;

    public PlayerInteractAtEntityListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if (BuildCommand.getBuilding().contains(player.getName())) {
            return;
        }

        event.setCancelled(true);

        if (event.getRightClicked().getType() == EntityType.ENDER_CRYSTAL) {
            EnderCrystal crystal = (EnderCrystal) event.getRightClicked();

            if (crystal.getName().startsWith("§cCommunity §8× §7§oRechtsklick")) {
                new ConnectMessage(player.getName(), "Community-1").sendToProxy("Proxy");
            }
        } else if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            ArmorStand armorStand = (ArmorStand) event.getRightClicked();

            if (armorStand.getCustomName().equals("§c§lBeta-Tester")) {
                player.closeInventory();

                lobby.getBetaTesterTable().isBetaTester(player.getUniqueId(), isBetaTester -> {
                    if (isBetaTester) {
                        player.sendMessage(Messages.PREFIX + "Du bist bereits ein §cBeta-Tester§7!");
                    } else {
                        lobby.getBuyItemInventory().show(player, BetaPass.BETA_PASS);
                    }
                });
            } else if (armorStand.getCustomName().equals("§cSchwarzmarkt")) {
                player.sendMessage("§eSchwarzmarkt §8» §7Ich habe gerade §cnichts§7! Und jetzt §cgeh§7, bevor uns hier jemmand sieht!");
            }
        }
    }
}
