package me.razorrider7.commandnpc.database;

import java.util.ArrayList;

import me.razorrider7.commandnpc.CommandNPC;
import me.razorrider7.commandnpc.Utils.npcdatamanager.NPCCommand;
import me.razorrider7.commandnpc.Utils.npcdatamanager.NPCData;

public class CommandDatabase {

	private CommandNPC instance;
	private YamlDatabase database;
	
	public CommandDatabase(CommandNPC instance) {
		this.instance = instance;
	}
	
	public void initDatabase() {
		this.database = new YamlDatabase(this.instance, "commands");
		this.database.onStartUp();
	}
	
	public void loadDatabase() {
		for(String idx : this.database.getSection("NPCS", new ArrayList<String>())) {
			NPCData data = new NPCData(Integer.parseInt(idx));
			for(String commands : this.database.getStringArray("NPCS." + idx + ".Commands", new ArrayList<String>())) {
				String[] args = commands.split("~");
				data.addCommand(new NPCCommand(args[0], args[1], Boolean.parseBoolean(args[2])));
			}
			CommandNPC.getCommandManager().addNPCData(data);
		}
	}
	
	public void deleteNPC(int id) {
		this.database.set("NPCS." + id, null);
	}
	
	public void saveDatabase() {
		for(NPCData data : CommandNPC.getCommandManager().getNPCDatas()) {
			ArrayList<String> commands = new ArrayList<String>();
			for(NPCCommand command : data.getCommands()) {
				commands.add(command.getCommand() + "~" + command.getPermission() + "~" + command.inConsole());
			}
			this.database.set("NPCS." + data.getId() + ".Commands", commands);
		}
	}
}
