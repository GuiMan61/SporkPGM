package io.sporkpgm.rotation;

import io.sporkpgm.map.SporkLoader;
import io.sporkpgm.match.MatchPhase;

import java.util.List;

public class RotationSet {

	private int current = 0;
	private List<RotationSlot> slots;

	public RotationSet(List<RotationSlot> slots) {
		this.slots = slots;
	}

	public RotationSlot getCurrent() {
		return slots.get(current);
	}

	public boolean isEnded() {
		return current + 1 >= slots.size();
	}

	public boolean cycle() {
		if(isEnded()) {
			return false;
		}

		current++;
		return true;
	}

	public RotationSlot getNext() {
		if(isEnded()) {
			return null;
		}

		RotationSlot slot = slots.get(current + 1);
		return slot;
	}

	public RotationSlot setNext(SporkLoader loader, boolean force) {
		if(isEnded() && !force) {
			return null;
		}

		RotationSlot slot = slots.get(current + 1);
		if(slot.getSet()) {
			slot.unload();
			slots.remove(slot);
		}

		slot = new RotationSlot(loader, true);
		slots.add(current + 1, slot);
		if(Rotation.get().getRotation().getCurrent().getMatch().getPhase() == MatchPhase.CYCLING) {
			slot.load();
		}

		return slot;
	}

}
