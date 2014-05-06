package io.sporkpgm.win;

import java.util.ArrayList;
import java.util.List;

public class WinConditions implements WinCondition {

	protected List<WinCondition> conditions;

	public WinConditions() {
		this.conditions = new ArrayList<>();
	}

	public boolean complete() {
		if(conditions.size() == 0) {
			return false;
		}

		for(WinCondition condition : conditions) {
			if(!condition.complete()) {
				return false;
			}
		}

		return true;
	}

}
