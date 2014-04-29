package io.sporkpgm;

import org.bukkit.plugin.java.JavaPlugin;

public class Spork extends JavaPlugin {

	protected static Spork instance;

	@Override
	public void onEnable() {
		instance = this;
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	public static Spork get() {
		return instance;
	}

}
