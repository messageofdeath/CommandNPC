package me.razorrider7.cmdnpc.util.npcdatamanager;

public class NPCCommand {

	private String command;
	private String permission;
	private boolean inConsole;
	private boolean asOp;
	private double cost;
	
	public NPCCommand(String command, String permission, boolean inConsole, boolean asOp, double cost) {
		this.command = command;
		this.permission = permission;
		this.inConsole = inConsole;
		this.asOp = asOp;
		this.cost = cost;
	}
	
	public double getCost() {
		return this.cost;
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
	
	public boolean asOp() {
		return this.asOp;
	}
	
}
