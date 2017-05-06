package me.messageofdeath.CommandNPC.Listeners;

import java.util.ArrayList;

import me.messageofdeath.CommandNPC.CommandNPC;
import me.messageofdeath.CommandNPC.Database.ClickType;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCCommand;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCData;
import me.messageofdeath.CommandNPC.Utilities.BungeeCord.BungeeCordUtil;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.util.Messaging;
/*import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;*/

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
						//------------ Economy ------------
						if(command.getCost() > 0 && CommandNPC.isEconAvailable()) {
							if(CommandNPC.getEcon().has(player, command.getCost())) {
								CommandNPC.getEcon().withdrawPlayer(player, command.getCost());
							}else{
								Messaging.sendError(player, "You do not have enough money for this!");
								return;
							}
						}
						//------------ BungeeCord ------------
						if(command.getCommand().toLowerCase().startsWith("server")) {
							if(CommandNPC.getConfigX().isBungeeCord()) {
								String[] args = command.getCommand().split(" ");
								if(args.length == 2) {
									BungeeCordUtil.sendPlayerToServer(player, args[1]);
									if(command.getDelay() > 0) {
										scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), new Runnable() {
											@Override
											public void run() {
												CommandNPC.getInstance().log("Sent '"+player.getName()+"' to server '"+args[1]+"'!", true);
											}
										}, command.getDelay());
									}else{
										CommandNPC.getInstance().log("Sent '"+player.getName()+"' to server '"+args[1]+"'!", true);
									}
									return;
								}else{
									Messaging.sendError(player, "Command can only have 1 argument. /server <server>");
									return;
								}
							}else{
								Messaging.sendError(player, "Command is a /server command, but BungeeCord is disabled in config.yml!");
								return;
							}
						}
						//------------ Execute Command ------------
						if(!command.inConsole()) {
							try{
								if(!isOp && command.asOp()) {
									player.setOp(true);
								}
								if(command.getDelay() > 0) {
									scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), new Runnable() {
										@Override
										public void run() {
											player.chat("/" + command.getCommand().replace("%name", player.getName()));
											if(CommandNPC.getConfigX().isExecuteCommandMessage()) {
												Messaging.send(player, "Command Executed.");
											}
										}
									}, command.getDelay());
								}else{
									player.chat("/" + command.getCommand().replace("%name", player.getName()));
									if(CommandNPC.getConfigX().isExecuteCommandMessage()) {
										Messaging.send(player, "Command Executed.");
									}
								}
							}finally{
								player.setOp(isOp);
							}
						}else{
							if(command.getDelay() > 0) {
								scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), new Runnable() {
									@Override
									public void run() {
										Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%name", player.getName()));
										if(CommandNPC.getConfigX().isExecuteCommandMessage()) {
											Messaging.send(player, "Command Executed.");
										}
									}
								}, command.getDelay());
							}else{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%name", player.getName()));
								if(CommandNPC.getConfigX().isExecuteCommandMessage()) {
									Messaging.send(player, "Command Executed.");
								}
							}
						}
					}else{
						Messaging.sendError(player, "You do not have enough money for this!");
					}
				}//Wrong clickType (Do nothing)
			}
		}
	}
}
