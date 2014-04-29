package io.sporkpgm.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class Config extends YamlConfiguration {

	private JavaPlugin plugin;
	private File file;
	private String def;

	private Config(JavaPlugin plugin, File file, String def) {
		this.plugin = plugin;
		this.file = file;
		this.def = def;
	}

	public FileConfiguration getConfig() {
		return this;
	}

	public void reload() {
		try {
			load(file);
		} catch(FileNotFoundException ex) {
			/* nothing */
		} catch(IOException | InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}

		Log.info("Loading defaults from " + def);

		// Look for defaults in the jar
		InputStream defConfigStream = plugin.getResource(def);
		if(defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			setDefaults(defConfig);
			save();
		}
	}

	public void save() {
		if(file == null) {
			return;
		}

		try {
			file.mkdirs();
			save(file);
		} catch(IOException ex) {
			Log.severe("Could not save config to " + file + " " + ex.getMessage());
		}
	}

	public static Config load(JavaPlugin plugin, File file, String def) {
		try {
			Config config = new Config(plugin, file, def);
			config.load(file);
			config.reload();
			return config;
		} catch(FileNotFoundException ex) {
			file.mkdirs();
			try {
				file.delete();
				boolean created = file.createNewFile();
				if(!created) {
					Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file + " because an empty file could not be created");
					return null;
				}

				return load(plugin, file, def);
			} catch(IOException e) {
				Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
			}
		} catch(IOException | InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}

		return null;
	}

	public static Config load(JavaPlugin plugin, File file) {
		return load(plugin, file, file.getName());
	}

	public static Config load(JavaPlugin plugin, String name, String def) {
		return load(plugin, new File(plugin.getDataFolder(), name), def);
	}

	public static Config load(JavaPlugin plugin, String name) {
		return load(plugin, new File(plugin.getDataFolder(), name));
	}

}
