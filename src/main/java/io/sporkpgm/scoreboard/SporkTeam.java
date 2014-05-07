package io.sporkpgm.scoreboard;

import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.util.StringUtil;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class SporkTeam {

	private ScoreboardHandler scoreboard;
	private TeamModule module;

	private List<Team> teams;

	public SporkTeam(ScoreboardHandler scoreboard, TeamModule module) {
		this.scoreboard = scoreboard;
		this.module = module;

		this.teams = new ArrayList<>();
		for(SporkScoreboard board : scoreboard.scoreboards) {
			teams.add(board.getScoreboard().registerNewTeam(StringUtil.trim(module.getName(), 16)));
		}
	}

	public void register(SporkScoreboard board) {
		for(Team team : teams) {
			if(team.getScoreboard().equals(board.getScoreboard())) {
				return;
			}
		}

		teams.add(board.getScoreboard().registerNewTeam(StringUtil.trim(module.getName(), 16)));
	}

	public void unregister(SporkScoreboard board) {
		List<Team> replace = new ArrayList<>();
		for(Team team : teams) {
			if(!team.getScoreboard().equals(board.getScoreboard())) {
				replace.add(team);
			}
		}

		this.teams = replace;
	}

}
