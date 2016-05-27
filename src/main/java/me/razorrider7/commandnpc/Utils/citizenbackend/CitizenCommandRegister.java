package me.razorrider7.commandnpc.Utils.citizenbackend;

import java.lang.reflect.Field;

import me.razorrider7.commandnpc.CommandNPC;
import net.citizensnpcs.api.CitizensAPI;

public class CitizenCommandRegister {
	
	private CommandNPC instance;

	public CitizenCommandRegister(CommandNPC instance) {
		this.instance = instance;
	}
	
	public void registerCitizenCommand(Class<?> classx) {
		try {
			Field field = CitizensAPI.getPlugin().getClass().getDeclaredField("commands");
			field.setAccessible(true);
			Object obj = field.get(CitizensAPI.getPlugin());
			net.citizensnpcs.api.command.CommandManager manager = (net.citizensnpcs.api.command.CommandManager)obj;
			manager.register(classx);
			field.setAccessible(false);
		} catch (Exception e) {
			this.instance.logError("Citizens", "CitizenCommandRegister", "registerCitizenCommand(Class)", "Couldn't implement commands into citizens! "
					+ "Shutting down!");
			this.instance.getServer().getPluginManager().disablePlugin(this.instance);
		}
	}
}
