package me.messageofdeath.commandnpc.Utilities.CitizenBackend;

import java.lang.reflect.Field;

import me.messageofdeath.commandnpc.CommandNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.command.CommandManager;

public class CitizenCommandRegister {

	private final CommandNPC instance;

	public CitizenCommandRegister(CommandNPC instance) {
		this.instance = instance;
	}

	public void registerCitizenCommand(Class<?> classx) {
		try {
			Field field = CitizensAPI.getPlugin().getClass().getDeclaredField("commands");
			field.setAccessible(true);
			Object obj = field.get(CitizensAPI.getPlugin());
			CommandManager manager = (CommandManager) obj;
			manager.register(classx);
			field.setAccessible(false);
		} catch (Exception e) {
			this.instance.logError("Citizens", "CitizenCommandRegister", "registerCitizenCommand(Class)", "Couldn't implement commands into citizens! " + "Shutting down!");
			this.instance.getServer().getPluginManager().disablePlugin(this.instance);
		}
	}
}
