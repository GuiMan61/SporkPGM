package io.sporkpgm;

import io.sporkpgm.map.MapBuilder;
import io.sporkpgm.map.MapLoader;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleBuilder;
import io.sporkpgm.util.Config;
import io.sporkpgm.util.Config.Map;
import io.sporkpgm.util.Log;
import org.bukkit.plugin.java.JavaPlugin;
import org.dom4j.Document;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static io.sporkpgm.Spork.StartupType.*;

public class Spork extends JavaPlugin {

	protected static Spork spork;

	protected List<Class<? extends ModuleBuilder>> builders;
	protected List<MapBuilder> maps;
	protected File repository;

	@Override
	public void onEnable() {
		spork = this;
		saveDefaultConfig();

		Config.init();
		Log.setDebugging(Config.General.DEBUG);

		if(getConfig().getString("settings.maps.repository") == null) {
			getConfig().set("settings.maps.repository", "maps/");
		}

		repository = Map.DIRECTORY;
		maps = new ArrayList<>();

		StartupType type = Map.STARTUP;

		List<MapBuilder> load = MapLoader.build(repository);
		if(type == SPECIFIED) {
			load.clear();
			List<String> specified = getConfig().getStringList("settings.maps.load");
			if(specified != null) {
				for(MapBuilder map : MapLoader.build(repository)) {
					if(contains(specified, "map name")) {
						load.add(map);
					}
				}
			}
		}

		if(load.isEmpty() && type != ALL) {

		}
	}

	private boolean contains(List<String> strings, String search) {
		for(String string : strings) {
			if(string.equalsIgnoreCase(search)) {
				return true;
			}
		}

		return false;
	}

	public List<Module> getModules(Document document) {
		return getModules(document, builders);
	}

	public List<Module> getModules(Document document, List<Class<? extends ModuleBuilder>> builders) {
		List<Module> modules = new ArrayList<>();

		for(Class<? extends ModuleBuilder> clazz : builders) {
			try {
				Constructor constructor = clazz.getConstructor(SporkMap.class);
				constructor.setAccessible(true);
				ModuleBuilder builder = (ModuleBuilder) constructor.newInstance(document);
				modules.addAll(builder.build());
			} catch(Exception e) {
				getLogger().warning("Error when loading '" + clazz.getSimpleName() + "' due to " + e.getClass().getSimpleName());
				continue;
			}
		}

		return modules;
	}

	public List<Class<? extends ModuleBuilder>> getBuilders() {
		return builders;
	}

	protected void builders() {
		builders = new ArrayList<>();
	}

	public static Spork get() {
		return spork;
	}

	public enum StartupType {

		ALL(new String[]{"all"}),
		SPECIFIED(new String[]{"specified", "specify", "listed"});

		String[] names;

		StartupType(String[] names) {
			this.names = names;
		}

		public String[] getNames() {
			return names;
		}

		public boolean matches(String name) {
			for(String value : names) {
				if(value.equalsIgnoreCase(name)) {
					return true;
				}
			}

			return false;
		}

		public static StartupType getType(String name) {
			if(name != null) {
				for(StartupType type : values()) {
					if(type.matches(name)) {
						return type;
					}
				}
			}

			return ALL;
		}

	}

}
