package me.messageofdeath.commandnpc.Database;

import java.util.ArrayList;

import me.messageofdeath.commandnpc.CommandNPC;
import me.messageofdeath.commandnpc.Database.PluginSettings.PluginSettings;
import me.messageofdeath.commandnpc.NPCDataManager.NPCCommand;
import me.messageofdeath.commandnpc.NPCDataManager.NPCData;

public class CommandDatabase {

	private final CommandNPC instance;
	private YamlDatabase database;

	public CommandDatabase(CommandNPC instance) {
		this.instance = instance;
	}

	public void initDatabase() {
		database = new YamlDatabase(instance, "commands");
		database.onStartUp();
	}

	public void loadDatabase() {
		for (String idx : database.getSection("NPCS", new ArrayList<>())) {
			NPCData data = new NPCData(Integer.parseInt(idx));
			for (String commands : database.getStringArray("NPCS." + idx + ".Commands", new ArrayList<>())) {
				String[] args = commands.split("~");
				if (args.length == 5) {//Version 1.8.5
					String console = args[2];
					if(console.toLowerCase().contains("-c")) {
						console = console.replace("-c", "");
					}
					data.addCommand(new NPCCommand(args[0], args[1], ClickType.valueOf(PluginSettings.ClickType.getSetting()), Boolean.parseBoolean(console),
							Boolean.parseBoolean(args[3]), false, Double.parseDouble(args[4]), 0));
					continue;
				}
				ClickType clickType;
				if(args.length == 7) {// Version 1.8.6
					clickType = this.getClickType(args);
					data.addCommand(new NPCCommand(Integer.parseInt(args[0]), args[1], args[2], clickType, Boolean.parseBoolean(args[4]), 
							Boolean.parseBoolean(args[5]), false, Double.parseDouble(args[6]), 0));
					continue;
				}
				args = commands.split("~~~");
				if(args.length == 8) {//Version 1.8.7
					clickType = this.getClickType(args);
					data.addCommand(new NPCCommand(Integer.parseInt(args[0]), args[1], args[2], clickType, Boolean.parseBoolean(args[4]), 
							Boolean.parseBoolean(args[5]), false, Double.parseDouble(args[6]), Integer.parseInt(args[7])));
					continue;
				}
				if(args.length == 9) {//Version 1.8.8
					clickType = this.getClickType(args);
					data.addCommand(new NPCCommand(Integer.parseInt(args[0]), args[1], args[2], clickType, Boolean.parseBoolean(args[4]), 
							Boolean.parseBoolean(args[5]), Boolean.parseBoolean(args[6]), Double.parseDouble(args[7]), Integer.parseInt(args[8])));
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
			ArrayList<String> commands = new ArrayList<>();
			for (NPCCommand command : data.getCommands()) {
				commands.add(command.getID() + "~~~" + command.getCommand() + "~~~" + command.getPermission() + "~~~" + command.getClickType().name() + "~~~" + 
						command.inConsole() + "~~~" + command.asOp() + "~~~" + command.isRandom() + "~~~" + command.getCost() + "~~~" + command.getDelay());
			}
			database.set("NPCS." + data.getId() + ".Commands", commands);
		}
	}
	
	private ClickType getClickType(String[] args) {
		return args.length > 3 && ClickType.hasClickType(args[3]) ? ClickType.getClickType(args[3]) : ClickType.getClickType(PluginSettings.ClickType.getSetting());
	}
}
