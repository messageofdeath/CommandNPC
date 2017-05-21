package me.razorrider7.commandnpc.commands;

import org.bukkit.command.CommandSender;

import me.razorrider7.commandnpc.CommandNPC;
import net.citizensnpcs.api.command.Command;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.command.Requirements;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;

@Requirements(selected = true, ownership = true)
public class ResetCommand {

	@Command(aliases = { "npc" }, usage = "resetcmds", desc = "Reset the commands on the NPC.", modifiers = { "resetcmds" }, min = 1, max = 1)
	public void resetCmds(CommandContext args, CommandSender sender, NPC npc) {
		if(sender.hasPermission("commandnpc.admin") || sender.isOp()) {
			int id = npc.getId();
			if(CommandNPC.getCommandManager().hasNPCData(id)) {
				CommandNPC.getCommandManager().removeNPCData(id);
				CommandNPC.getCommandDatabase().deleteNPC(id);
				Messaging.send(sender, "You have successfully resetted the commands for this NPC!");
			}else{
				Messaging.sendError(sender, "There are no set commands at the moment for this NPC!");
			}
		}else{
			Messaging.send(sender, "You don't have permission to execute that command!");		}
	}
}
