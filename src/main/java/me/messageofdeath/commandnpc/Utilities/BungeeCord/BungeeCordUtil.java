package me.messageofdeath.commandnpc.Utilities.BungeeCord;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.messageofdeath.commandnpc.CommandNPC;

public class BungeeCordUtil {
	
	public static void setupUtil() {
		JavaPlugin plugin = CommandNPC.getInstance();
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
	}
	
	public static void disableUtil() {
		JavaPlugin plugin = CommandNPC.getInstance();
		plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, "BungeeCord");
	}
	
	public static void sendPlayerToServer(Player player, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		player.sendPluginMessage(CommandNPC.getInstance(), "BungeeCord", out.toByteArray());
	}
}
