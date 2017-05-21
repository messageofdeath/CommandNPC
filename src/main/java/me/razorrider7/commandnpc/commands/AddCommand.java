package me.razorrider7.commandnpc.commands;

import org.bukkit.command.CommandSender;

import me.razorrider7.commandnpc.CommandNPC;
import me.razorrider7.commandnpc.Utils.npcdatamanager.NPCCommand;
import me.razorrider7.commandnpc.Utils.npcdatamanager.NPCData;
import net.citizensnpcs.api.command.Command;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.command.Requirements;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;

@Requirements(selected = true, ownership = true)
public class AddCommand {

	@Command(aliases = { "npc" }, usage = "addcmd <in console(true/false)> <customPerm(noPerm)> <command>", desc = "Add command on a NPC", 
			modifiers = { "addcmd" }, min = 4)
	public void addCmd(CommandContext args, CommandSender sender, NPC npc) {
		if(sender.hasPermission("commandnpc.admin") || sender.isOp()) {
			int id = npc.getId();
			String inConsoleString = args.getString(1);
			String permission = args.getString(2);
			boolean inConsole = false;
			if(inConsoleString.equalsIgnoreCase("yes") || inConsoleString.equalsIgnoreCase("true") || inConsoleString.equalsIgnoreCase("console")) {
				inConsole = true;
			}
			if(permission.equalsIgnoreCase("noPerm")) {
				permission = "";
			}
			String cmd = args.getJoinedStrings(3);
			if(CommandNPC.getCommandManager().hasNPCData(id)) {
				CommandNPC.getCommandManager().getNPCData(id).addCommand(new NPCCommand(cmd, permission, inConsole));
			}else{
				NPCData data = new NPCData(id, new NPCCommand(cmd, permission, inConsole));
				CommandNPC.getCommandManager().addNPCData(data);
			}
			CommandNPC.getCommandDatabase().saveDatabase();
			Messaging.send(sender, "You have successfully added the command to the npc!");
		}else{
			Messaging.send(sender, "You don't have permission to execute that command!");
		}
	}
}
