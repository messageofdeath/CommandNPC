package me.razorrider7.commandnpc.Utils.npcdatamanager;

public class NPCCommand {

	private String command;
	private String permission;
	private boolean inConsole;
	
	public NPCCommand(String command, String permission, boolean inConsole) {
		this.command = command;
		this.permission = permission;
		this.inConsole = inConsole;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public String getPermission() {
		return this.permission;
	}
	
	public boolean inConsole() {
		return this.inConsole;
	}
}
