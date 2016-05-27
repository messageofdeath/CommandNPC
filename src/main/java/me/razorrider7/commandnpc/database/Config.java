package me.razorrider7.commandnpc.database;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;

import me.razorrider7.commandnpc.CommandNPC;

public class Config {

	private CommandNPC instance;
	private YamlDatabase config;
	private ClickType clickType;
	
	public Config(CommandNPC instance) {
		this.instance = instance;
		this.config = new YamlDatabase(this.instance, "config");
		this.config.onStartUp();
		this.checkConfig();
		this.loadConfig();
	}
	
	public void loadConfig() {
		if(this.config.getString("Clicktype", "Both").equalsIgnoreCase("Left")) {
			this.clickType = ClickType.Left;
		} else if(this.config.getString("Clicktype", "Both").equalsIgnoreCase("Right")) {
			this.clickType = ClickType.Right;
		} else if (this.config.getString("Clicktype", "Both").equalsIgnoreCase("Both")) {
			this.clickType = ClickType.Both;
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "CommandNPC could not read the config.");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Defaulting to both left and right click.");
		}
		
		switch(this.config.getString("ClickType", "Interact")) {
			case "Left":
			case "Punch":
				this.clickType = ClickType.Left;
				break;
			case "Right":
			case "Interact":
				this.clickType = ClickType.Right;
				break;
			case "Both":
			case "All":
				this.clickType = ClickType.Both;
				break;
			default:
				this.clickType = ClickType.Right;
		}
	}
	
	private void checkConfig() {
		this.check("ClickType", "Both");
	}
	
	private void check(String path, Object set) {
		if(!this.config.contains(path)) {
			this.config.set(path, set);
		}
	}
	
	public ClickType getClickType() {
		return this.clickType;
	}
}
