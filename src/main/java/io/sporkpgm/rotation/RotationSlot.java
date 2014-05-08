package io.sporkpgm.rotation;

import io.sporkpgm.map.SporkLoader;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.match.Match;

public class RotationSlot {

	private SporkLoader loader;
	private boolean set;

	private SporkMap map;
	private Match match;

	public RotationSlot(SporkLoader loader) {
		this(loader, false);
	}

	public RotationSlot(SporkLoader loader, boolean set) {
		this.loader = loader;
		this.set = set;
	}

	public SporkLoader getLoader() {
		return loader;
	}

	public boolean getSet() {
		return set;
	}

	public SporkMap getMap() {
		return map;
	}

	public Match getMatch() {
		return match;
	}

	public void load() {
		SporkMap map = loader.build();
		Match match = new Match(map, Rotation.get().getID());
		map.load(match);

		this.map = map;
		this.match = match;
	}

	public void unload() {
		this.map.unload(match);
	}

	public static Rotation get() {
		return Rotation.get();
	}

}
