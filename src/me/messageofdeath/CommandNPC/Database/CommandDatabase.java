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
					String console = args[2];
					if(console.toLowerCase().contains("-c")) {
						console.replace("-c", "");
					}
					data.addCommand(new NPCCommand(args[0], args[1], CommandNPC.getConfigX().getClickType(), Boolean.parseBoolean(args[2]), 
							Boolean.parseBoolean(args[3]), Double.parseDouble(args[4]), 0));
					continue;
				}
				ClickType clickType = null;
				if(args.length == 7) {// Version 1.8.6
					clickType = this.getClickType(args);
					data.addCommand(new NPCCommand(Integer.parseInt(args[0]), args[1], args[2], clickType, Boolean.parseBoolean(args[4]), 
							Boolean.parseBoolean(args[5]), Double.parseDouble(args[6]), 0));
					continue;
				}
				args = commands.split("~~~");
				if(args.length == 8) {//Version 1.8.7
					clickType = this.getClickType(args);
					data.addCommand(new NPCCommand(Integer.parseInt(args[0]), args[1], args[2], clickType, Boolean.parseBoolean(args[4]), 
							Boolean.parseBoolean(args[5]), Double.parseDouble(args[6]), Integer.parseInt(args[7])));
					continue;
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
				commands.add(command.getID() + "~~~" + command.getCommand() + "~~~" + command.getPermission() + "~~~" + command.getClickType().toString() + "~~~" + 
						command.inConsole() + "~~~" + command.asOp() + "~~~" + command.getCost() + "~~~" + command.getDelay());
			}
			database.set("NPCS." + data.getId() + ".Commands", commands);
		}
	}
	
	private ClickType getClickType(String[] args) {
		ClickType clickType = null;
		if(args.length > 3) {
			if(args[3].equalsIgnoreCase("left")) {
				clickType = ClickType.LEFT;
			}else if(args[3].equalsIgnoreCase("right")) {
				clickType = ClickType.RIGHT;
			}else if(args[3].equalsIgnoreCase("both")) {
				clickType = ClickType.BOTH;
			}else{
				clickType = CommandNPC.getConfigX().getClickType();
			}
		}
		return clickType;
	}
}
