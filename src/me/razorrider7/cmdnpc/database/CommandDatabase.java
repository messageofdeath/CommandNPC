package me.razorrider7.cmdnpc.database;

import java.util.ArrayList;

import me.razorrider7.cmdnpc.CommandNPC;
import me.razorrider7.cmdnpc.util.npcdatamanager.NPCCommand;
import me.razorrider7.cmdnpc.util.npcdatamanager.NPCData;

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
				if (args.length == 5) {
					data.addCommand(new NPCCommand(args[0], args[1], Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]), Double.parseDouble(args[4])));
				} else {
					String command = args[0];
					String permission = "";
					boolean inConsole = false;
					boolean asOp = false;
					double cost = 0.0;
					if (args[1] != null && (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
						if (args[2] != null && (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))) {
							inConsole = Boolean.parseBoolean(args[1]);
							asOp = Boolean.parseBoolean(args[2]);
							if (args[3] != null) {
								cost = Double.parseDouble(args[3]);
							}
						} else if (args[1].substring(0, 2).contains("-c")) {
							inConsole = Boolean.parseBoolean(args[1].replace("-c", ""));
							if (args[2] != null && Double.parseDouble(args[3]) > 0.0) {
								cost = Double.parseDouble(args[2]);
							}
						} else {
							asOp = Boolean.parseBoolean(args[1]);
							if (args[3] != null && Double.parseDouble(args[3]) > 0.0) {
								cost = Double.parseDouble(args[2]);
							}
						}
					} else if (args[1] != null) {
						if (Double.parseDouble(args[1]) > 0.0) {
							cost = Double.parseDouble(args[1]);
						}
					} else if (args[1] != null) {
						permission = args[1];
						if (args[2] != null && (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false"))) {
							if (args[3] != null && (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false"))) {
								inConsole = Boolean.parseBoolean(args[2]);
								asOp = Boolean.parseBoolean(args[3]);
								if (args[4] != null) {
									cost = Double.parseDouble(args[4]);
								}
							} else if (args[2].substring(0, 2).contains("-c")) {
								inConsole = Boolean.parseBoolean(args[2].replace("-c", ""));
								if (args[3] != null && Double.parseDouble(args[3]) > 0.0) {
									cost = Double.parseDouble(args[3]);
								}
							} else {
								asOp = Boolean.parseBoolean(args[2]);
								if (args[3] != null && Double.parseDouble(args[3]) > 0.0) {
									cost = Double.parseDouble(args[3]);
								}
							}
						}
					}
					data.addCommand(new NPCCommand(command, permission, inConsole, asOp, cost));
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
				commands.add(command.getCommand() + "~" + command.getPermission() + "~-c" + command.inConsole() + "~" + command.asOp() + "~" + command.getCost());
			}
			database.set("NPCS." + data.getId() + ".Commands", commands);
		}
	}
}
