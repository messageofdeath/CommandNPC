package me.messageofdeath.commandnpc.Listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import me.clip.placeholderapi.PlaceholderAPI;
import me.messageofdeath.commandnpc.CommandNPC;
import me.messageofdeath.commandnpc.Database.ClickType;
import me.messageofdeath.commandnpc.Database.LanguageSettings.LanguageSettings;
import me.messageofdeath.commandnpc.Database.PluginSettings.PluginSettings;
import me.messageofdeath.commandnpc.NPCDataManager.NPCCommand;
import me.messageofdeath.commandnpc.NPCDataManager.NPCData;
import me.messageofdeath.commandnpc.Utilities.BungeeCord.BungeeCordUtil;
import me.messageofdeath.commandnpc.Utilities.CooldownManager.Cooldown;
import me.messageofdeath.commandnpc.Utilities.CooldownManager.CooldownManager;
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

	private final ArrayList<UUID> coolingDown = new ArrayList<>();//To prevent server overloads
	private final long delay = PluginSettings.CoolDown.getInteger();
	private final CooldownManager cooldown;

	public NPCListener() {
		cooldown = new CooldownManager();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onNPCDelete(NPCRemoveEvent event) {
		if (CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId())) {
			CommandNPC.getCommandManager().removeNPCData(event.getNPC().getId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRight(NPCRightClickEvent event) {
		if (CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId()) && coolingDown.contains(event.getClicker().getUniqueId())) {
			if(PluginSettings.CooldownMessage.getBoolean()) {
				Messaging.send(event.getClicker(), LanguageSettings.CmdNPC_Cooldown.getSetting());
			}
			return;
		}
		this.onClick(event.getClicker(), event.getNPC(), ClickType.RIGHT);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeft(NPCLeftClickEvent event) {
		if (CommandNPC.getCommandManager().hasNPCData(event.getNPC().getId()) && coolingDown.contains(event.getClicker().getUniqueId())) {
			if(PluginSettings.CooldownMessage.getBoolean()) {
				Messaging.send(event.getClicker(), LanguageSettings.CmdNPC_Cooldown.getSetting());
			}
			return;
		}
		this.onClick(event.getClicker(), event.getNPC(), ClickType.LEFT);
	}

	private void onClick(final Player player, NPC npc, ClickType clickType) {
		if (CommandNPC.getCommandManager().hasNPCData(npc.getId())) {
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			if(delay > 0) {
				coolingDown.add(player.getUniqueId());
				scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), () -> coolingDown.remove(player.getUniqueId()), this.delay);
			}
			NPCData data = CommandNPC.getCommandManager().getNPCData(npc.getId());
			boolean isOp = player.isOp();
			ArrayList<NPCCommand> commands = new ArrayList<>();
			if(data.isRandom()) {
				commands.add(data.getCommands().get(new Random().nextInt(data.getCommands().size())));
			}else{
				commands = data.getCommands();
			}
			for(NPCCommand command : commands) {
				if(command.getClickType() == clickType || command.getClickType() == ClickType.BOTH) {
					if(command.getPermission().isEmpty() || player.hasPermission(command.getPermission()) || command.getPermission().equalsIgnoreCase("noPerm")) {
						//------------ Cooldown ------------
						if(cooldown.hasCooldown(player.getUniqueId())) {
							if(Arrays.stream(cooldown.getCooldowns(player.getUniqueId())).anyMatch(search ->
									search.getNpcID() == npc.getId() && search.getCommandID() == command.getID())) {
								if (command.getCooldownMessage() != null && !command.getCooldownMessage().isEmpty()) {
									Messaging.sendError(player, command.getCooldownMessage());
								}
								continue;
							}
						}
						//------------ Economy ------------
						if(command.getCost() > 0 && CommandNPC.isEconAvailable()) {
							if(CommandNPC.getEcon().has(player, command.getCost())) {
								CommandNPC.getEcon().withdrawPlayer(player, command.getCost());
							}else{
								if(!command.isIgnoreMoneyMsg()) {
									Messaging.sendError(player, LanguageSettings.CmdNPC_NoMoney.getSetting());
								}
								continue;
							}
						}
						//------------ BungeeCord ------------
						if(command.getCommand().toLowerCase().startsWith("server ")) {
							if(PluginSettings.BungeeCord.getBoolean()) {
								String[] args = command.getCommand().split(" ");
								if(args.length == 2) {
									if(command.getDelay() > 0) {
										scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), () -> {
                                            BungeeCordUtil.sendPlayerToServer(player, args[1]);
                                            CommandNPC.getInstance().log("Sent '"+player.getName()+"' to server '"+args[1]+"'!", true);
                                        }, command.getDelay());
									}else{
										BungeeCordUtil.sendPlayerToServer(player, args[1]);
										CommandNPC.getInstance().log("Sent '"+player.getName()+"' to server '"+args[1]+"'!", true);
									}
									executeCooldown(player.getUniqueId(), npc.getId(), command.getID(), command.getCooldown());
									continue;
								}else{
									Messaging.sendError(player, "Inform the system administrator to look in console for error.");
									CommandNPC.getInstance().logError("BungeeCord Command", "NPCListener", "onClick(Player, NPC, ClickType)", "/server command for NPC " +
											"ID: " + npc.getId() + ", Command ID: " + command.getID() + ", does not follow the format of /server <serverName>");
									continue;
								}
							}else{
								Messaging.sendError(player, "Inform the system administrator to look in console for error.");
								CommandNPC.getInstance().logError("BungeeCord Command", "NPCListener", "onClick(Player, NPC, ClickType)", "BungeeCord is " +
										"disabled in config.yml, yet an NPC has the command /server registered to it.");
								continue;
							}
						}
						//------------ Execute Command ------------
						if(!command.inConsole()) {
							try{
								if(command.asOp() && !isOp) {
									player.setOp(true);
								}
								if(command.getDelay() > 0) {
									scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), () -> {
                                        player.chat("/" + process(player, command.getCommand()));
                                        if(PluginSettings.ExecuteCommandMessage.getBoolean()) {
                                            Messaging.send(player, LanguageSettings.CmdNPC_Executed.getSetting());
                                        }
                                    }, command.getDelay());
								}else{
									player.chat("/" + process(player, command.getCommand()));
									if(PluginSettings.ExecuteCommandMessage.getBoolean()) {
										Messaging.send(player, LanguageSettings.CmdNPC_Executed.getSetting());
									}
								}
							}finally{
								player.setOp(isOp);
							}
						}else{
							if(command.getDelay() > 0) {
								scheduler.scheduleSyncDelayedTask(CommandNPC.getInstance(), () -> {
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), process(player, command.getCommand()));
                                    if(PluginSettings.ExecuteCommandMessage.getBoolean()) {
                                        Messaging.send(player, LanguageSettings.CmdNPC_Executed.getSetting());
                                    }
                                }, command.getDelay());
							}else{
								Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), process(player, command.getCommand()));
								if(PluginSettings.ExecuteCommandMessage.getBoolean()) {
									Messaging.send(player, LanguageSettings.CmdNPC_Executed.getSetting());
								}
							}
						}
						//------------ Cooldown ------------
						executeCooldown(player.getUniqueId(), npc.getId(), command.getID(), command.getCooldown());
					}else{
						if(!command.isIgnorePermMsg()) {
							Messaging.sendError(player, LanguageSettings.Commands_NoPermission.getSetting());
						}
					}
				}//Wrong clickType (Do nothing)
			}
		}
	}

	private String process(Player player, String cmd) {
		return CommandNPC.hasPlaceHolderAPI() ? PlaceholderAPI.setPlaceholders(player, cmd.replace("%name", player.getName()))
				: cmd.replace("%name", player.getName());
	}

	private void executeCooldown(UUID uuid, int npcID, int commandID, int cd) {
		if(cd > 0) {
			cooldown.addCooldown(new Cooldown(uuid, npcID, commandID));
			Bukkit.getScheduler().scheduleSyncDelayedTask(CommandNPC.getInstance(), () -> cooldown.removeCooldown(uuid), cd);
		}
	}
}
