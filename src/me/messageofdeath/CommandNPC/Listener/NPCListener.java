package me.messageofdeath.CommandNPC.Listener;

import me.messageofdeath.CommandNPC.CommandNPC;
import me.messageofdeath.CommandNPC.Database.ClickType;
import me.messageofdeath.CommandNPC.Utils.NPCDataManager.NPCCommand;
import me.messageofdeath.CommandNPC.Utils.NPCDataManager.NPCData;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNPCDelete(NPCRemoveEvent event) {
		if(CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId())) {
			CommandNPC.getCommandManager().removeNPCData(event.getNPC().getId());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRight(NPCRightClickEvent event) {
		if(CommandNPC.getConfigX().getClickType() == ClickType.Right || CommandNPC.getConfigX().getClickType() == ClickType.Both) {
			onClick(event.getClicker(), event.getNPC());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeft(NPCLeftClickEvent event) {
        if(CommandNPC.getConfigX().getClickType() == ClickType.Left || CommandNPC.getConfigX().getClickType() == ClickType.Both) {
			onClick(event.getClicker(), event.getNPC());
		}
	}
	
	private void onClick(final Player player, NPC npc) {
		if(CommandNPC.getCommandManager().hasNPCData(npc.getId())) {
			NPCData data = CommandNPC.getCommandManager().getNPCData(npc.getId());
			for(NPCCommand command : data.getCommands()) {
				if(player.hasPermission(command.getPermission()) || command.getPermission().equalsIgnoreCase("noPerm") || command.getPermission().isEmpty() || command.getPermission().equalsIgnoreCase("")) {
					if(command.inConsole()) {
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%name", player.getName()));
					}else{
						if(!player.isOp()) {
							try {
								player.setOp(true);
								player.performCommand(command.getCommand().replace("%name", player.getName()));
                       					} catch(Exception e) {
                       						e.printStackTrace();
                       					} finally {
                       						player.setOp(false);
                       					}
                		 		} else {
							player.performCommand(command.getCommand().replace("%name", player.getName()));
                   				}
					}
				}
			}
		}
	}
}
