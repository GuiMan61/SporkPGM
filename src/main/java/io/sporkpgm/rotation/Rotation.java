package io.sporkpgm.rotation;

import com.google.common.base.Charsets;
import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkFactory;
import io.sporkpgm.map.SporkLoader;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.match.Match;
import io.sporkpgm.rotation.exceptions.RotationLoadException;
import io.sporkpgm.util.Config;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.SporkConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Rotation {

	private static int attempts = 0;
	private static int maxAttempts = 5;
	private static Rotation instance;

	private int match = 1;
	private List<RotationSet> rotations;
	private int rotation = 0;
	private boolean restart;

	public Rotation(List<SporkLoader> maps) {
		instance = this;
		rotations = new ArrayList<>();

		int i = 0;
		while(i < 30) {
			List<RotationSlot> slots = new ArrayList<>();

			for(SporkLoader loader : maps) {
				if(i >= 30) {
					break;
				}

				slots.add(new RotationSlot(loader));
				i++;
			}

			rotations.add(new RotationSet(slots));
		}
	}

	public void start() {
		getCurrent().load();
	}

	public int getID() {
		return match;
	}

	public RotationSet getRotation() {
		return getRotation(rotation);
	}

	public RotationSet getRotation(int id) {
		return rotations.get(id);
	}

	public RotationSlot setNext(SporkLoader loader, boolean force) {
		return getRotation().setNext(loader, force);
	}

	public RotationSlot getCurrent() {
		return getRotation().getCurrent();
	}

	public RotationSlot getNext() {
		RotationSlot next = getRotation().getNext();
		if(next != null) {
			return next;
		}

		RotationSet set;
		try {
			set = getRotation(rotation + 1);
		} catch(IndexOutOfBoundsException e) {
			return null;
		}

		return set.getCurrent();
	}

	public boolean isRestarting() {
		return restart || getNext() == null;
	}

	public void cycle() { /* complete later */ }

	public static Rotation get() {
		return instance;
	}

	public static RotationSlot getSlot() {
		return get().getCurrent();
	}

	public static SporkMap getMap() {
		return getSlot().getMap();
	}

	public static Match getMatch() {
		return getSlot().getMatch();
	}

	public static Rotation provide() throws RotationLoadException, IOException {
		File rotation = SporkConfig.Rotation.rotation();
		if(rotation.isDirectory()) {
			throw new RotationLoadException("Unable to parse '" + rotation.getPath() + "' because it is a directory.");
		}

		if(!rotation.exists()) {
			create(rotation);
		}

		List<String> lines = Files.readAllLines(rotation.toPath(), Charsets.UTF_8);
		List<SporkLoader> loaders = new ArrayList<>();
		for(String rawLine : lines) {
			SporkLoader map = SporkFactory.getMap(rawLine);
			if(map == null) {
				Log.warning("Failed to find a map for '" + rawLine + "' in the rotation file");
				continue;
			}

			loaders.add(map);
		}

		if(loaders.size() == 0) {
			if(lines.size() == 0) {
				Log.warning("Creating a new rotation.txt because the old one was empty");
			} else {
				Log.warning("Creating a new rotation.txt because the old one had no valid entries");
			}

			rotation.delete();
			create(rotation);
			attempts++;
			if(attempts > maxAttempts) {
				throw new RotationLoadException("Attempted to create the Rotation 5 times and failed every time");
			}
			return provide();
		}

		return new Rotation(loaders);
	}

	private static void create(File rotation) throws IOException {
		FileWriter write = new FileWriter(rotation, false);
		PrintWriter printer = new PrintWriter(write);
		for(SporkLoader loader : SporkFactory.getMaps()) {
			Log.info("Printing out " + loader.getName() + " into the Rotation file");
			printer.printf("%s" + "%n", loader.getName());
		}

		printer.close();
	}

}
