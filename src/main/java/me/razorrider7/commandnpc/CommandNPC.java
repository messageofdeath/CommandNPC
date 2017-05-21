package me.razorrider7.commandnpc;

import java.util.ArrayList;

import me.razorrider7.commandnpc.Utils.citizenbackend.CitizenCommandRegister;
import me.razorrider7.commandnpc.Utils.npcdatamanager.NPCDataManager;
import me.razorrider7.commandnpc.commands.AddCommand;
import me.razorrider7.commandnpc.commands.ResetCommand;
import me.razorrider7.commandnpc.database.CommandDatabase;
import me.razorrider7.commandnpc.database.Config;
import me.razorrider7.commandnpc.listener.NPCListener;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandNPC extends JavaPlugin {

	private final String prefix = this.getColorized("&8[&6CommandNPC&8] &6");
	
	private CitizenCommandRegister commandRegister;
	
	private static CommandDatabase database;
	
	private static JavaPlugin instance;
	
	private static Config config;
	
	private static NPCDataManager manager;
	
	@Override
	public void onEnable() {
		/**--------------Checking for Dependency's--------------**/
		if(!super.getServer().getPluginManager().isPluginEnabled("Citizens")) {
			this.logError("Required Dependencies", "CommandNPC", "onEnable()", "Citizens.jar is not installed on the server! Shutting down.");
			super.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getServer().getPluginManager().registerEvents(new NPCListener(), this);
		/**--------------Initiation of Databases, Managers, and Parser's--------------**/
		CommandNPC.instance = this;
		CommandNPC.manager = new NPCDataManager();
		CommandNPC.config = new Config(this);
		this.commandRegister = new CitizenCommandRegister(this);
		/**--------------Initiation and Loading of Databases--------------**/
		CommandNPC.database = new CommandDatabase(this);
		CommandNPC.database.initDatabase();
		CommandNPC.database.loadDatabase();
		/**--------------Registering Commands--------------**/
		this.log("Injecting command info into Citizens.", false);
		this.commandRegister.registerCitizenCommand(AddCommand.class);
		this.commandRegister.registerCitizenCommand(ResetCommand.class);
	}
	
	@Override
	public void onDisable() {
		if(CommandNPC.database != null) {
			CommandNPC.database.saveDatabase();
		}
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public String getColorized(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
	
	public void log(String log, boolean prefix) {
		super.getServer().getConsoleSender().sendMessage(this.getColorized((prefix ? this.prefix : "") + log));
	}
	
	public static NPCDataManager getCommandManager() {
		return CommandNPC.manager;
	}
	
	public static CommandDatabase getCommandDatabase() {
		return CommandNPC.database;
	}
	
	public static Config getConfigX() {
		return CommandNPC.config;
	}
	
	public static JavaPlugin getInstance() {
		return CommandNPC.instance;
	}
	
	public void logError(String topic, String classx, String method, String error) {
		final String space = "                                                             ";
		String text = "&cTopic";
		topic = "&c" + topic;
		this.log("&4---------------------&b{&1CommandNPC &cError&b}&4---------------------", false);
		this.log(space.substring((space.length() + text.length()) / 2, space.length()) + text, false);
		this.log(space.substring((space.length() + topic.length()) / 2, space.length()) + topic, false);
		this.log("", false);
		for(String s : this.getLines(error, space)) {
			this.log("&b" + space.substring((space.length() + s.length()) / 2, space.length()) + s, false);
		}
		this.log("", false);
		String cl = "&8Class: &c" + classx + "   &8Method: &c" + method;
		for(String s : this.getLines(cl, space)) {
			this.log("&c" + space.substring((space.length() + s.length()) / 2, space.length()) + s, false);
		}
		this.log("", false);
		this.log("&4---------------------&b{&1CommandNPC &cError&b}&4---------------------", false);
	}
	
	private ArrayList<String> getLines(String parse, final String space) {
		ArrayList<String> lines = new ArrayList<String>();
		String s = "";
		String[] split = parse.split(" ");
		final int length = split.length;
		for(int i = 0; i < length; i++) {
			if(s.length() + split[i].length() < space.length()) {
				s += split[i] + " ";
			}else{
				lines.add(s);
				s = split[i] + " ";
			}
			if(i + 1 == length) {
				lines.add(s);
			}
		}
		return lines;
	}
}
