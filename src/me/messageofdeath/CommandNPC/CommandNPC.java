package me.messageofdeath.CommandNPC;

import java.util.ArrayList;

import me.messageofdeath.CommandNPC.Database.CommandDatabase;
import me.messageofdeath.CommandNPC.Database.Config;
import me.messageofdeath.CommandNPC.Listeners.NPCListener;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCDataManager;
import me.messageofdeath.CommandNPC.Utilities.BungeeCord.BungeeCordUtil;
import me.messageofdeath.CommandNPC.Utilities.CitizenBackend.CitizenCommandRegister;
import me.messageofdeath.CommandNPC.commands.CitizenCommands;
import me.messageofdeath.CommandNPC.commands.ReloadCommand;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandNPC extends JavaPlugin {

	public static final String prefix = CommandNPC.getColorized("&8[&2CommandNPC&8] &6");

	private CitizenCommandRegister commandRegister;

	private static CommandDatabase database;

	private static JavaPlugin instance;

	private static Config config;

	private static NPCDataManager manager;

	private static Economy econ = null;
	
	private static boolean econAvailable = false;

	@Override
	public void onEnable() {
		/** --------------Checking for Dependencies-------------- **/
		if (!getServer().getPluginManager().isPluginEnabled("Citizens")) {
			logError("Required Dependencies", "CommandNPC", "onEnable()", "Citizens 2 not found! CommandNPC will now shut down.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupEconomy();
				
		/** --------------Initiation of Databases, Managers, and Parsers-------------- **/
		instance = this;
		manager = new NPCDataManager();
		config = new Config(this);
		commandRegister = new CitizenCommandRegister(this);
		/** --------------Initiation of the Listener-------------- **/
		getServer().getPluginManager().registerEvents(new NPCListener(), this);
		/** --------------Initiation and Loading of Databases-------------- **/
		log("Initiating Database", true);
		database = new CommandDatabase(this);
		database.initDatabase();
		database.loadDatabase();
		/** --------------Registering Commands-------------- **/
		log("Injecting command info into Citizens.", true);
		commandRegister.registerCitizenCommand(CitizenCommands.class);
		getCommand("commandnpc").setExecutor(new ReloadCommand());
		if(config.isBungeeCord()) {
			log("Setting up BungeeCord", true);
			BungeeCordUtil.setupUtil();
		}
		log("CommandNPC successfully loaded!", true);
	}

	@Override
	public void onDisable() {
		if (database != null) {
			database.saveDatabase();
		}
	}

	public static Economy getEcon() {
		return econ;
	}

	public String getPrefix() {
		return CommandNPC.prefix;
	}

	public static String getColorized(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public void log(String log, boolean prefix) {
		getServer().getConsoleSender().sendMessage(CommandNPC.getColorized((prefix ? CommandNPC.prefix : "") + log));
	}

	public static NPCDataManager getCommandManager() {
		return manager;
	}

	public static CommandDatabase getCommandDatabase() {
		return database;
	}

	public static Config getConfigX() {
		return config;
	}

	public static CommandNPC getInstance() {
		return (CommandNPC)instance;
	}
	
	public static boolean isEconAvailable() {
		return econAvailable;
	}

	public void logError(String topic, String classx, String method, String error) {
		final String space = "                                                             ";
		String text = "&cTopic";
		topic = "&c" + topic;
		log("&4---------------------&b{&2CommandNPC &cError&b}&4---------------------", false);
		log(space.substring((space.length() + text.length()) / 2, space.length()) + text, false);
		log(space.substring((space.length() + topic.length()) / 2, space.length()) + topic, false);
		log("", false);
		for (String s : getLines(error, space)) {
			log("&b" + space.substring((space.length() + s.length()) / 2, space.length()) + s, false);
		}
		log("", false);
		String cl = "&8Class: &c" + classx + "   &8Method: &c" + method;
		for (String s : getLines(cl, space)) {
			log("&c" + space.substring((space.length() + s.length()) / 2, space.length()) + s, false);
		}
		log("", false);
		log("&4---------------------&b{&2CommandNPC &cError&b}&4---------------------", false);
	}

	private ArrayList<String> getLines(String parse, final String space) {
		ArrayList<String> lines = new ArrayList<String>();
		String s = "";
		String[] split = parse.split(" ");
		final int length = split.length;
		for (int i = 0; i < length; i++) {
			if (s.length() + split[i].length() < space.length()) {
				s += split[i] + " ";
			} else {
				lines.add(s);
				s = split[i] + " ";
			}
			if (i + 1 == length) {
				lines.add(s);
			}
		}
		return lines;
	}

	private void setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			log("Vault not found! Economy support for CommandNPC has been disabled.", true);
			return;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			log("Vault compatible economy not found! Economy support for CommandNPC has been disabled.", true);
			return;
		}
		econ = rsp.getProvider();
		if (econ != null) {
			log("Vault compatible economy found! Economy support for CommandNPC has been enabled.", true);
			econAvailable = true;
		}
	}
}
