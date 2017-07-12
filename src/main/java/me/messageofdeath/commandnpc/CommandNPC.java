package me.messageofdeath.commandnpc;

import java.util.ArrayList;

import me.messageofdeath.commandnpc.Database.CommandDatabase;
import me.messageofdeath.commandnpc.Database.LanguageSettings.LanguageConfiguration;
import me.messageofdeath.commandnpc.Database.LanguageSettings.LanguageSettings;
import me.messageofdeath.commandnpc.Database.PluginSettings.PluginConfiguration;
import me.messageofdeath.commandnpc.Database.PluginSettings.PluginSettings;
import me.messageofdeath.commandnpc.Listeners.NPCListener;
import me.messageofdeath.commandnpc.NPCDataManager.NPCDataManager;
import me.messageofdeath.commandnpc.Utilities.BungeeCord.BungeeCordUtil;
import me.messageofdeath.commandnpc.Utilities.CitizenBackend.CitizenCommandRegister;
import me.messageofdeath.commandnpc.Utilities.Metrics.Metrics;
import me.messageofdeath.commandnpc.Utilities.queue.QueueSystem;
import me.messageofdeath.commandnpc.commands.CitizenCommands;
import me.messageofdeath.commandnpc.commands.ReloadCommand;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandNPC extends JavaPlugin {

	public static String prefix;

	private static CommandDatabase database;

	private static JavaPlugin instance;

	private static PluginConfiguration config;

	private static NPCDataManager manager;

	private static Economy econ = null;

	private static QueueSystem queueSystem;

	private static boolean placeHolderAPI = false;
	
	private static boolean econAvailable = false;

	@Override
	public void onEnable() {
		CommandNPC.queueSystem = new QueueSystem(1);
		new Metrics(this);
		LanguageConfiguration langConfig = new LanguageConfiguration(this);
		langConfig.initConfiguration();
		langConfig.loadConfiguration();
		this.reloadConfigX();
		CommandNPC.prefix = CommandNPC.getColorized(LanguageSettings.General_PluginTag.getSetting());
		/** --------------Checking for Dependencies-------------- **/
		if (!getServer().getPluginManager().isPluginEnabled("Citizens")) {
			this.logError("Required Dependencies", "commandnpc", "onEnable()", "Citizens 2 not found! commandnpc will now shut down.");
			super.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			placeHolderAPI = true;
		}
		this.setupEconomy();
		
		/** --------------Initiation of Databases, Managers, and Parsers-------------- **/
		CommandNPC.instance = this;
		CommandNPC.manager = new NPCDataManager();
		CitizenCommandRegister commandRegister = new CitizenCommandRegister(this);
		/** --------------Initiation of the Listener-------------- **/
		super.getServer().getPluginManager().registerEvents(new NPCListener(), this);
		/** --------------Initiation and Loading of Databases-------------- **/
		this.log("Initiating Database", true);
		CommandNPC.database = new CommandDatabase(this);
		CommandNPC.database.initDatabase();
		CommandNPC.database.loadDatabase();
		/** --------------Registering Commands-------------- **/
		this.log("Injecting command info into Citizens.", true);
		commandRegister.registerCitizenCommand(CitizenCommands.class);
		super.getCommand("commandnpc").setExecutor(new ReloadCommand());
		if(PluginSettings.BungeeCord.getBoolean()) {
			this.log("Setting up BungeeCord", true);
			BungeeCordUtil.setupUtil();
		}
		this.log("CommandNPC successfully loaded!", true);
	}

	@Override
	public void onDisable() {
		if (database != null) {
			database.saveDatabase();
		}
		queueSystem.stop();
		if(PluginSettings.BungeeCord.getBoolean()) {
			this.log("Disabling BungeeCord Support", true);
			BungeeCordUtil.disableUtil();
		}
	}

	public static Economy getEcon() {
		return econ;
	}

	private static String getColorized(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public void log(String log, boolean prefix) {
		getServer().getConsoleSender().sendMessage(CommandNPC.getColorized((prefix ? CommandNPC.prefix : "") + log));
	}

	public static NPCDataManager getCommandManager() {
		return manager;
	}

	public static QueueSystem getQueueSystem() {
		return queueSystem;
	}

	public static CommandDatabase getCommandDatabase() {
		return database;
	}
	
	public static PluginConfiguration getConfigX() {
		return config;
	}

	public static boolean hasPlaceHolderAPI() {
		return placeHolderAPI;
	}
	
	public void reloadConfigX() {
		config = new PluginConfiguration(this);
		config.initConfiguration();
		config.loadConfiguration();
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
		ArrayList<String> lines = new ArrayList<>();
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
