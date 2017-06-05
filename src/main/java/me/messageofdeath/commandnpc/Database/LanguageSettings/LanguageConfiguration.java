package me.messageofdeath.commandnpc.Database.LanguageSettings;

import me.messageofdeath.commandnpc.CommandNPC;
import me.messageofdeath.commandnpc.Database.YamlDatabase;

public class LanguageConfiguration {
	
	protected final CommandNPC instance;
	private YamlDatabase config;

	public LanguageConfiguration(CommandNPC instance) {
		this.instance = instance;
	}

	public void initConfiguration() {
		this.config = new YamlDatabase(this.instance, "language", false);
		this.config.onStartUp();
		this.config.saveOnSet = false;
		boolean changes = false;
		for(LanguageSettings setting : LanguageSettings.values()) {
			if (!this.config.contains(setting.getName().replace("_", "."))) {
				changes = true;
				this.config.set(setting.getName().replace("_", "."), setting.getDefaultSetting());
			}
		}
		if(changes) {
			this.config.save();
		}
		this.config.saveOnSet = true;
	}

	public YamlDatabase getConfig() {
		return this.config;
	}

	public void loadConfiguration() {
		for(LanguageSettings setting : LanguageSettings.values()) {
			setting.setSetting(this.config.getString(setting.getName().replace("_", "."), setting.getDefaultSetting()));
		}
	}
}
