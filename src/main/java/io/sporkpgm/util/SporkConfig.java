package io.sporkpgm.util;

import io.sporkpgm.Spork;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SporkConfig {

	public static class Maps {

		public static List<File> getFiles() {
			Config config = Spork.get().getConfig();
			List<File> files = new ArrayList<>();

			String pre = "settings.folders";
			try {
				Set<String> folders = config.getConfigurationSection(pre).getKeys(false);
				if(folders != null) {
					for(String folder : folders) {
						String fetch = pre + "." + folder + ".path";
						String path = config.getString(fetch);
						if(path != null) {
							File file = new File(path);

							if(file.exists()) {
								files.add(file);
							} else {
								Log.info("Could not find a folder specified in the config (" + path + " @ " + fetch + ")");
							}
						} else {
							Log.info("Failed to specify path in Config (" + fetch + ")");
						}
					}
				}
			} catch(NullPointerException e) {
				Log.exception(e);
			}

			return files;
		}

	}

	public static class Settings {

		public static String prefix() {
			Config config = Spork.get().getConfig();
			return config.getString("settings.match.prefix", "match-");
		}

	}

	public static class Rotation {

		public static File rotation() {
			Config config = Spork.get().getConfig();
			return new File(config.getString("settings.rotation.file", "rotation.txt"));
		}

	}

}
