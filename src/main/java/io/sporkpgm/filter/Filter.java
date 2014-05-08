package io.sporkpgm.filter;

import io.sporkpgm.filter.other.Context;
import io.sporkpgm.filter.other.State;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;

import static io.sporkpgm.filter.other.State.*;

@ModuleInfo(description = "Filters different events", builder = FilterBuilder.class)
public abstract class Filter extends Module {

	protected String name;
	protected State state;

	protected Filter(String name, State state) {
		this.name = name;
		this.state = state;
	}

	protected abstract State filter(Context context);

	public State result(Context context) {
		if(state == DENY) {
			return filter(context).reverse();
		} else if(state == ALLOW) {
			return filter(context);
		}

		return ABSTAIN;
	}

	public String getName() {
		return name;
	}

	public State getState() {
		return state;
	}

}
