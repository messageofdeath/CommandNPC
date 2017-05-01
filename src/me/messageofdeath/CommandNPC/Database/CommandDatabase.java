package me.messageofdeath.CommandNPC.Database;

import java.util.ArrayList;

import me.messageofdeath.CommandNPC.CommandNPC;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCCommand;
import me.messageofdeath.CommandNPC.NPCDataManager.NPCData;

public class CommandDatabase {

	private CommandNPC instance;
	private YamlDatabase database;

	public CommandDatabase(CommandNPC instance) {
		this.instance = instance;
	}

	public void initDatabase() {
		database = new YamlDatabase(instance, "commands");
		database.onStartUp();
	}

	public void loadDatabase() {
		for (String idx : database.getSection("NPCS", new ArrayList<String>())) {
			NPCData data = new NPCData(Integer.parseInt(idx));
			for (String commands : database.getStringArray("NPCS." + idx + ".Commands", new ArrayList<String>())) {
				String[] args = commands.split("~");
				if (args.length == 5) {//Version 1.8.5
					data.addCommand(new NPCCommand(args[0], args[1], CommandNPC.getConfigX().getClickType(), Boolean.parseBoolean(args[2]), 
							Boolean.parseBoolean(args[3]), Double.parseDouble(args[4])));
				}else if(args.length == 7) {// Version 1.8.6 and later
					ClickType clickType = null;
					if(args[3].equalsIgnoreCase("left")) {
						clickType = ClickType.LEFT;
					}else if(args[3].equalsIgnoreCase("right")) {
						clickType = ClickType.RIGHT;
					}else if(args[3].equalsIgnoreCase("both")) {
						clickType = ClickType.BOTH;
					}else{
						clickType = CommandNPC.getConfigX().getClickType();
					}
					data.addCommand(new NPCCommand(Integer.parseInt(args[0]), args[1], args[2], clickType, Boolean.parseBoolean(args[4]), 
							Boolean.parseBoolean(args[5]), Double.parseDouble(args[6])));
				}
			}
			CommandNPC.getCommandManager().addNPCData(data);
		}
	}

	public void deleteNPC(int id) {
		database.set("NPCS." + id, null);
	}

	public void saveDatabase() {
		for (NPCData data : CommandNPC.getCommandManager().getNPCDatas()) {
			ArrayList<String> commands = new ArrayList<String>();
			for (NPCCommand command : data.getCommands()) {
				commands.add(command.getID() + "~" + command.getCommand() + "~" + command.getPermission() + "~" + command.getClickType().toString() + "~" + 
						command.inConsole() + "~" + command.asOp() + "~" + command.getCost());
			}
			database.set("NPCS." + data.getId() + ".Commands", commands);
		}
	}
}
