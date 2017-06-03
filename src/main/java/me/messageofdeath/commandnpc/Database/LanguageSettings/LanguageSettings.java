package me.messageofdeath.commandnpc.Database.LanguageSettings;

public enum LanguageSettings {
	
	/**-------------------------------------- General --------------------------------------**/
	General_PluginTag("&8[&2CommandNPC&8] &6"), 
	General_ErrorTag("&0[&cError&0] &c"), 
	
	/**-------------------------------------- commandnpc NPC --------------------------------------**/
	CmdNPC_Cooldown("You cannot do this yet!"),
	CmdNPC_NoMoney("You do not have enough money for this!"),
	CmdNPC_Executed("Command Executed."),
	
	/**-------------------------------------- General Commands --------------------------------------**/
	Commands_NoPermission("You do not have permission for this!"), 
	Commands_WrongArgs("Wrong Args. Use /commandnpc reload"),
	Commands_MustBeNumeric("The argument '%arg' must be numeric!"),
	Commands_AlreadyExists("A %type with that name already exists!"),
	Commands_DoesNotExist("A %type with that name doesn't exist!"),
	Commands_List_Header("Available %type:"),
	Commands_List_Line(" &8 -&a %name"),
	Commands_List_InfoHeader("Information for NPC '%id':"),
	Commands_List_InfoLineHeader("  &7%name: &6%value"),
	Commands_List_InfoLinePrefix("  &8- "),
	Commands_List_InfoLine("&2%name: &b%value"),
	Commands_SetTo_Header("You have set the following variables:"),
	Commands_SetTo_Line("  &8- &6%variable to&8: &b%value"),
	Commands_SetTo_Nothing("  &8- &4Nothing's changed..."),
	
	/**-------------------------------------- commandnpc Commands --------------------------------------**/
	Commands_CmdNPC_Reload("You have successfully reloaded the commandnpc configuration file!"),
	
	/**-------------------------------------- Citizens Commands --------------------------------------**/
	Commands_Citizens_Add("You have successfully added the command to the NPC!"),
	Commands_Citizens_Reset("You have successfully reset the commands for this NPC!"),
	Commands_Citizens_Removed("You have successfully removed the command!"),
	Commands_Citizens_NumBetween("You must enter a number between %num1 and %num2!"),
	Commands_Citizens_ValueFlagT("For value flag 't', you must use 'left', 'right', or 'both'!"),
	Commands_Citizens_NoCmdInput("You didn't input a command!"),
	Commands_Citizens_NoCommands("There are no set commands at the moment for this NPC!");

	private String setting;
	private final String defaultSetting;

	LanguageSettings(String defaultSetting) {
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

	public String getDefaultSetting() {
		return this.defaultSetting;
	}
}
