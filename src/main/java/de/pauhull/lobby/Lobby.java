package de.pauhull.lobby;

import de.pauhull.coins.spigot.inventory.BuyItemInventory;
import de.pauhull.lobby.command.*;
import de.pauhull.lobby.data.MySQL;
import de.pauhull.lobby.data.table.*;
import de.pauhull.lobby.display.LobbyScoreboard;
import de.pauhull.lobby.entity.EntityBalloon;
import de.pauhull.lobby.inventory.*;
import de.pauhull.lobby.listener.*;
import de.pauhull.lobby.manager.BalloonManager;
import de.pauhull.lobby.manager.LobbyLocationManager;
import de.pauhull.lobby.util.ActionBar;
import de.pauhull.lobby.util.HeadCache;
import de.pauhull.lobby.util.LobbyItems;
import de.pauhull.scoreboard.NovusScoreboardManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Lobby extends JavaPlugin {

    /* INSTANCE */
    @Getter
    private static Lobby instance;

    /* DATA */
    @Getter
    private ExecutorService executorService;
    @Getter
    private ScheduledExecutorService scheduledExecutorService;
    @Getter
    private FileConfiguration config;
    @Getter
    private MySQL mySQL;

    /* MISC */
    @Getter
    private LobbyLocationManager locationManager;
    @Getter
    private LobbyItems lobbyItems;
    @Getter
    private HeadCache headCache;
    @Getter
    private BalloonManager balloonManager;
    @Getter
    private NovusScoreboardManager scoreboardManager;

    /* INVENTORIES */
    @Getter
    private ServerInventory serverInventory;
    @Getter
    private BootsInventory bootsInventory;
    @Getter
    private BuyItemInventory buyItemInventory;
    @Getter
    private ProfileInventory profileInventory;
    @Getter
    private SkullInventory skullInventory;
    @Getter
    private LobbySwitcherInventory lobbySwitcherInventory;
    @Getter
    private NavigatorInventory navigatorInventory;
    @Getter
    private PlayerHiderInventory playerHiderInventory;
    @Getter
    private PrivacyPolicyInventory privacyPolicyInventory;
    @Getter
    private ShopInventory shopInventory;
    @Getter
    private NickInventory nickInventory;
    @Getter
    private BalloonsInventory balloonsInventory;
    @Getter
    private LoadingInventory loadingInventory;
    @Getter
    private PlayerMenuInventory playerMenuInventory;

    /* TABLES */
    @Getter
    private BootsTable bootsTable;
    @Getter
    private PrivacyPolicyTable privacyPolicyTable;
    @Getter
    private PlayerHiderTable playerHiderTable;
    @Getter
    private SkullsTable skullsTable;
    @Getter
    private BalloonTable balloonTable;
    @Getter
    private SelectedGadgetsTable selectedGadgetsTable;
    @Getter
    private LastLocationTable lastLocationTable;

    @Setter
    @Getter
    private boolean weather = false;
    private List<String> messages = Arrays.asList(
            "§8» §7Du benötigst Hilfe? §6§l/support§7! §8«",
            "§8» §6§lTS³: §b/teamspeak §8«",
            "§8» §6§lDiscord: §c/discord §8«",
            "§8» §7Du hast einen Hacker gefunden? §a§l/report <Spieler>§7! §8«"
    );
    private int messageTime = 10 * 20; // 10 seconds
    private int message = 0;
    private long ticks = 0;

    @Override
    public void onDisable() {
        this.mySQL.close();
        this.executorService.shutdown();
        this.scheduledExecutorService.shutdown();
    }

    public void onEnable() {
        /* INSTANCE */
        instance = this;

        /* DATA */
        this.executorService = Executors.newSingleThreadExecutor();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.config = copyAndLoad("config.yml", new File(getDataFolder(), "config.yml"));
        this.mySQL = new MySQL(config.getString("MySQL.Host"),
                config.getString("MySQL.Port"),
                config.getString("MySQL.Database"),
                config.getString("MySQL.User"),
                config.getString("MySQL.Password"),
                config.getBoolean("MySQL.SSL"));

        if (!this.mySQL.connect()) {
            return;
        }

        /* MISC */
        this.balloonManager = new BalloonManager();
        this.locationManager = new LobbyLocationManager(this);
        this.scoreboardManager = new NovusScoreboardManager(this, LobbyScoreboard.class);
        this.lobbyItems = new LobbyItems();
        this.headCache = new HeadCache();

        /* INVENTORIES */
        this.serverInventory = new ServerInventory(this);
        this.bootsInventory = new BootsInventory(this);
        this.buyItemInventory = new BuyItemInventory();
        this.profileInventory = new ProfileInventory(this);
        this.skullInventory = new SkullInventory(this);
        this.lobbySwitcherInventory = new LobbySwitcherInventory(this);
        this.navigatorInventory = new NavigatorInventory(this);
        this.playerHiderInventory = new PlayerHiderInventory(this);
        this.privacyPolicyInventory = new PrivacyPolicyInventory(this);
        this.shopInventory = new ShopInventory(this);
        this.nickInventory = new NickInventory(this);
        this.loadingInventory = new LoadingInventory(this);
        this.balloonsInventory = new BalloonsInventory(this);
        this.playerMenuInventory = new PlayerMenuInventory(this);

        /* MYSQL TABLES */
        this.skullsTable = new SkullsTable(mySQL, executorService);
        this.bootsTable = new BootsTable(mySQL, executorService);
        this.playerHiderTable = new PlayerHiderTable(mySQL, executorService);
        this.privacyPolicyTable = new PrivacyPolicyTable(mySQL, executorService);
        this.balloonTable = new BalloonTable(mySQL, executorService);
        this.selectedGadgetsTable = new SelectedGadgetsTable(mySQL, executorService);
        this.lastLocationTable = new LastLocationTable(mySQL, executorService);

        /* COMMANDS */
        new BuildCommand(this);
        new WeatherCommand(this);
        new SetLocationCommand(this);
        new SpawnCommand(this);
        new SpawnVillagerCommand(this);
        new TextCommand(this);

        /* EVENTS */
        new CancelledEventListener(this);
        new PlayerInteractAtEntityListener(this);
        new PlayerJoinListener(this);
        new PlayerJoinPartyListener(this);
        new PlayerLeavePartyListener(this);
        new PlayerMoveListener(this);
        new PlayerQuitListener(this);
        new PlayerTeleportListener(this);
        new ServerListPingListener(this);
        new SignClickListener(this);

        /* ACTION BAR */
        this.sendActionBar();
        EntityBalloon.schedule();

        balloonManager.removeAllInactiveBalloons();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setAllowFlight(true);
        }

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public void teleportToSpawn(Player player, Location location) {
        if (BuildCommand.getBuilding().contains(player.getName())) {
            BuildCommand.getBuilding().remove(player.getName());
            player.sendMessage(Messages.PREFIX + "§7Du hast den Buildmodus §cverlassen§7!");
        }

        player.setFoodLevel(20);
        player.setHealth(20);
        player.setGameMode(GameMode.ADVENTURE);

        player.setExp(0);
        player.setLevel(0);

        if (player.hasPermission("lobby.team")) {
            lobbyItems.giveTeamItems(player);
        } else {
            lobbyItems.giveItems(player);
        }

        if (location == null) {
            locationManager.teleport(player, "Spawn");
        } else {
            player.teleport(location);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            player.setAllowFlight(true);
        }, 10);
    }

    private void sendActionBar() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (++ticks % messageTime == 0)
                message++;

            if (ticks % 20 == 0) { // every second
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ActionBar.sendActionBar(player, messages.get(message % messages.size()));
                }
            }

            if (ticks % 20 == 0) { // update every xp bar segment
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setExp(1f - (ticks % messageTime) / (float) messageTime);
                }
            }
        }, 0, 1);
    }

    private FileConfiguration copyAndLoad(String resource, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(getResource(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

} 