package me.messageofdeath.CommandNPC.commands;

import net.citizensnpcs.api.util.Messaging;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.messageofdeath.CommandNPC.CommandNPC;
import me.messageofdeath.CommandNPC.Utilities.BungeeCord.BungeeCordUtil;

public class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				if(sender.hasPermission("commandnpc.admin") || sender.isOp()) {
					if(CommandNPC.getConfigX().isBungeeCord()) {
						BungeeCordUtil.disableUtil();
					}
					CommandNPC.getConfigX().reloadConfig();
					Messaging.send(sender, "Reloaded CommandNPC");
					if(CommandNPC.getConfigX().isBungeeCord()) {
						BungeeCordUtil.setupUtil();
					}
					return true;
				}else{
					Messaging.sendError(sender, "You do not have permission for this!");
				}
			}else{
				Messaging.sendError(sender, "Wrong Args. Use /commandnpc reload");
			}
		}else{
			Messaging.sendError(sender, "Wrong Args. Use /commandnpc reload");
		}
		return false;
	}
}
