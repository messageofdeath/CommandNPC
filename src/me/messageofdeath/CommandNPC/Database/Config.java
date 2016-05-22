package me.messageofdeath.CommandNPC.Database;

import me.messageofdeath.CommandNPC.CommandNPC;

public class Config {

	private CommandNPC instance;
	private YamlDatabase config;
	private ClickType clickType;
	
	public Config(CommandNPC instance) {
		this.instance = instance;
		this.config = new YamlDatabase(this.instance, "config");
		this.config.onStartUp();
		this.checkConfig();
	}
	
	public void loadConfig() {
		if(this.config.getString("ClickType", "Interact").equalsIgnoreCase("Left") || this.config.getString("ClickType", "Interact").equalsIgnoreCase("Punch")) {
			this.clickType = ClickType.Left;
			this.config.set("ClickType", "Left");
		} else if(this.config.getString("ClickType", "Interact").equalsIgnoreCase("Right") || this.config.getString("ClickType", "Interact").equalsIgnoreCase("Interact")) {
			this.clickType = ClickType.Right;
			this.config.set("ClickType", "Right");
		} else if(this.config.getString("ClickType", "Interact").equalsIgnoreCase("Both") || this.config.getString("ClickType", "Interact").equalsIgnoreCase("All")) {
			this.clickType = ClickType.Both;
			this.config.set("ClickType", "Both");
		} else {
			this.clickType = ClickType.Right;
			this.config.set("ClickType", "Error");
		}
	}
	
	private void checkConfig() {
		this.check("ClickType", "Interact");
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
