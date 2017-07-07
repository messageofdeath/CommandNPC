package me.messageofdeath.commandnpc.commands;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import me.messageofdeath.commandnpc.CommandNPC;
import me.messageofdeath.commandnpc.Database.ClickType;
import me.messageofdeath.commandnpc.Database.LanguageSettings.LanguageSettings;
import me.messageofdeath.commandnpc.Database.PluginSettings.PluginSettings;
import me.messageofdeath.commandnpc.NPCDataManager.NPCCommand;
import me.messageofdeath.commandnpc.NPCDataManager.NPCData;
import me.messageofdeath.commandnpc.Utilities.Utilities;
import net.citizensnpcs.api.command.Command;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.command.Requirements;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;

@Requirements(selected = true, ownership = true)
public class CitizenCommands {

	@Command(aliases = { "npc" }, usage = "cmdadd [-c console] [-o Op] [-r random] [-i ignorePermMsg] [-l ignoreMoneyMsg] [--v price] [--t clickType] " +
			"[--d delay] [--cd cooldown] [--p custom.permission.node] <command...>",
			desc = "Add a command to a NPC", modifiers = { "cmdadd" }, min = 2, flags = "coril", permission = "commandnpc.admin")
	public void addCmd(CommandContext args, CommandSender sender, NPC npc) {
		int id = npc.getId();
		String permission = "noPerm";
		ClickType clickType;
		if(ClickType.hasClickType(PluginSettings.ClickType.getSetting())) {
			clickType = ClickType.getClickType(PluginSettings.ClickType.getSetting());
		}else{
			clickType = ClickType.BOTH;
			CommandNPC.getInstance().logError("ClickType", "CitizensCommands", "addCmd(CommandContext, CommandSender, NPC)", "ClickType from config.yml did not resolve! "
					+ "Resulting to ClickType BOTH!");
		}
		boolean inConsole = false;
		boolean isRandom = false;
		boolean asOp = false;
		boolean ignorePerm = false;
		boolean ignoreMoney = false;
		String cmd;
		double cost = 0;
		int delay = 0;
		int cooldown = 0;
		
		if (args.hasFlag('c')) {
			inConsole = true;
		}
		if (args.hasFlag('o')) {
			asOp = true;
		}
		if (args.hasFlag('r')) {
			isRandom = true;
		}
		if (args.hasFlag('i')) {
			ignorePerm = true;
		}
		if (args.hasFlag('l')) {
			ignoreMoney = true;
		}
		if(args.hasValueFlag("t")) {
			String value = args.getFlag("t");
			if(ClickType.hasClickType(value)) {
				clickType = ClickType.getClickType(value);
			}else{
				Messaging.sendError(sender, LanguageSettings.Commands_Citizens_ValueFlagT.getSetting());
			}
		}
		if (args.hasValueFlag("p")) {
			permission = args.getFlag("p");
		}
		if (args.hasValueFlag("v")) {
			if(Utilities.isDouble(args.getFlag("v"))) {
				cost = args.getFlagDouble("v");
			}else{
				Messaging.sendError(sender, LanguageSettings.Commands_MustBeNumeric.getSetting().replace("%arg", "price"));
			}
		}
		if (args.hasValueFlag("d")) {
			if(Utilities.isInteger(args.getFlag("d"))) {
				delay = args.getFlagInteger("d");
			}else{
				Messaging.sendError(sender, LanguageSettings.Commands_MustBeNumeric.getSetting().replace("%arg", "delay"));
			}
		}
		if(args.hasValueFlag("cd")) {
			if(Utilities.isInteger(args.getFlag("cd"))) {
				cooldown = args.getFlagInteger("cd");
			}else{
				Messaging.sendError(sender, LanguageSettings.Commands_MustBeNumeric.getSetting().replace("%arg", "cooldown"));
			}
		}
		cmd = args.getJoinedStrings(1);
		if (cmd != null) {
			NPCCommand npcCommand = new NPCCommand(cmd, permission, "", clickType, inConsole, asOp, isRandom, ignorePerm, ignoreMoney, cost, delay, cooldown);
			if (CommandNPC.getCommandManager().hasNPCData(id)) {
				CommandNPC.getCommandManager().getNPCData(id).addCommand(npcCommand);
			} else {
				CommandNPC.getCommandManager().addNPCData(new NPCData(id, npcCommand));
			}
			CommandNPC.getCommandDatabase().saveDatabase();
			Messaging.send(sender, LanguageSettings.Commands_Citizens_Add.getSetting());
		}else{
			Messaging.send(sender, LanguageSettings.Commands_Citizens_NoCmdInput.getSetting());
		}
	}
	
	@Command(aliases = { "npc" }, usage = "cmdremove <id>", desc = "Remove a command on the NPC.", modifiers = { "cmdremove" }, min = 2, max = 2, 
			permission = "commandnpc.admin")
	public void removeCmd(CommandContext args, CommandSender sender, NPC npc) {
		int id = npc.getId();
		if(CommandNPC.getCommandManager().hasNPCData(id)) {
			NPCData data = CommandNPC.getCommandManager().getNPCData(id);
			if(Utilities.isInteger(args.getString(1))) {
				if(data.hasCommand(args.getInteger(1))) {
					data.removeCommand(args.getInteger(1));
					Messaging.send(sender, LanguageSettings.Commands_Citizens_Removed.getSetting());
				}else{
					Messaging.sendError(sender, LanguageSettings.Commands_DoesNotExist.getSetting().replace("%type", "ID"));			}
			}else{
				Messaging.sendError(sender, LanguageSettings.Commands_Citizens_NumBetween.getSetting().replace("%num1", "1").replace("%num2", data.getCommands().size() + ""));
			}
		}else{
			Messaging.sendError(sender, LanguageSettings.Commands_Citizens_NoCommands.getSetting());
		}
	}
	
	@Command(aliases = { "npc" }, usage = "cmdset <id> [-c console] [-o Op] [-r random] [-m cdMsg] [-i ignorePermMsg] [-l ignoreMoneyMsg] [--v price] " +
			"[--t clickType] [--d delay] [--cd cooldown] [--p custom.permission.node] [command | cdMsg...]",
			desc = "Set various variables for the command.", modifiers = { "cmdset" }, min = 2, flags = "cormil", permission = "commandnpc.admin")
	public void setCmd(CommandContext args, CommandSender sender, NPC npc) {
		int npcID = npc.getId();
		if(Utilities.isInteger(args.getString(1))) {
			int id = args.getInteger(1);
			if(CommandNPC.getCommandManager().hasNPCData(npcID)) {
				NPCData data = CommandNPC.getCommandManager().getNPCData(npcID);
				if(data.hasCommand(id)) {
					NPCCommand command = data.getCommand(id);
					Messaging.send(sender, CommandNPC.prefix + LanguageSettings.Commands_SetTo_Header.getSetting());
					if(args.hasFlag('c')) {
						command.setInConsole(!command.inConsole());
						Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Console")
								.replace("%value", command.inConsole() + ""));
					}
					if(args.hasFlag('o')) {
						command.setAsOP(!command.asOp());
						Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Op")
								.replace("%value", command.asOp() + ""));
					}
					if(args.hasFlag('r')) {
						command.setIsRandom(!command.isRandom());
						Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Random")
								.replace("%value", command.isRandom() + ""));
					}
					if(args.hasFlag('i')) {
						command.setIgnorePermMsg(!command.isIgnorePermMsg());
						Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Ignore Perm Message")
								.replace("%value", command.isIgnorePermMsg() + ""));
					}
					if(args.hasFlag('l')) {
						command.setIgnoreMoneyMsg(!command.isIgnoreMoneyMsg());
						Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Ignore Money Message")
								.replace("%value", command.isIgnoreMoneyMsg() + ""));
					}
					if(args.hasValueFlag("p")) {
						command.setPermission(args.getFlag("p"));
						Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Permission")
								.replace("%value", command.getPermission()));
					}
					if(args.hasValueFlag("v")) {
						if(Utilities.isDouble(args.getFlag("v"))) {
							command.setCost(args.getFlagDouble("v"));
							Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Cost")
									.replace("%value", command.getCost() + ""));
						}else{
							Messaging.sendError(sender, LanguageSettings.Commands_MustBeNumeric.getSetting().replace("%arg", "cost"));
						}
					}
					if (args.hasValueFlag("d")) {
						if(Utilities.isInteger(args.getFlag("d"))) {
							command.setDelay(args.getFlagInteger("d"));
							Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Delay")
									.replace("%value", command.getDelay() + ""));
						}else{
							Messaging.sendError(sender, LanguageSettings.Commands_MustBeNumeric.getSetting().replace("%arg", "delay"));
						}
					}
					if(args.hasValueFlag("cd")) {
						if(Utilities.isInteger(args.getFlag("cd"))) {
							command.setCooldown(args.getFlagInteger("cd"));
							Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Cooldown")
									.replace("%value", command.getCooldown() + ""));
						}else{
							Messaging.sendError(sender, LanguageSettings.Commands_MustBeNumeric.getSetting().replace("%arg", "cooldown"));
						}
					}
					if(args.hasValueFlag("t")) {
						String value = args.getFlag("t");
						if(ClickType.hasClickType(value)) {
							command.setClickType(ClickType.getClickType(value));
							Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "ClickType")
									.replace("%value", command.getClickType().name()));
						}else{
							Messaging.sendError(sender, LanguageSettings.Commands_Citizens_ValueFlagT.getSetting());
						}
					}
					if(args.argsLength() > 2) {
						if(args.hasFlag('m')) {
							command.setCooldownMessage(args.getJoinedStrings(2));
							Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Cooldown Message")
									.replace("%value", command.getCooldownMessage()));
						}else {
							command.setCommand(args.getJoinedStrings(2));
							Messaging.send(sender, LanguageSettings.Commands_SetTo_Line.getSetting().replace("%variable", "Command")
									.replace("%value", command.getCommand()));
						}
					}
					CommandNPC.getCommandDatabase().saveDatabase();
				}else{
					Messaging.sendError(sender, LanguageSettings.Commands_Citizens_NumBetween.getSetting().replace("%num1", "1")
							.replace("%num2", data.getCommands().size() + ""));
				}
			}else{
				Messaging.sendError(sender, LanguageSettings.Commands_Citizens_NoCommands.getSetting());
			}
		}else{
			Messaging.sendError(sender, LanguageSettings.Commands_MustBeNumeric.getSetting().replace("%arg", "ID"));
		}
	}
	
	@Command(aliases = { "npc" }, usage = "cmdinfo [ID]", desc = "Displays various information about the commands of an NPC.", modifiers = { "cmdinfo" }, min = 1, max = 2, 
			permission = "commandnpc.admin")
	public void infoCmds(CommandContext args, CommandSender sender, NPC npc) {
		int id = npc.getId();
		if(CommandNPC.getCommandManager().hasNPCData(id)) {
			NPCData data = CommandNPC.getCommandManager().getNPCData(id);
			ArrayList<NPCCommand> commands;
			if(args.argsLength() == 2) {
				if(Utilities.isInteger(args.getString(1))) {
					int cmdID = args.getInteger(1);
					if(data.hasCommand(cmdID)) {
						commands = new ArrayList<>();
						commands.add(data.getCommand(cmdID));
					}else{
						Messaging.sendError(sender, LanguageSettings.Commands_Citizens_NumBetween.getSetting().replace("%num1", "1")
								.replace("%num2", data.getCommands().size() + ""));
						return;
					}
				}else{
					Messaging.sendError(sender, LanguageSettings.Commands_MustBeNumeric.getSetting().replace("%arg", "ID"));
					return;
				}
			}else{
				commands = data.getCommands();
			}
			//npc cmdset 2 -c -o -r -m -i -l --v 25 --t left --d 60 --cd 200 --p custom.permission This is a test cooldown message
			Messaging.send(sender, CommandNPC.prefix + LanguageSettings.Commands_List_InfoHeader.getSetting().replace("%id", id + ""));
			String prefix = LanguageSettings.Commands_List_InfoLinePrefix.getSetting();
			String infoLine = LanguageSettings.Commands_List_InfoLine.getSetting();
			String spacer = " &8| ";
			for(NPCCommand command : commands) {
				Messaging.send(sender, LanguageSettings.Commands_List_InfoLineHeader.getSetting().replace("%name", "Command ID")
						.replace("%value", "" + command.getID()));
				Messaging.send(sender, prefix + infoLine.replace("%name", "Command").replace("%value", command.getCommand()));
				Messaging.send(sender, prefix + infoLine.replace("%name", "Permission").replace("%value", command.getPermission()));
				Messaging.send(sender, prefix + infoLine.replace("%name", "Cooldown Message").replace("%value", command.getCooldownMessage()));
				Messaging.send(sender, prefix + infoLine.replace("%name", "Ignore Perm Message").replace("%value", command.isIgnorePermMsg() + "")
						+ spacer + infoLine.replace("%name", "Ignore Money Message").replace("%value", command.isIgnoreMoneyMsg() + ""));
				Messaging.send(sender, prefix + infoLine.replace("%name", "ClickType").replace("%value", command.getClickType().name().toLowerCase())
						+ spacer + infoLine.replace("%name", "Cost").replace("%value", command.getCost() + "")
						+ spacer + infoLine.replace("%name", "Cooldown").replace("%value", command.getCooldown() + ""));
				Messaging.send(sender, prefix + infoLine.replace("%name", "In Console").replace("%value", command.inConsole() + "")
						+ spacer + infoLine.replace("%name", "As Op").replace("%value", command.asOp() + ""));
			}
		}else{
			Messaging.sendError(sender, LanguageSettings.Commands_Citizens_NoCommands.getSetting());
		}
	}
	
	@Command(aliases = { "npc" }, usage = "cmdreset", desc = "Reset the commands on the NPC.", modifiers = { "cmdreset" }, min = 1, max = 1, permission = "commandnpc.admin")
	public void resetCmds(CommandContext args, CommandSender sender, NPC npc) {
		int id = npc.getId();
		if(CommandNPC.getCommandManager().hasNPCData(id)) {
			CommandNPC.getCommandManager().removeNPCData(id);
			CommandNPC.getCommandDatabase().deleteNPC(id);
			Messaging.send(sender, LanguageSettings.Commands_Citizens_Reset.getSetting());
		}else{
			Messaging.sendError(sender, LanguageSettings.Commands_Citizens_NoCommands.getSetting());
		}
	}
}
