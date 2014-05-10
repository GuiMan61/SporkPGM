package io.sporkpgm.util;

import io.sporkpgm.Spork;
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

	public boolean load() {
		try {
			load(file);
			return true;
		} catch(FileNotFoundException ex) {
			/* nothing */
		} catch(IOException | InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		}

		return false;
	}

	public boolean exists() {
		return exists(null);
	}

	public boolean exists(String reason) {
		Log.debug("File does" + (!file.exists() ? "n't" : "") + " exist" + (reason != null ? " (" + reason + ")" : ""));
		return file.exists();
	}

	public void reload() {
		exists("About to load configuration");
		load();
		exists("About to load defaults");
		if(!file.exists()) {
			plugin.saveResource(def, false);
			load();
		}
		exists("Loaded defaults");
	}

	public void save() {
		if(file == null) {
			return;
		}

		try {
			save(file);
		} catch(IOException e) {
			try {
				file.mkdirs();
				file.delete();
				save(file);
			} catch(IOException ex) {
				Log.exception(ex);
				// Log.severe("Could not save config to " + file + " " + ex.getMessage());
			}
		}
	}

	public static Config load(JavaPlugin plugin, File file, String def) {
		Config config = new Config(plugin, file, def);
		config.exists("Just created the Config object");
		config.reload();
		return config;
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
