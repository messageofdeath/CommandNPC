package me.messageofdeath.CommandNPC.commands;

import net.citizensnpcs.api.util.Messaging;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.messageofdeath.CommandNPC.CommandNPC;
import me.messageofdeath.CommandNPC.Database.LanguageSettings.LanguageSettings;
import me.messageofdeath.CommandNPC.Database.PluginSettings.PluginSettings;
import me.messageofdeath.CommandNPC.Utilities.BungeeCord.BungeeCordUtil;

public class ReloadCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				if(sender.hasPermission("commandnpc.admin") || sender.isOp()) {
					if(PluginSettings.BungeeCord.getBoolean()) {
						BungeeCordUtil.disableUtil();
					}
					CommandNPC.getConfigX().initConfiguration();
					CommandNPC.getConfigX().loadConfiguration();
					Messaging.send(sender, LanguageSettings.Commands_CmdNPC_Reload.getSetting());
					if(PluginSettings.BungeeCord.getBoolean()) {
						BungeeCordUtil.setupUtil();
					}
					return true;
				}else{
					Messaging.sendError(sender, LanguageSettings.Commands_NoPermission.getSetting());
				}
			}else{
				Messaging.sendError(sender, LanguageSettings.Commands_WrongArgs.getSetting());
			}
		}else{
			Messaging.sendError(sender, LanguageSettings.Commands_WrongArgs.getSetting());
		}
		return false;
	}
}
