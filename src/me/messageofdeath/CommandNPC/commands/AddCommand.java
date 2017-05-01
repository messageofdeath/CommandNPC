package me.messageofdeath.CommandNPC.commands;

import me.messageofdeath.CommandNPC.CommandNPC;
import me.messageofdeath.CommandNPC.Database.ClickType;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCCommand;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCData;
import net.citizensnpcs.api.command.Command;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.command.Requirements;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;

import org.bukkit.command.CommandSender;

@Requirements(selected = true, ownership = true)
public class AddCommand {

	@Command(aliases = { "npc" }, usage = "addcmd [-c] [-o] [--v price] [--p custom.permission.node] <command>", desc = "Add a command to a NPC", modifiers = { "addcmd" }, min = 2, flags = "ocpvlrb")
	public void addCmd(CommandContext args, CommandSender sender, NPC npc) {
		if (sender.hasPermission("commandnpc.admin")) {
			int id = npc.getId();
			String permission = "noPerm";
			ClickType clickType = null;
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
			if (args.hasFlag('l')) {
				clickType = ClickType.LEFT;
			}else if (args.hasFlag('r')) {
				clickType = ClickType.RIGHT;
			}else if (args.hasFlag('b')) {
				clickType = ClickType.BOTH;
			}else if (clickType == null) {
				clickType = CommandNPC.getConfigX().getClickType();
			}
			if (args.hasValueFlag("v")) {
				cost = args.getFlagDouble("v");
			}
			if (args.hasValueFlag("p")) {
				permission = args.getFlag("p");
			}
			if(args.argsLength() > 1) {
				cmd = args.getJoinedStrings(1);
			}
			if (cmd != null) {
				if (CommandNPC.getCommandManager().hasNPCData(id)) {
					CommandNPC.getCommandManager().getNPCData(id).addCommand(new NPCCommand(cmd, permission, clickType, inConsole, asOp, cost));
				} else {
					NPCData data = new NPCData(id, new NPCCommand(cmd, permission, clickType, inConsole, asOp, cost));
					CommandNPC.getCommandManager().addNPCData(data);
				}
				CommandNPC.getCommandDatabase().saveDatabase();
				Messaging.send(sender, "You have successfully added the command to the npc!");
			}else{
				Messaging.send(sender, "You didn't input a command!");
			}
		} else {
			Messaging.send(sender, "You don't have permission to execute that command!");
		}
	}
}
