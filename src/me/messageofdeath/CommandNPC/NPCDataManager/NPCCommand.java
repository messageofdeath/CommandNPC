package me.messageofdeath.CommandNPC.NPCDataManager;

import me.messageofdeath.CommandNPC.Database.ClickType;

public class NPCCommand {

	private int id;
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
	
	public NPCCommand(int id, String command, String permission, ClickType clickType, boolean inConsole, boolean asOp, double cost) {
		this.id = id;
		this.command = command;
		this.permission = permission;
		this.clickType = clickType;
		this.inConsole = inConsole;
		this.asOp = asOp;
		this.cost = cost;
	}
	
	public int getID() {
		return this.id;
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
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
	
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	public void setClickType(ClickType clickType) {
		this.clickType = clickType;
	}
	
	public void setInConsole(boolean inConsole) {
		this.inConsole = inConsole;
	}
	
	public void setAsOP(boolean asOp) {
		this.asOp = asOp;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
}