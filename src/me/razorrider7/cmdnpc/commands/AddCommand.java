package me.razorrider7.cmdnpc.commands;

import me.razorrider7.cmdnpc.CommandNPC;
import me.razorrider7.cmdnpc.util.npcdatamanager.NPCCommand;
import me.razorrider7.cmdnpc.util.npcdatamanager.NPCData;
import net.citizensnpcs.api.command.Command;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.command.Requirements;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@Requirements(selected = true, ownership = true)
public class AddCommand {

	@Command(aliases = { "npc" }, usage = "addcmd [-c] [-o] [-v price] [-p custom.permission.node] <command>", desc = "Add a command to a NPC", modifiers = { "addcmd" }, min = 2, flags = "ocpv")
	public void addCmd(CommandContext args, CommandSender sender, NPC npc) {
		if (sender.hasPermission("commandnpc.admin")) {
			int id = npc.getId();
			String permission = "";
			boolean inConsole = false;
			boolean asOp = false;
			String cmd = null;
			double cost = 0;

			if (args.hasFlag('c')) {
				inConsole = true;
			}
			if (args.hasFlag('o')) {
				asOp = true;
			}
			if (args.hasFlag('v') && args.hasFlag('p')) {
				try {
					cost = Math.rint(Double.parseDouble(args.getString(1)) * 100) / 100;
				} catch (NumberFormatException e) {
					Bukkit.getServer().getConsoleSender().sendMessage("Command NPC Error: " + e.getMessage());
				}
				permission = args.getString(2);
				cmd = args.getJoinedStrings(3);
			} else if (args.hasFlag('v') && args.getString(2) != null) {
				try {
					cost = Math.rint(Double.parseDouble(args.getString(1)) * 100) / 100;
				} catch (NumberFormatException e) {
					Bukkit.getServer().getConsoleSender().sendMessage("Command NPC Error: " + e.getMessage());
				}
				cmd = args.getJoinedStrings(2);
			} else if (args.hasFlag('p') && args.getString(2) != null) {
				permission = args.getString(1);
				cmd = args.getJoinedStrings(2);
			} else {
				cmd = args.getJoinedStrings(1);
			}

			if (cmd != null) {
				if (CommandNPC.getCommandManager().hasNPCData(id)) {
					CommandNPC.getCommandManager().getNPCData(id).addCommand(new NPCCommand(cmd, permission, inConsole, asOp, cost));
				} else {
					NPCData data = new NPCData(id, new NPCCommand(cmd, permission, inConsole, asOp, cost));
					CommandNPC.getCommandManager().addNPCData(data);
				}
				CommandNPC.getCommandDatabase().saveDatabase();
				Messaging.send(sender, "You have successfully added the command to the npc!");
			}
		} else {
			Messaging.send(sender, "You don't have permission to execute that command!");
		}
	}
}
