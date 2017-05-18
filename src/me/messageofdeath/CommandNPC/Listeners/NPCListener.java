package me.messageofdeath.CommandNPC.Listeners;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import me.messageofdeath.CommandNPC.CommandNPC;
import me.messageofdeath.CommandNPC.Database.ClickType;
import me.messageofdeath.CommandNPC.Database.LanguageSettings.LanguageSettings;
import me.messageofdeath.CommandNPC.Database.PluginSettings.PluginSettings;
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

	private ArrayList<UUID> coolingDown = new ArrayList<UUID>();
	private long delay = PluginSettings.CoolDown.getInteger();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNPCDelete(NPCRemoveEvent event) {
		if (CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId())) {
			CommandNPC.getCommandManager().removeNPCData(event.getNPC().getId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRight(NPCRightClickEvent event) {
		if (coolingDown.contains(event.getClicker().getUniqueId()) && CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId())) {
			if(PluginSettings.CooldownMessage.getBoolean()) {
				Messaging.send(event.getClicker(), LanguageSettings.CmdNPC_Cooldown.getSetting());
			}
			return;
		}
		this.onClick(event.getClicker(), event.getNPC(), ClickType.RIGHT);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeft(NPCLeftClickEvent event) {
		if (coolingDown.contains(event.getClicker().getUniqueId()) && CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId())) {
			if(PluginSettings.CooldownMessage.getBoolean()) {
				Messaging.send(event.getClicker(), LanguageSettings.CmdNPC_Cooldown.getSetting());
			}
			return;
		}
		this.onClick(event.getClicker(), event.getNPC(), ClickType.LEFT);
	}

	private void onClick(final Player player, NPC npc, ClickType clickType) {
		if (CommandNPC.getCommandManager().hasNPCData(npc.getId())) {
			coolingDown.add(player.getUniqueId());
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), new Runnable() {
				@Override
				public void run() {
					coolingDown.remove(player.getUniqueId());
				}
			}, this.delay);
			NPCData data = CommandNPC.getCommandManager().getNPCData(npc.getId());
			boolean isOp = player.isOp();
			ArrayList<NPCCommand> commands = new ArrayList<NPCCommand>();
			if(data.isRandom()) {
				commands.add(data.getCommands().get(new Random().nextInt(data.getCommands().size())));
			}else{
				commands = data.getCommands();
			}
			for(NPCCommand command : commands) {
				if(command.getClickType() == clickType || command.getClickType() == ClickType.BOTH) {
					if(player.hasPermission(command.getPermission()) || command.getPermission().equalsIgnoreCase("noPerm") || command.getPermission().isEmpty()) {
						//------------ Economy ------------
						if(command.getCost() > 0 && CommandNPC.isEconAvailable()) {
							if(CommandNPC.getEcon().has(player, command.getCost())) {
								CommandNPC.getEcon().withdrawPlayer(player, command.getCost());
							}else{
								Messaging.sendError(player, LanguageSettings.CmdNPC_NoMoney.getSetting());
								return;
							}
						}
						//------------ BungeeCord ------------
						if(command.getCommand().toLowerCase().startsWith("server")) {
							if(PluginSettings.BungeeCord.getBoolean()) {
								String[] args = command.getCommand().split(" ");
								if(args.length == 2) {
									if(command.getDelay() > 0) {
										scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), new Runnable() {
											@Override
											public void run() {
												BungeeCordUtil.sendPlayerToServer(player, args[1]);
												CommandNPC.getInstance().log("Sent '"+player.getName()+"' to server '"+args[1]+"'!", true);
											}
										}, command.getDelay());
									}else{
										BungeeCordUtil.sendPlayerToServer(player, args[1]);
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
											if(PluginSettings.ExecuteCommandMessage.getBoolean()) {
												Messaging.send(player, LanguageSettings.CmdNPC_Executed.getSetting());
											}
										}
									}, command.getDelay());
								}else{
									player.chat("/" + command.getCommand().replace("%name", player.getName()));
									if(PluginSettings.ExecuteCommandMessage.getBoolean()) {
										Messaging.send(player, LanguageSettings.CmdNPC_Executed.getSetting());
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
										if(PluginSettings.ExecuteCommandMessage.getBoolean()) {
											Messaging.send(player, LanguageSettings.CmdNPC_Executed.getSetting());
										}
									}
								}, command.getDelay());
							}else{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%name", player.getName()));
								if(PluginSettings.ExecuteCommandMessage.getBoolean()) {
									Messaging.send(player, LanguageSettings.CmdNPC_Executed.getSetting());
								}
							}
						}
					}else{
						Messaging.sendError(player, LanguageSettings.Commands_NoPermission.getSetting());
					}
				}//Wrong clickType (Do nothing)
			}
		}
	}
}
