package me.messageofdeath.CommandNPC.Listeners;

import java.util.ArrayList;

import me.messageofdeath.CommandNPC.CommandNPC;
import me.messageofdeath.CommandNPC.Database.ClickType;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCCommand;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCData;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

public class NPCListener implements Listener {

	private ArrayList<Player> coolingDown = new ArrayList<Player>();
	private long delay = CommandNPC.getConfigX().getCoolDown();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNPCDelete(NPCRemoveEvent event) {
		if (CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId())) {
			CommandNPC.getCommandManager().removeNPCData(event.getNPC().getId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRight(NPCRightClickEvent event) {
		if (coolingDown.contains(event.getClicker()) && CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId())) {
			Messaging.send(event.getClicker(), "You cannot do this yet!");
			return;
		}
		this.onClick(event.getClicker(), event.getNPC(), ClickType.RIGHT);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeft(NPCLeftClickEvent event) {
		if (coolingDown.contains(event.getClicker()) && CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId())) {
			Messaging.send(event.getClicker(), "You cannot do this yet!");
			return;
		}
		this.onClick(event.getClicker(), event.getNPC(), ClickType.LEFT);
	}

	private void onClick(final Player player, NPC npc, ClickType clickType) {
		if (CommandNPC.getCommandManager().hasNPCData(npc.getId())) {
			coolingDown.add(player);
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), new Runnable() {
				@Override
				public void run() {
					coolingDown.remove(player);
				}
			}, delay);
			NPCData data = CommandNPC.getCommandManager().getNPCData(npc.getId());
			boolean isOp = player.isOp();
			for(NPCCommand command : data.getCommands()) {
				if(command.getClickType() == clickType || command.getClickType() == ClickType.BOTH) {
					if(player.hasPermission(command.getPermission()) || command.getPermission().equalsIgnoreCase("noPerm") || command.getPermission().isEmpty()) {
						boolean hadEnough = true;
						if(command.getCost() > 0 && CommandNPC.isEconAvailable()) {
							if(CommandNPC.getEcon().has(player, command.getCost())) {
								CommandNPC.getEcon().withdrawPlayer(player, command.getCost());
							}else{
								hadEnough = false;
							}
						}
						if(hadEnough) {
							if(!command.inConsole()) {
								try{
									if(!isOp && command.asOp()) {
										player.setOp(true);
									}
									player.chat("/" + command.getCommand().replace("%name", player.getName()));
									Messaging.send(player, "Command Executed.");
								}finally{
									player.setOp(isOp);
								}
							}else{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%name", player.getName()));
								Messaging.send(player, "Command Executed.");
							}
						}else{
							Messaging.sendError(player, "You do not have enough money for this!");
						}
					}else{
						Messaging.sendError(player, "You do not have permission for this!");
					}
				}//Wrong clickType (Do nothing)
			}
		}
	}
}
