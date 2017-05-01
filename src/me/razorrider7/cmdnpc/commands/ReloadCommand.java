package me.razorrider7.cmdnpc.commands;

import me.razorrider7.cmdnpc.CommandNPC;
import net.citizensnpcs.api.util.Messaging;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args[0].equalsIgnoreCase("reload") && (sender.hasPermission("commandnpc.admin") || sender.isOp())) {
			CommandNPC.getConfigX().reloadConfig();
			Messaging.send(sender, "Reloaded CommandNPC");
			return true;
		}
		return false;
	}
}
