package io.sporkpgm.module;

import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.scoreboard.objective.TeamObjective;

public abstract class ObjectiveModule implements TeamObjective {

	protected String name;
	protected TeamModule team;

	public ObjectiveModule(String name, TeamModule team) {
		this.name = name;
		this.team = team;
	}

	public String getDisplay() {
		return name;
	}

	public TeamModule getTeam() {
		return team;
	}

}
