package me.razorrider7.commandnpc.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author messageofDEATH
 */

public class YamlDatabase {
	public JavaPlugin plugin = null;
	public String fileName = null;
	public String fileExtension = null;
	public String fileLocation = null;
	public File file = null;
	public FileConfiguration fileConfig = null;
	public boolean createdFile = false;
	public boolean saveOnSet = true;

	public YamlDatabase(JavaPlugin plugin) {
		this.plugin = plugin;
		this.fileExtension = ".yml";
		this.fileConfig = new YamlConfiguration();
	}

	public YamlDatabase(JavaPlugin plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.fileExtension = ".yml";
		this.fileConfig = new YamlConfiguration();
	}

	public YamlDatabase(JavaPlugin plugin, String fileName, String fileLocation) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.fileExtension = ".yml";
		this.fileConfig = new YamlConfiguration();
		this.fileLocation = fileLocation;
	}

	public void changeFile(String fileName) {
		changeFile(fileName, this.plugin.getDataFolder().getPath());
	}

	public void changeFile(String fileName, String fileLocation) {
		this.fileName = fileName;
		this.fileLocation = fileLocation;
		onStartUp();
	}

	public void onStartUp() {
		if (this.fileLocation == null) {
			this.plugin.getDataFolder().mkdirs();
			this.file = new File(this.plugin.getDataFolder(), this.fileName + this.fileExtension);
		} else {
			new File(this.fileLocation).mkdirs();
			this.file = new File(this.fileLocation, this.fileName + this.fileExtension);
		}
		try {
			this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
			if (!this.file.exists()) {
				this.file.getParentFile().mkdirs();
				this.file.createNewFile();
				if (this.plugin.getResource(this.fileName + this.fileExtension) != null) {
					copy(this.plugin.getResource(this.fileName + this.fileExtension), this.file);
				}
				this.createdFile = true;
			}
		} catch (Exception e) {
			this.plugin.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void onShutDown() {
		save();
	}

	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte['Ѐ'];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reload() {
		try {
			this.fileConfig.load(this.file);
		} catch (Exception localException) {
		}
	}

	public void save() {
		try {
			this.fileConfig.save(this.file);
		} catch (Exception localException) {
		}
	}

	public ConfigurationSection getConfigurationSection(String key,
			ConfigurationSection fallback) {
		if (this.fileConfig.contains(key)) {
			return this.fileConfig.getConfigurationSection(key);
		}
		return fallback;
	}

	public ArrayList<String> getSection(String key, ArrayList<String> fallback) {
		if (this.fileConfig.contains(key)) {
			ArrayList<String> section = new ArrayList<String>();
			for (Object str : getConfigurationSection(key, null).getKeys(false)
					.toArray()) {
				section.add(String.valueOf(str));
			}
			return section;
		}
		return fallback;
	}

	public int getInteger(String key, int fallback) {
		if (this.fileConfig.contains(key)) {
			return this.fileConfig.getInt(key);
		}
		return fallback;
	}

	public String getString(String key, String fallback) {
		if (this.fileConfig.contains(key)) {
			return this.fileConfig.getString(key);
		}
		return fallback;
	}

	public boolean contains(String key) {
		return this.fileConfig.contains(key);
	}

	public boolean getBoolean(String key, boolean fallback) {
		if (this.fileConfig.contains(key)) {
			return this.fileConfig.getBoolean(key);
		}
		return fallback;
	}

	public ArrayList<String> getStringArray(String key,
			ArrayList<String> fallback) {
		if (this.fileConfig.contains(key)) {
			return (ArrayList<String>) this.fileConfig.getStringList(key);
		}
		return fallback;
	}

	public double getDouble(String key, double fallback) {
		if (this.fileConfig.contains(key)) {
			return this.fileConfig.getDouble(key);
		}
		return fallback;
	}

	public Object getObject(String key, Object fallback) {
		if (this.fileConfig.contains(key)) {
			return this.fileConfig.get(key);
		}
		return fallback;
	}

	public float getFloat(String key, float fallback) {
		if (this.fileConfig.contains(key)) {
			return (float) this.fileConfig.getDouble(key);
		}
		return fallback;
	}

	public Material getMaterial(String key, Material fallback) {
		if (this.fileConfig.contains(key)) {
			return this.fileConfig.getItemStack(key).getType();
		}
		return fallback;
	}

	public void set(String key, Object set) {
		this.fileConfig.set(key, set);
		if (this.saveOnSet) {
			save();
		}
	}
}
