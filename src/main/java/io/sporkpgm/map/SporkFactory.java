package io.sporkpgm.map;

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

		}

		return loaders;
	}

	public static boolean isMap(File folder) {
		return new File(folder, "map.xml").isFile() && new File(folder, "level.dat").isFile() && new File(folder, "region").isDirectory();
	}

}
