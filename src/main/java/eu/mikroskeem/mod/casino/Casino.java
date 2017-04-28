package eu.mikroskeem.mod.casino;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import eu.mikroskeem.mod.casino.config.CasinoConfiguration;
import eu.mikroskeem.mod.casino.config.categories.LoseCategory;
import eu.mikroskeem.mod.casino.config.categories.WaitCategory;
import eu.mikroskeem.mod.casino.config.categories.WinCategory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Casino extends JavaPlugin implements Listener {
    private final HashSet<Player> players = new HashSet<>();
    private final HashMap<Player, Integer> runnables = new HashMap<>();
    private final Random random = new Random();

    private CasinoConfigurationLoader configurationLoader;
    private CasinoConfiguration configuration;
    private Economy economy;

    @Override
    public void onEnable() {
        configurationLoader = new CasinoConfigurationLoader(this);
        configurationLoader.load();
        configurationLoader.save();
        configuration = configurationLoader.getConfiguration();
        economy = checkNotNull(getServer().getServicesManager().getRegistration(Economy.class).getProvider());
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if(event.getLine(configuration.getSignCheckLine()).equals(configuration.getSignCreateLine())
                && player.hasPermission("casino.createsign")) {
            event.setLine(configuration.getSignCheckLine(), configuration.getSignDoneLine());
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if(block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
                Sign sign = (Sign)block.getState();
                Player player = event.getPlayer();
                if(sign.getLine(configuration.getSignCheckLine()).equals(configuration.getSignDoneLine())
                        && player.hasPermission("casino.use")
                        && !players.contains(player)
                        && !runnables.containsKey(player)) {
                    player.sendMessage(c(configuration.getMessages().getEnterMoney()));
                    players.add(player);
                }

            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if(players.remove(player)) {
            event.setCancelled(true);
            if(event.getMessage().equalsIgnoreCase("cancel")) {
                player.sendMessage(c(configuration.getMessages().getCancel()));
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(event.getMessage());
            } catch (NumberFormatException e) {
                player.sendMessage(c(configuration.getMessages().getCancelWrongAmount()));
                return;
            }

            if(amount < configuration.getMinAmount()) {
                player.sendMessage(c(configuration.getMessages().getCancelTooLittle()));
                return;
            }

            if(amount > configuration.getMaxAmount()) {
                player.sendMessage(c(configuration.getMessages().getCancelTooBig()));
                return;
            }
            if(economy.withdrawPlayer(player, amount).transactionSuccess()) {
                runnables.put(player, getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                    int left = configuration.getProcessTime();
                    public void run() {
                        --left;
                        if (left == 0) {
                            Bukkit.getScheduler().cancelTask(runnables.remove(player));
                            if (random.nextDouble() < configuration.getWinChance()) {
                                double winAmount = amount * 2.0D;
                                economy.depositPlayer(player, winAmount);

                                WinCategory win = configuration.getWin();
                                player.sendTitle(
                                        c(win.getTitle().replaceAll("%amount%", ""+winAmount)),
                                        c(win.getSubtitle().replaceAll("%amount%", ""+winAmount)),
                                        win.getFadeIn(),
                                        win.getStay(),
                                        win.getFadeOut());
                            } else {
                                LoseCategory lose = configuration.getLose();
                                player.sendTitle(
                                        c(lose.getTitle().replaceAll("%amount%", ""+amount)),
                                        c(lose.getSubtitle().replaceAll("%amount%", ""+amount)),
                                        lose.getFadeIn(),
                                        lose.getStay(),
                                        lose.getFadeOut());
                            }
                        } else {
                            WaitCategory wait = configuration.getWait();
                            player.sendTitle(
                                    c(wait.getTitle().replaceAll("%seconds%", ""+left)),
                                    c(wait.getSubtitle().replaceAll("%seconds%", ""+left)),
                                    wait.getFadeIn(),
                                    wait.getStay(),
                                    wait.getFadeOut());
                        }
                    }
                }, 20L, 20L));
                player.sendMessage(c(configuration.getMessages().getProcessing()));
            } else {
                player.sendMessage(c(configuration.getMessages().getNotEnoughMoney()));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("casino")) {
            configurationLoader.load();
            configurationLoader.save();
            configuration = configurationLoader.getConfiguration();
            sender.sendMessage("Configuration reloaded!");
            return true;
        }
        return false;
    }

    private static String c(String c) {
        return ChatColor.translateAlternateColorCodes('&', "" + c);
    }
}