package io.sporkpgm.scoreboard;

import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.util.StringUtil;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class SporkTeam {

	protected ScoreboardHandler scoreboard;
	protected TeamModule module;

	protected List<Team> teams;
	protected String[] name;

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

	public void setName(String name) {
		this.name = StringUtil.trim(name, 48, 3);
	}

}
