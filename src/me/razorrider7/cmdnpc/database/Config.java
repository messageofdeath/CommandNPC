package me.razorrider7.cmdnpc.database;

import me.razorrider7.cmdnpc.CommandNPC;

public class Config {

	private CommandNPC instance;
	private YamlDatabase config;
	private ClickType clickType;
	private int coolDown;

	public Config(CommandNPC instance) {
		this.instance = instance;
		this.config = new YamlDatabase(this.instance, "config");
		this.config.onStartUp();
		checkConfig();
		loadConfig();
	}

	private void loadConfig() {
		String click = this.config.getString("ClickType", "both");
		if(click.equalsIgnoreCase("left") || click.equalsIgnoreCase("punch")) {
			this.clickType = ClickType.LEFT;
		} else if(click.equalsIgnoreCase("right") || click.equalsIgnoreCase("interact")) {
			this.clickType = ClickType.RIGHT;
		}  else if(click.equalsIgnoreCase("both") || click.equalsIgnoreCase("all")) {
			this.clickType = ClickType.BOTH;
		} else {
			instance.log("Could not load ClickType in config! Defaulting to Both!", true);
			this.clickType = ClickType.BOTH;
		}

		this.coolDown = this.config.getInteger("CoolDown", 20);
		if(this.coolDown < 1) {
			this.coolDown = 20;
			instance.log("CoolDown must be longer than 0!", false);
		}
	}
	
	public void reloadConfig() {
		this.config.onStartUp();
		checkConfig();
		loadConfig();
	}

	private void checkConfig() {
		check("ClickType", "Interact");
		check("CoolDown", 20);
	}

	private void check(String path, Object set) {
		if (!this.config.contains(path)) {
			this.config.set(path, set);
		}
	}

	public ClickType getClickType() {
		return this.clickType;
	}

	public int getCoolDown() {
		return this.coolDown;
	}
}
