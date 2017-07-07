package me.messageofdeath.commandnpc.NPCDataManager;

import me.messageofdeath.commandnpc.Database.ClickType;

public class NPCCommand {

	private int id;
	private int delay;
	private int cooldown;
	private String command;
	private String permission;
	private String cooldownMessage;
	private ClickType clickType;
	private boolean inConsole;
	private boolean asOp;
	private boolean isRandom;
	private boolean ignorePermMsg;
	private boolean ignoreMoneyMsg;
	private double cost;
	
	public NPCCommand(String command, String permission, String cooldownMessage, ClickType clickType, boolean inConsole, boolean asOp, boolean isRandom, boolean ignorePermMsg,
					  boolean ignoreMoneyMsg, double cost, int delay, int cooldown) {
		this(1000, command, permission, cooldownMessage, clickType, inConsole, asOp, isRandom, ignorePermMsg, ignoreMoneyMsg, cost, delay, cooldown);
	}
	
	public NPCCommand(int id, String command, String permission, String cooldownMessage, ClickType clickType, boolean inConsole, boolean asOp, boolean isRandom, boolean ignorePermMsg,
					  boolean ignoreMoneyMsg, double cost, int delay, int cooldown) {
		this.id = id;
		this.command = command;
		this.permission = permission;
		this.cooldownMessage = cooldownMessage;
		this.clickType = clickType;
		this.inConsole = inConsole;
		this.asOp = asOp;
		this.isRandom = isRandom;
		this.ignorePermMsg = ignorePermMsg;
		this.ignoreMoneyMsg = ignoreMoneyMsg;
		this.cost = cost;
		this.delay = delay;
		this.cooldown = cooldown;
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

	public String getCooldownMessage() {
		return this.cooldownMessage;
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
	
	public boolean isRandom() {
		return this.isRandom;
	}

	public boolean isIgnorePermMsg() {
		return this.ignorePermMsg;
	}

	public boolean isIgnoreMoneyMsg() {
		return this.ignoreMoneyMsg;
	}
	
	public double getCost() {
		return this.cost;
	}
	
	public int getDelay() {
		return this.delay;
	}

	public int getCooldown() {
		return this.cooldown;
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

	public void setCooldownMessage(String cooldownMessage) {
		this.cooldownMessage = cooldownMessage;
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
	
	public void setIsRandom(boolean isRandom) {
		this.isRandom = isRandom;
	}

	public void setIgnorePermMsg(boolean ignorePermMsg) {
		this.ignorePermMsg = ignorePermMsg;
	}

	public void setIgnoreMoneyMsg(boolean ignoreMoneyMsg) {
		this.ignoreMoneyMsg = ignoreMoneyMsg;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}
}
