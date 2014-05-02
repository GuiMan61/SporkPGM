package io.sporkpgm.map;

import io.sporkpgm.module.ModuleCollection;
import io.sporkpgm.module.modules.team.TeamCollection;
import org.bukkit.scoreboard.Scoreboard;

public class SporkMap {

	private Scoreboard scoreboard;
	private TeamCollection teams;

	private ModuleCollection modules;

	public SporkMap(SporkLoader loader) {
		this.modules = loader.getModules().clone(this);
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public TeamCollection getTeams() {
		return teams;
	}

}
