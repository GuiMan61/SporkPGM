package io.sporkpgm.map;

import com.google.common.collect.Lists;
import io.sporkpgm.Spork;
import io.sporkpgm.map.exceptions.MapLoadException;
import io.sporkpgm.util.Log;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class SporkFactory {

	private static SporkFactory factory;

	private List<SporkLoader> loaders;

	public SporkFactory() {
		factory = this;
		this.loaders = new ArrayList<>();
	}

	public List<SporkLoader> getLoaders() {
		return loaders;
	}

	public static List<SporkLoader> register(File folder) {
		List<SporkLoader> loaders = new ArrayList<>();

		boolean map = isMap(folder);
		if(!map) {
			File[] files = folder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});

			for(File file : files) {
				loaders.addAll(register(file));
			}
		} else {
			try {
				SporkLoader loader = new SporkLoader(folder);
				if(getMap(loader.getName(), "name") == null) {
					loaders.add(loader);
				} else {
					Log.info("Already loaded " + loader.getName());
				}
			} catch(MapLoadException e) {
				if(Spork.isDebug()) {
					e.printStackTrace();
				} else {
					File root = Spork.getRoot();
					String relative = root.toURI().relativize(folder.toURI()).getPath();
					Log.info("Failed to load map (" + relative + ") - threw " + e.getClass().getSimpleName() + " (" + e.getMessage() + ")");
				}
			}
		}

		factory.loaders.addAll(loaders);
		return loaders;
	}

	public static boolean isMap(File folder) {
		return new File(folder, "map.xml").isFile() && new File(folder, "level.dat").isFile() && new File(folder, "region").isDirectory();
	}

	public static SporkLoader getMap(String string) {
		return getMap(string, "name", "folder", "starts with", "contains");
	}

	public static SporkLoader getMap(String string, String... check) {
		List<String> names = Lists.newArrayList(check);
		List<SporkLoader> loaders = factory.loaders;

		if(names.contains("name")) {
			for(SporkLoader loader : loaders) {
				if(loader.getName().equalsIgnoreCase(string)) {
					return loader;
				}
			}
		}

		if(names.contains("folder")) {
			for(SporkLoader loader : loaders) {
				if(loader.getFolder().getName().equalsIgnoreCase(string)) {
					return loader;
				}
			}
		}

		if(names.contains("starts with")) {
			for(SporkLoader loader : loaders) {
				if(loader.getName().toLowerCase().startsWith(string.toLowerCase())) {
					return loader;
				}
			}
		}

		if(names.contains("contains")) {
			for(SporkLoader loader : loaders) {
				if(loader.getName().toLowerCase().contains(string.toLowerCase())) {
					return loader;
				}
			}
		}

		return null;
	}

}
