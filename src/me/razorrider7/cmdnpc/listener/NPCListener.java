package me.razorrider7.cmdnpc.listener;

import java.util.ArrayList;

import me.razorrider7.cmdnpc.CommandNPC;
import me.razorrider7.cmdnpc.database.ClickType;
import me.razorrider7.cmdnpc.util.npcdatamanager.NPCCommand;
import me.razorrider7.cmdnpc.util.npcdatamanager.NPCData;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;
import net.milkbowl.vault.economy.EconomyResponse;

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
			Messaging.send(event.getClicker(), "You cannot do this yet! RIGHT CLICK");
			return;
		}
		if (CommandNPC.getConfigX().getClickType() == ClickType.RIGHT || CommandNPC.getConfigX().getClickType() == ClickType.BOTH) {
			this.onClick(event.getClicker(), event.getNPC());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeft(NPCLeftClickEvent event) {
		if (coolingDown.contains(event.getClicker()) && CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId())) {
			Messaging.send(event.getClicker(), "You cannot do this yet! LEFT CLICK");
			return;
		}
		if (CommandNPC.getConfigX().getClickType() == ClickType.LEFT || CommandNPC.getConfigX().getClickType() == ClickType.BOTH) {
			this.onClick(event.getClicker(), event.getNPC());
		}
	}

	private void onClick(final Player player, NPC npc) {
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
			for (NPCCommand command : data.getCommands()) {
				if (player.hasPermission(command.getPermission()) || command.getPermission().equalsIgnoreCase("noPerm") || command.getPermission().isEmpty() || command.getPermission().equalsIgnoreCase("")) {
					if (command.inConsole()) {
						if (command.getCost() > 0) {
							EconomyResponse er = CommandNPC.getEcon().withdrawPlayer(player, command.getCost());
							if (er.transactionSuccess()) {
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%name", player.getName()));
							}
						} else {
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%name", player.getName()));
						}
					} else {
						try {
							if (!player.isOp()) {
								player.setOp(command.asOp());
							}
							if (command.getCost() > 0) {
								EconomyResponse er = CommandNPC.getEcon().withdrawPlayer(player, command.getCost());
								if (er.transactionSuccess()) {
									player.chat("/" + command.getCommand().replace("%name", player.getName()));
								}
							} else {
								player.chat("/" + command.getCommand().replace("%name", player.getName()));
							}
						} finally {
							player.setOp(isOp);
						}
					}
				}
			}
		}
	}
}
