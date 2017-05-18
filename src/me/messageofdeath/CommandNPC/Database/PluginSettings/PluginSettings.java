package me.messageofdeath.CommandNPC.Database.PluginSettings;

import me.messageofdeath.CommandNPC.Utilities.Utilities;

public enum PluginSettings {
	
	/**-------------------------------------- General --------------------------------------**/
	ClickType("both"),
	CoolDown("20"),
	ExecuteCommandMessage("false"),
	BungeeCord("false"),
	CooldownMessage("true");

	private String setting;
	private final String defaultSetting;

	private PluginSettings(String defaultSetting) {
		this.defaultSetting = defaultSetting;
	}

	public String getName() {
		return toString();
	}

	public void setSetting(String setting) {
		this.setting = setting;
	}

	public void setDefaultSetting() {
		this.setting = this.defaultSetting;
	}

	public String getSetting() {
		return this.setting;
	}
	
	public Integer getInteger() {
		if(Utilities.isInteger(this.setting)) {
			return Integer.parseInt(this.setting);
		}else{
			return -1;
		}
	}
	
	public Double getDouble() {
		if(Utilities.isDouble(this.setting)) {
			return Double.parseDouble(this.setting);
		}else{
			return -1.0;
		}
	}
	
	public boolean getBoolean() {
		return Boolean.parseBoolean(this.setting);
	}

	public String getDefaultSetting() {
		return this.defaultSetting;
	}
}
