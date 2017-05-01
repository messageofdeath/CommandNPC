package me.messageofdeath.CommandNPC.NPCDataManager;

import me.messageofdeath.CommandNPC.Database.ClickType;

public class NPCCommand {

	private String command;
	private String permission;
	private ClickType clickType;
	private boolean inConsole;
	private boolean asOp;
	private double cost;
	
	public NPCCommand(String command, String permission, ClickType clickType, boolean inConsole, boolean asOp, double cost) {
		this.command = command;
		this.permission = permission;
		this.clickType = clickType;
		this.inConsole = inConsole;
		this.asOp = asOp;
		this.cost = cost;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public String getPermission() {
		return this.permission;
	}
	
	public ClickType getClickType() {
		return this.clickType;
	}
	
	public boolean inConsole() {
		return this.inConsole;
	}
	
	public boolean asOp() {
		return this.asOp;
	}
	
	public double getCost() {
		return this.cost;
	}
}
