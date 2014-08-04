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
		switch(this.config.getString("ClickType", "Interact")) {
			case "Left":
			case "Punch":
				this.clickType = ClickType.Left;
				break;
			case "Right":
			case "Interact":
				this.clickType = ClickType.Right;
			case "Both":
			case "All":
				this.clickType = ClickType.Both;
				break;
			default:
				this.clickType = ClickType.Right;
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
