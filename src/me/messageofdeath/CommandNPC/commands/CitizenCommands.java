package me.messageofdeath.CommandNPC.commands;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import me.messageofdeath.CommandNPC.CommandNPC;
import me.messageofdeath.CommandNPC.Database.ClickType;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCCommand;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCData;
import net.citizensnpcs.api.command.Command;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.command.Requirements;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;

@Requirements(selected = true, ownership = true)
public class CitizenCommands {

	@Command(aliases = { "npc" }, usage = "cmdadd [-c console] [-o Op] [--v price] [--t clickType] [--d delay] [--p custom.permission.node] <command...>", 
			desc = "Add a command to a NPC", modifiers = { "cmdadd" }, min = 2, flags = "oc", permission = "commandnpc.admin")
	public void addCmd(CommandContext args, CommandSender sender, NPC npc) {
		int id = npc.getId();
		String permission = "noPerm";
		ClickType clickType = CommandNPC.getConfigX().getClickType();
		boolean inConsole = false;
		boolean asOp = false;
		String cmd = null;
		double cost = 0;
		int delay = 0;
		
		if (args.hasFlag('c')) {
			inConsole = true;
		}
		if (args.hasFlag('o')) {
			asOp = true;
		}
		if(args.hasValueFlag("t")) {
			String value = args.getFlag("t");
			if(value.equalsIgnoreCase("left")) {
				clickType = ClickType.LEFT;
			}else if(value.equalsIgnoreCase("right")) {
				clickType = ClickType.RIGHT;
			}else if(value.equalsIgnoreCase("both")) {
				clickType = ClickType.BOTH;
			}else{
				Messaging.sendError(sender, "For value flag 't' you must use 'left', 'right', or 'both'!");
			}
		}
		if (args.hasValueFlag("p")) {
			permission = args.getFlag("p");
		}
		if (args.hasValueFlag("v")) {
			if(StringUtils.isNumeric(args.getFlag("v"))) {
				cost = args.getFlagDouble("v");
			}else{
				Messaging.sendError(sender, "The cost variable must be numeric!");
			}
		}
		if (args.hasValueFlag("d")) {
			if(StringUtils.isNumeric(args.getFlag("d"))) {
				delay = args.getFlagInteger("d");
			}else{
				Messaging.sendError(sender, "The delay variable must be numeric!");
			}
		}
		cmd = args.getJoinedStrings(1);
		if (cmd != null) {
			NPCCommand npcCommand = new NPCCommand(cmd, permission, clickType, inConsole, asOp, cost, delay);
			if (CommandNPC.getCommandManager().hasNPCData(id)) {
				CommandNPC.getCommandManager().getNPCData(id).addCommand(npcCommand);
			} else {
				CommandNPC.getCommandManager().addNPCData(new NPCData(id, npcCommand));
			}
			CommandNPC.getCommandDatabase().saveDatabase();
			Messaging.send(sender, "You have successfully added the command to the npc!");
		}else{
			Messaging.send(sender, "You didn't input a command!");
		}
	}
	
	@Command(aliases = { "npc" }, usage = "cmdremove <id>", desc = "Remove a command on the NPC.", modifiers = { "cmdremove" }, min = 2, max = 2, permission = "commandnpc.admin")
	public void removeCmd(CommandContext args, CommandSender sender, NPC npc) {
		int id = npc.getId();
		if(CommandNPC.getCommandManager().hasNPCData(id)) {
			NPCData data = CommandNPC.getCommandManager().getNPCData(id);
			if(StringUtils.isNumeric(args.getString(1))) {
				if(data.hasCommand(args.getInteger(1))) {
					data.removeCommand(args.getInteger(1));
					Messaging.send(sender, "You have successfully removed the command!");
				}else{
					Messaging.sendError(sender, "There is not a command with that ID!");
				}
			}else{
				Messaging.sendError(sender, "You must enter a number between 1 and " + data.getCommands().size() + "!");
			}
		}else{
			Messaging.sendError(sender, "There are no set commands at the moment for this NPC!");
		}
	}
	
	@Command(aliases = { "npc" }, usage = "cmdset <id> [--p custom.permission.node] [--v price] [--t clickType] [--d delay] [-c console] [-o Op] [command...]", 
			desc = "Set various variables for the command.", modifiers = { "cmdset" }, min = 2, flags = "co", permission = "commandnpc.admin")
	public void setCmd(CommandContext args, CommandSender sender, NPC npc) {
		int npcID = npc.getId();
		if(StringUtils.isNumeric(args.getString(1))) {
			int id = args.getInteger(1);
			if(CommandNPC.getCommandManager().hasNPCData(npcID)) {
				NPCData data = CommandNPC.getCommandManager().getNPCData(npcID);
				if(data.hasCommand(id)) {
					NPCCommand command = data.getCommand(id);
					Messaging.send(sender, CommandNPC.prefix + "You have set the following variables:");
					if(args.hasFlag('c')) {
						command.setInConsole(!command.inConsole());
						Messaging.send(sender, "Console to: " + command.inConsole());
					}
					if(args.hasFlag('o')) {
						command.setAsOP(!command.asOp());
						Messaging.send(sender, "Op to: " + command.asOp());
					}
					if(args.hasValueFlag("p")) {
						command.setPermission(args.getFlag("p"));
						Messaging.send(sender, "Permission to: " + command.getPermission());
					}
					if(args.hasValueFlag("v")) {
						if(StringUtils.isNumeric(args.getFlag("v"))) {
							command.setCost(args.getFlagDouble("v"));
							Messaging.send(sender, "Cost to: " + command.getCost());
						}else{
							Messaging.sendError(sender, "The cost variable must be numeric!");
						}
					}
					if (args.hasValueFlag("d")) {
						if(StringUtils.isNumeric(args.getFlag("d"))) {
							command.setDelay(args.getFlagInteger("d"));
							Messaging.send(sender, "Delay to: " + command.getDelay());
						}else{
							Messaging.sendError(sender, "The delay variable must be numeric!");
						}
					}
					if(args.hasValueFlag("t")) {
						String value = args.getFlag("t");
						if(value.equalsIgnoreCase("left")) {
							command.setClickType(ClickType.LEFT);
							Messaging.send(sender, "ClickType to: " + command.getClickType().name().toLowerCase());
						}else if(value.equalsIgnoreCase("right")) {
							command.setClickType(ClickType.RIGHT);
							Messaging.send(sender, "ClickType to: " + command.getClickType().name().toLowerCase());
						}else if(value.equalsIgnoreCase("both")) {
							command.setClickType(ClickType.BOTH);
							Messaging.send(sender, "ClickType to: " + command.getClickType().name().toLowerCase());
						}else{
							Messaging.sendError(sender, "For value flag 't' you must use 'left', 'right', or 'both'!");
						}
					}
					if(args.argsLength() > 2) {
						command.setCommand(args.getJoinedStrings(2));
						Messaging.send(sender, "Command to: " + command.getCommand());
					}
					CommandNPC.getCommandDatabase().saveDatabase();
				}else{
					Messaging.sendError(sender, "You must enter a number between 1 and " + data.getCommands().size() + "!");
				}
			}else{
				Messaging.sendError(sender, "There are no set commands at the moment for this NPC!");
			}
		}else{
			Messaging.sendError(sender, "The ID must be numeric!");
		}
	}
	
	@Command(aliases = { "npc" }, usage = "cmdinfo [ID]", desc = "Displays various information about the commands of an NPC.", modifiers = { "cmdinfo" }, min = 1, max = 2, 
			permission = "commandnpc.admin")
	public void infoCmds(CommandContext args, CommandSender sender, NPC npc) {
		int id = npc.getId();
		if(CommandNPC.getCommandManager().hasNPCData(id)) {
			NPCData data = CommandNPC.getCommandManager().getNPCData(id);
			ArrayList<NPCCommand> commands = null;
			if(args.argsLength() == 2) {
				if(StringUtils.isNumeric(args.getString(1))) {
					int cmdID = args.getInteger(1);
					if(data.hasCommand(cmdID)) {
						commands = new ArrayList<NPCCommand>();
						commands.add(data.getCommand(cmdID));
					}else{
						Messaging.sendError(sender, "You must enter a number between 1 and " + data.getCommands().size() + "!");
						return;
					}
				}else{
					Messaging.sendError(sender, "The ID must be numeric!");
					return;
				}
			}else{
				commands = data.getCommands();
			}
			Messaging.send(sender, CommandNPC.prefix + "Information for NPC '"+id+"'");
			for(NPCCommand command : commands) {
				Messaging.send(sender, "  &7Command ID: &6" + command.getID());
				Messaging.send(sender, " &8 -&2 Command: &b" + command.getCommand());
				Messaging.send(sender, " &8 -&2 Permission: &b" + command.getPermission());
				Messaging.send(sender, " &8 -&2 ClickType: &b" + command.getClickType().name().toLowerCase() + " &8| &2Cost: &b" + command.getCost()
				 + " &8| &2Delay: &b" + command.getDelay() + " ticks");
				Messaging.send(sender, " &8 -&2 In Console: &b" + command.inConsole() + " &8| &2As Op: &b" + command.asOp());
			}
		}else{
			Messaging.sendError(sender, "There are no set commands at the moment for this NPC!");
		}
	}
	
	@Command(aliases = { "npc" }, usage = "cmdreset", desc = "Reset the commands on the NPC.", modifiers = { "cmdreset" }, min = 1, max = 1, permission = "commandnpc.admin")
	public void resetCmds(CommandContext args, CommandSender sender, NPC npc) {
		int id = npc.getId();
		if(CommandNPC.getCommandManager().hasNPCData(id)) {
			CommandNPC.getCommandManager().removeNPCData(id);
			CommandNPC.getCommandDatabase().deleteNPC(id);
			Messaging.send(sender, "You have successfully reset the commands for this NPC!");
		}else{
			Messaging.sendError(sender, "There are no set commands at the moment for this NPC!");
		}
	}
}
