package io.sporkpgm.module.modules.filter.conditions;

import io.sporkpgm.event.map.BlockChangeEvent;
import io.sporkpgm.module.modules.filter.Filter;
import io.sporkpgm.module.modules.filter.other.Context;
import io.sporkpgm.module.modules.filter.other.State;
import org.bukkit.Material;

import static io.sporkpgm.module.modules.filter.other.State.*;

public class BlockCondition extends Filter {

	Material material;

	public BlockCondition(String name, State state) {
		super(name, state);
	}

	public BlockCondition(String name, State state, Material material) {
		super(name, state);
		this.material = material;
	}

	public State filter(Context context) {
		if(!context.hasTransformation() && !context.hasBlock()) {
			return ABSTAIN;
		}

		BlockChangeEvent event = (context.hasBlock() ? context.getBlock() : context.getTransformation());
		boolean match = event.getNewState().getType().equals(material);

		if(material == null) {
			return ALLOW;
		} else if(!match) {
			return ALLOW;
		} else if(match) {
			return DENY;
		}

		return ABSTAIN;
	}

}