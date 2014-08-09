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
		if(CommandNPC.getConfigX().getClickType() != ClickType.Left) {
			this.onClick(event.getClicker(), event.getNPC());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeft(NPCLeftClickEvent event) {
		if(CommandNPC.getConfigX().getClickType() != ClickType.Right) {
			this.onClick(event.getClicker(), event.getNPC());
		}
	}
	
	private void onClick(final Player player, NPC npc) {
		if(CommandNPC.getCommandManager().hasNPCData(npc.getId())) {
			NPCData data = CommandNPC.getCommandManager().getNPCData(npc.getId());
			boolean isOp = player.isOp();
			for(NPCCommand command : data.getCommands()) {
				if(player.hasPermission(command.getPermission()) || command.getPermission().equalsIgnoreCase("noPerm")
						|| command.getPermission().isEmpty() || command.getPermission().equalsIgnoreCase("")) {
					player.setOp(true);
					if(command.inConsole()) {
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%name", player.getName()));
					}else{
						player.chat("/" + command.getCommand().replace("%name", player.getName()));
					}
					if(!isOp) {
						player.setOp(false);
					}
				}
			}
			if(!isOp) {
				player.setOp(false);
				if(Bukkit.getPluginManager().isPluginEnabled(CommandNPC.getInstance())) {
					Bukkit.getScheduler().runTaskLater(CommandNPC.getInstance(), new Runnable() {
						@Override
						public void run() {
							Bukkit.getServer().getPlayer(player.getUniqueId()).setOp(false);
						}
					}, 5L);
				}
			}
		}
	}
}
