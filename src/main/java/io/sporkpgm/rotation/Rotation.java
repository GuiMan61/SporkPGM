package io.sporkpgm.rotation;

import io.sporkpgm.map.SporkLoader;

import java.util.ArrayList;
import java.util.List;

public class Rotation {

	private static Rotation instance;

	private int match = 1;
	private List<RotationSet> rotations;
	private int rotation = 0;
	private boolean restart;

	public Rotation(List<SporkLoader> maps) {
		instance = this;

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

}
