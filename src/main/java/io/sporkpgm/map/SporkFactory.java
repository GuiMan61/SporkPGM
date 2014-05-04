package io.sporkpgm.map;

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
				loaders.add(new SporkLoader(folder));
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

		factory.loaders = loaders;
		return loaders;
	}

	public static boolean isMap(File folder) {
		return new File(folder, "map.xml").isFile() && new File(folder, "level.dat").isFile() && new File(folder, "region").isDirectory();
	}

}
