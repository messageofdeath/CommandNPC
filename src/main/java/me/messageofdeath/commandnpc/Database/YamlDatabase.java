package me.messageofdeath.commandnpc.Database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

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
	public String fileName = null, fileExtension = null, fileLocation = null;
	public File file = null;
	public FileConfiguration fileConfig = null;
	public boolean createdFile = false, saveOnSet = true, copyFileOnStart = true;
	
    /**
	 * Creates a new instance of YamlDatabase with the default fileLocation.
	 * @param plugin
	 * @return new YamlDatabase
	 */
	
	public YamlDatabase(JavaPlugin plugin) {
		this.plugin = plugin;
		this.fileExtension = ".yml";
		this.fileConfig = new YamlConfiguration();
	}

	/**
	 * Creates a new instance of YamlDatabase with the default fileLocation.
	 * @param plugin
	 * @param fileName
	 * @return new YamlDatabase
	 */
	public YamlDatabase(JavaPlugin plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.fileExtension = ".yml";
		this.fileConfig = new YamlConfiguration();
	}

	public YamlDatabase(JavaPlugin plugin, String fileName, boolean copyFileOnStart) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.fileExtension = ".yml";
		this.fileConfig = new YamlConfiguration();
		this.copyFileOnStart = copyFileOnStart;
	}
	
    /**
	 * Creates a new instance of YamlDatabase with a set fileLocation.
	 * @param plugin
	 * @param fileName
     * @param fileLocation
	 * @return new YamlDatabase
	 */
    
	public YamlDatabase(JavaPlugin plugin, String fileName, String fileLocation) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.fileExtension = ".yml";
		this.fileConfig = new YamlConfiguration();
		this.fileLocation = fileLocation;
	}
    
	/**
	 * Checks if file exists, if not creates one and puts default.
	 */
	public void changeFile(String fileName) {
		this.changeFile(fileName, this.plugin.getDataFolder().getPath());
	}
	
	public void changeFile(String fileName, String fileLocation) {
		this.fileName = fileName;
		this.fileLocation = fileLocation;
		this.onStartUp();
	}
    
	public void onStartUp() {
		if(this.fileLocation == null)
			this.file = new File(this.plugin.getDataFolder(), this.fileName + this.fileExtension);
		else
			this.file = new File(this.fileLocation, this.fileName + this.fileExtension);
		try{
			// *** Config ***
			this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
			if(!this.file.exists()){
				this.file.getParentFile().mkdirs();
				this.file.createNewFile();
				if(copyFileOnStart && this.plugin.getResource(this.fileName + this.fileExtension) != null)
					copy(this.plugin.getResource(this.fileName + this.fileExtension), this.file);
				this.createdFile = true;
			}
			//this.fileConfig.load(this.file);
		}catch (Exception e){e.getCause();}
	}
	
    /**
	 * Saves the file.
	 */
    
	public void onShutDown() {
		this.save();
	}
	
	private void copy(InputStream in, File file) {
		try{
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
        
     /**
	 * Reloads the file
	 */
	
	public void reload() {
		try{
			this.fileConfig.load(this.file);
		}catch (Exception ignored){
                    
		}
	}
	
	/**
	 * Saves the file
	 */
	
	public void save() {
		try{
			this.fileConfig.save(this.file);
		}catch(Exception ignored) {
			
		}
	}
	
     /**
	 * Gets a ConfigurationSection value from the file.
	 * @param key
	 * @param fallback
	 * @return the ConfigurationSection for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
        
	public ConfigurationSection getConfigurationSection(String key, ConfigurationSection fallback) {
		if(this.fileConfig.contains(key)) {
			return this.fileConfig.getConfigurationSection(key);
		}else{
			return fallback;
		}
	}
        
     /**
	 * Gets the ConfigurationSection in a List<String> value from the file.
	 * @param key
	 * @return the List<String> for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
	
	public ArrayList<String> getSection(String key, ArrayList<String> fallback) {
		if(this.fileConfig.contains(key)) {
			ArrayList<String> section = new ArrayList<>();
			for(Object str : getConfigurationSection(key, null).getKeys(false).toArray()) {
				section.add(String.valueOf(str));
			}
			return section;
		}else{
			return fallback;
		}
	}

	/**
	 * Gets an integer value from the file.
	 * @param key
	 * @param fallback
	 * @return the integer for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
	public int getInteger(String key, int fallback){
		if(this.fileConfig.contains(key)) {
			return this.fileConfig.getInt(key);
		}else{
			return fallback;
		}
	}

	/**
	 * Gets an string value from the file.
	 * @param key
	 * @param fallback
	 * @return the string for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
	public String getString(String key, String fallback){
		if(this.fileConfig.contains(key)) {
			return this.fileConfig.getString(key);
		}else{
			return fallback;
		}
	}
        
        /**
	 * Gets an float value from the file.
	 * @param key
	 * @return whether it exists or not
	 */
	
	public boolean contains(String key) {
		return this.fileConfig.contains(key);
	}

	/**
	 * Gets an boolean value from the file. It will accept "true" and "false".
	 * @param key
	 * @param fallback
	 * @return the boolean for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
	public boolean getBoolean(String key, boolean fallback){
		if(this.fileConfig.contains(key)) {
			return this.fileConfig.getBoolean(key);
		}else{
			return fallback;
		}
	}
	
        /**
	 * Gets a List<String> value from the file.
	 * @param key
	 * @param fallback
	 * @return the List<String> for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
        
	public ArrayList<String> getStringArray(String key, ArrayList<String> fallback) {
		if(this.fileConfig.contains(key)) {
			return (ArrayList<String>)this.fileConfig.getStringList(key);
		}else{
			return fallback;
		}
	}
 
	/**
	 * Gets an double value from the file.
	 * @param key
	 * @param fallback
	 * @return the double for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
	public double getDouble(String key, double fallback){
		if(this.fileConfig.contains(key)) {
			return this.fileConfig.getDouble(key);
		}else{
			return fallback;
		}
	}

        /**
	 * Gets an Object value from the file.
	 * @param key
	 * @param fallback
	 * @return the Object for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
        
	public Object getObject(String key, Object fallback) {
		if(this.fileConfig.contains(key)) {
			return this.fileConfig.get(key);
		}else{
			return fallback;
		}
	}
	/**
	 * Gets an float value from the file.
	 * @param key
	 * @param fallback
	 * @return the float for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
	public float getFloat(String key, float fallback){
		if(this.fileConfig.contains(key)) {
			return (float) this.fileConfig.getDouble(key);
		}else{
			return fallback;
		}
	}

	/**
	 * Gets an material value from the file. It parses material-ids as well as Bukkit-Material names.
	 * @param key
	 * @param fallback
	 * @return the material for the key, if exists.
	 * @return fallback when the key doesn't exist.
	 */
	public Material getMaterial(String key, Material fallback){
		if(this.fileConfig.contains(key)) {
			return this.fileConfig.getItemStack(key).getType();
		}else{
			return fallback;
		}
	}

	/**
	 * When one key exists multiple times, use this method to get all values as strings in a list.
	 * @param key the key to search
	 * @return all values for that key.
	 */

	/**
	 * Writes the keySet to the file
	 */
	
	public void set(String key, Object set) {
		this.fileConfig.set(key, set);
		if(this.saveOnSet) {
			this.save();
		}
	}
}
