package me.messageofdeath.commandnpc.NPCDataManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NPCData {

	private final int npcID;
	private ArrayList<NPCCommand> commands;
	
	public NPCData(int npcID) {
		this.npcID = npcID;
		this.commands = new ArrayList<>();
	}
	
	public NPCData(int npcID, NPCCommand command) {
		this(npcID);
		this.addCommand(command);
	}
	
	public NPCData(int npcID, ArrayList<NPCCommand> commands) {
		this(npcID);
		for(NPCCommand cmd : commands) {
			this.addCommand(cmd);
		}
	}
	
	public int getId() {
		return this.npcID;
	}
	
	public boolean isRandom() {
		for(NPCCommand cmd : this.commands) {
			if(cmd.isRandom()) {
				return true;
			}
		}
		return false;
	}
	
	public void addCommand(NPCCommand command) {
		this.commands.add(command);
		checkPositions();
	}
	
	public void removeCommand(int id) {
		if(this.hasCommand(id)) {
			this.commands.remove(this.getCommand(id));
			checkPositions();
		}
	}
	
	public boolean hasCommand(int id) {
		return this.getCommand(id) != null;
	}
	
	public NPCCommand getCommand(int id) {
		for(NPCCommand command : this.commands) {
			if(command.getID() == id) {
				return command;
			}
		}
		return null;
	}
	
	public ArrayList<NPCCommand> getCommands() {
		return this.commands;
	}
	
	public void checkPositions() {
		ArrayList<NPCCommand> commands = this.commands;
		Collections.sort(commands, comparePosition());
		if (!commands.isEmpty()) {
			int lastPosition = 0;
			int difference;
			NPCCommand commandx;
			for (int i = 0; i < commands.size(); i++) {
				commandx = commands.get(i);
				if (lastPosition != commandx.getID()) {
					difference = commandx.getID() - lastPosition;
					if (difference > 1) {
						for (int x = i; x < commands.size(); x++) {
							commands.get(x).setID(commands.get(x).getID() - difference);
						}
					}
					difference = commandx.getID() - lastPosition;
					if (difference == 0) {
						for (int x = i; x < commands.size(); x++) {
							commands.get(x).setID(commands.get(x).getID() + 1);
						}
					}
					lastPosition = commandx.getID();
				}else if(lastPosition == commandx.getID()){
					commandx.setID(commandx.getID() + 1);
					lastPosition = commandx.getID();
				}
			}
			Collections.sort(commands, comparePosition());
			this.commands = commands;
		}
	}
	
	private static Comparator<NPCCommand> comparePosition() {
		return (cmd1, cmd2) -> {
            if (cmd1.getID() > cmd2.getID()) {
                return 1;
            }
            if (cmd1.getID() < cmd2.getID()) {
                return -1;
            }
            return 0;
        };
	}
}
