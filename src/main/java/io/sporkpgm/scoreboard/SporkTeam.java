package io.sporkpgm.scoreboard;

import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.util.StringUtil;
import org.bukkit.scoreboard.Team;

public class SporkTeam {

	private SporkScoreboard scoreboard;
	private Team team;
	private TeamModule module;

	public SporkTeam(SporkScoreboard scoreboard, TeamModule module) {
		this.scoreboard = scoreboard;
		this.module = module;
		this.scoreboard.getScoreboard().registerNewTeam(StringUtil.trim(module.getName(), 16));
	}

}
