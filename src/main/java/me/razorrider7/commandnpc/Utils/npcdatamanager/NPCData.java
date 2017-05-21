package me.razorrider7.commandnpc.Utils.npcdatamanager;

import java.util.ArrayList;

public class NPCData {

	private int id;
	private ArrayList<NPCCommand> commands;
	
	public NPCData(int id) {
		this.id = id;
		this.commands = new ArrayList<NPCCommand>();
	}
	
	public NPCData(int id, NPCCommand command) {
		this(id);
		this.commands.add(command);
	}
	
	public NPCData(int id, ArrayList<NPCCommand> commands) {
		this(id);
		this.commands.addAll(commands);
	}
	
	public int getId() {
		return this.id;
	}
	
	public void addCommand(NPCCommand command) {
		this.commands.add(command);
	}
	
	public ArrayList<NPCCommand> getCommands() {
		return this.commands;
	}
}
