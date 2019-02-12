package de.godtitan.lobby.listener;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import de.godtitan.lobby.command.BuildCommand;
import de.godtitan.lobby.util.ItemBuilder;
import de.godtitan.lobby.util.LobbyItems;
import de.godtitan.lobby.util.RandomFireworkGenerator;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class JumpAndRunListener implements Listener {

    private static final ItemStack WAIT = new ItemBuilder(Material.INK_SACK, (short) 8).setDisplayName("§cBitte warte kurz...").build();

    @Getter
    private static JumpAndRunListener instance;

    @Getter
    private Map<String, Type> jumpAndRuns;

    @Getter
    private Map<String, JumpAndRunScheduler> schedulers;

    private Lobby lobby;

    public JumpAndRunListener(Lobby lobby) {
        instance = this;

        this.jumpAndRuns = new HashMap<>();
        this.schedulers = new HashMap<>();
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getLocation().getBlock().getType() == Material.GOLD_PLATE) {

            if (jumpAndRuns.containsKey(player.getName())) {
                return;
            }

            if (BuildCommand.getBuilding().contains(player.getName())) {
                return;
            }

            switch (player.getLocation().subtract(0, 1, 0).getBlock().getType()) {
                case WOOD_STEP:
                    jumpAndRuns.put(player.getName(), Type.SEA);
                    break;
                case STAINED_CLAY:
                    jumpAndRuns.put(player.getName(), Type.HILL);
                    break;
                case STONE:
                    jumpAndRuns.put(player.getName(), Type.LIGHTHOUSE);
                    break;
            }

            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            lobby.getLobbyItems().giveJumpAndRunItems(player);
            JumpAndRunScheduler scheduler = new JumpAndRunScheduler(player);
            schedulers.put(player.getName(), scheduler);
        } else if (player.getLocation().getBlock().getType() == Material.IRON_PLATE) {

            if (!jumpAndRuns.containsKey(player.getName())) {
                return;
            }

            if (BuildCommand.getBuilding().contains(player.getName())) {
                return;
            }

            if (schedulers.containsKey(player.getName())) {
                schedulers.get(player.getName()).finish();
            }

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (stack != null) {
                if (stack.equals(LobbyItems.BACK)) {
                    if (schedulers.containsKey(player.getName())) {
                        schedulers.get(player.getName()).reset();
                    }

                    player.getInventory().setItem(1, WAIT);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(lobby, () -> {
                        if (player.isOnline() && jumpAndRuns.containsKey(player.getName())) {
                            player.getInventory().setItem(1, LobbyItems.BACK);
                        }
                    }, 20);
                } else if (stack.equals(LobbyItems.SPAWN)) {
                    if (schedulers.containsKey(player.getName())) {
                        schedulers.get(player.getName()).cancel();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (schedulers.containsKey(player.getName())) {
            schedulers.get(player.getName()).cancel();
        }
    }

    public enum Type {
        SEA("Sea", "See"),
        LIGHTHOUSE("Lighthouse", "Leuchtturm"),
        HILL("Hill", "Hügel");

        @Getter
        private Location location;

        @Getter
        private String name;

        Type(String locationName, String name) {
            this.location = Lobby.getInstance().getLocationManager().getLocation(locationName);
            this.name = name;
        }
    }

    public class JumpAndRunScheduler implements Runnable {

        private long startMillis;
        private int counter;
        private ScheduledFuture task;
        private UUID uuid;

        public JumpAndRunScheduler(Player player) {
            this.uuid = player.getUniqueId();
            this.start();
        }

        public void start() {
            Player player = getPlayer();
            if (player == null)
                return;

            player.setAllowFlight(false);
            player.sendMessage(Messages.PREFIX + "Du hast das §aJump and Run §7begonnen!");
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
            this.counter = 0;
            this.startMillis = System.currentTimeMillis();
            this.task = lobby.getScheduledExecutorService().scheduleAtFixedRate(this, 0, 1000, TimeUnit.MILLISECONDS);
        }

        @Override
        public void run() {

            Player player = getPlayer();
            if (player == null)
                return;

            if (!jumpAndRuns.containsKey(player.getName())) {
                cancel();
                return;
            }

            if (counter > 100) {
                reset();
                return;
            }

            player.setLevel(counter);
            player.setExp((float) counter / 100.0f);

            counter++;
        }

        public void reset() {
            Player player = getPlayer();
            if (player == null)
                return;

            this.startMillis = System.currentTimeMillis();
            this.counter = 0;
            player.setLevel(0);
            player.setExp(0);
            player.teleport(jumpAndRuns.get(player.getName()).getLocation());
            player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
        }

        public void cancel() {
            Player player = getPlayer();
            if (player == null)
                return;

            player.setAllowFlight(true);
            player.setLevel(0);
            player.setExp(0);
            lobby.teleportToSpawn(player);
            schedulers.remove(player.getName());
            jumpAndRuns.remove(player.getName());

            task.cancel(true);
        }

        public void finish() {
            Player player = getPlayer();
            if (player == null)
                return;

            player.setAllowFlight(true);
            player.setLevel(0);
            player.setExp(0);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

            RandomFireworkGenerator.shootRandomFirework(player.getLocation(), 5);

            float differenceInSeconds = (float) (System.currentTimeMillis() - startMillis) / 1000.0f;
            player.sendMessage(Messages.PREFIX + "§a§lHerzlichen Glückwunsch!§7 Du hast das Jump and Run §e"
                    + jumpAndRuns.get(player.getName()).getName() + "§7 in §e" + NumberFormat.getInstance(Locale.GERMAN).format(differenceInSeconds) + " Sekunden§7 absolviert!");

            if (player.hasPermission("lobby.team")) {
                lobby.getLobbyItems().giveTeamItems(player);
            } else {
                lobby.getLobbyItems().giveItems(player);
            }

            schedulers.remove(player.getName());
            jumpAndRuns.remove(player.getName());
            task.cancel(true);
        }

        public Player getPlayer() {
            return Bukkit.getPlayer(uuid);
        }
    }

}
 