package me.messageofdeath.commandnpc.Database.PluginSettings;

import me.messageofdeath.commandnpc.CommandNPC;
import me.messageofdeath.commandnpc.Database.YamlDatabase;

public class PluginConfiguration {
	
	protected final CommandNPC instance;
	private YamlDatabase config;

	public PluginConfiguration(CommandNPC instance) {
		this.instance = instance;
	}

	public void initConfiguration() {
		this.config = new YamlDatabase(this.instance, "config", false);
		this.config.onStartUp();
		this.config.saveOnSet = false;
		boolean changes = false;
		for (PluginSettings setting : PluginSettings.values()) {
			if (!this.config.contains(setting.getName().replace("_", "."))) {
				changes = true;
				this.config.set(setting.getName().replace("_", "."), setting.getDefaultSetting());
			}
		}
		if (changes) {
			this.config.save();
		}
		this.config.saveOnSet = true;
	}

	public YamlDatabase getConfig() {
		return this.config;
	}

	public void loadConfiguration() {
		for (PluginSettings setting : PluginSettings.values()) {
			setting.setSetting(this.config.getString(setting.getName().replace("_", "."), setting.getDefaultSetting()));
		}
	}
}
