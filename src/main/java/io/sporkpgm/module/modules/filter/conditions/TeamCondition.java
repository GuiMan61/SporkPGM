package io.sporkpgm.module.modules.filter.conditions;

import io.sporkpgm.module.modules.filter.Filter;
import io.sporkpgm.module.modules.filter.other.Context;
import io.sporkpgm.module.modules.filter.other.State;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.user.User;

import static io.sporkpgm.module.modules.filter.other.State.*;

public class TeamCondition extends Filter {

	private TeamModule team;

	public TeamCondition(String name, State state) {
		super(name, state);
	}

	public TeamCondition(String name, State state, TeamModule team) {
		super(name, state);
		this.team = team;
	}

	public State filter(Context context) {
		if(!context.hasPlayer()) {
			return ABSTAIN;
		}

		User player = context.getPlayer();
		if(team == null) {
			return DENY;
		} else if(!player.getTeam().equals(team)) {
			return DENY;
		} else if(player.getTeam().equals(team)) {
			return ALLOW;
		}

		return ABSTAIN;
	}

}