package eu.mikroskeem.mod.casino;

import com.comphenix.packetwrapper.WrapperPlayClientUpdateSign;
import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEditor;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Mark V.
 */
public class FakeSignFactory {
    private ProtocolManager protocolManager;
    private PacketAdapter packetListener;
    private Map<String, FakeSignListener> listeners;
    private Map<String, Vector> signLocations;

    /**
     * Construct FakeSignFactory
     *
     * @param plugin Plugin to register packet listeners on
     */
    public FakeSignFactory(Plugin plugin) {
        protocolManager = ProtocolLibrary.getProtocolManager();
        packetListener = new PacketListener(plugin);
        protocolManager.addPacketListener(packetListener);
        listeners = new ConcurrentHashMap<>();
        signLocations = new ConcurrentHashMap<>();
    }

    /**
     * Open fake sign on player screen
     *
     * @param player Player to open sign on
     * @param response Sign response callback
     */
    public void open(Player player, FakeSignListener response) {
        open(player, (Location)null, response);
    }

    /**
     * Open fake sign on player screen
     *
     * @param player Player to open sign on
     * @param signLocation Fake sign location
     * @param response Sign response callback
     */
    public void open(Player player, Location signLocation, FakeSignListener response) {
        int x = 0, y = 0, z = 0;
        if (signLocation != null) {
            x = signLocation.getBlockX();
            y = signLocation.getBlockY();
            z = signLocation.getBlockZ();
        }

        WrapperPlayServerOpenSignEditor openSignEditor = new WrapperPlayServerOpenSignEditor();
        openSignEditor.setLocation(new BlockPosition(x, y, z));

        try {
            protocolManager.sendServerPacket(player, openSignEditor.getHandle());
            signLocations.put(player.getName(), new Vector(x, y, z));
            listeners.put(player.getName(), response);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open fake sign on player screen
     *
     * @param player Player to open sign on
     * @param defaultText Sign default text
     * @param response Sign response callback
     */
    @SuppressWarnings("deprecated")
    public void open(Player player, String[] defaultText, FakeSignListener response) {
        List<PacketContainer> packets = new ArrayList<>();

        int x = 0, y = 255, z = 0;
        if (defaultText != null) {
            x = player.getLocation().getBlockX();
            z = player.getLocation().getBlockZ();
            Location signLocation = new Location(player.getWorld(), (double)x, (double)y, (double)z);

            /* Send block change */
            player.sendBlockChange(signLocation, Material.WALL_SIGN, (byte)0);

            /*
            TODO: those didn't seem to work
            WrapperPlayServerBlockChange block = new WrapperPlayServerBlockChange();
            block.setBlockData(WrappedBlockData.createData(Material.WALL_SIGN));
            block.setLocation(new BlockPosition(signLocation.toVector()));
            block.sendPacket(player);
            */

            /* Set sign content */
            player.sendSignChange(signLocation, defaultText);
        }

        /* Open sign editor */
        WrapperPlayServerOpenSignEditor openSignEditor = new WrapperPlayServerOpenSignEditor();
        openSignEditor.setLocation(new BlockPosition(x, y, z));
        packets.add(openSignEditor.getHandle());

        try {
            for (PacketContainer packet : packets) {
                protocolManager.sendServerPacket(player, packet);
            }
            if (defaultText != null) {
                /* Make sign disappear */
                Location signLocation = new Location(player.getWorld(), (double)x, (double)y, (double)z);

                /* Send block change */
                player.sendBlockChange(signLocation, Material.AIR, (byte)0);

                /*
                TODO: those didn't seem to work
                WrapperPlayServerBlockChange block = new WrapperPlayServerBlockChange();
                block.setBlockData(WrappedBlockData.createData(Material.AIR));
                block.setLocation(new BlockPosition(signLocation.toVector()));
                block.sendPacket(player);
                */
            }
            signLocations.put(player.getName(), new Vector(x, y, z));
            listeners.put(player.getName(), response);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Destroy FakeSignFactory
     */
    public void destroy() {
        protocolManager.removePacketListener(packetListener);
        listeners.clear();
        signLocations.clear();
    }

    public interface FakeSignListener {
        void onSignDone(Player player, String[] lines);
    }

    private class PacketListener extends PacketAdapter {
        Plugin plugin;

        public PacketListener(Plugin plugin) {
            super(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.UPDATE_SIGN);
            this.plugin = plugin;
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            final Player player = event.getPlayer();

            /* Check if it's sign update packet */
            PacketContainer packet = event.getPacket();
            if(packet.getType() == PacketType.Play.Client.UPDATE_SIGN){
                /* Wrap packet */
                final WrapperPlayClientUpdateSign updateSign = new WrapperPlayClientUpdateSign(packet);
                Location signLocation = updateSign.getLocation().toLocation(player.getWorld());

                /* Remove sign location from signLocations */
                Vector v = signLocations.remove(player.getName());
                if (v == null) return;
                if (signLocation.getBlockX() != v.getBlockX() ||
                        signLocation.getBlockY() != v.getBlockY() ||
                        signLocation.getBlockZ() != v.getBlockZ()) return;

                /* Do callback */
                final FakeSignListener response = listeners.remove(event.getPlayer().getName());
                if (response != null) {
                    event.setCancelled(true);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, ()->
                            response.onSignDone(player, updateSign.getLines()));
                }
            }
        }
    }
}
