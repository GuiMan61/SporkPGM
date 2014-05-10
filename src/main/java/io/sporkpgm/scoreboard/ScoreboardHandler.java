package io.sporkpgm.scoreboard;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.scoreboard.exceptions.IllegalScoreboardException;
import io.sporkpgm.util.Log;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardHandler {

	protected SporkMap map;

	protected SporkScoreboard main;
	protected List<SporkScoreboard> scoreboards;
	protected List<SporkTeam> teams;

	public ScoreboardHandler(SporkMap map) {
		this.map = map;
		this.scoreboards = new ArrayList<>();
		this.teams = new ArrayList<>();
	}

	public SporkScoreboard getMain() {
		return (main != null ? main : scoreboards.get(0));
	}

	public void setMain(SporkScoreboard main) {
		this.main = main;
	}

	public <S> S get(Class<S> type) throws IllegalScoreboardException {
		if(!SporkScoreboard.class.isAssignableFrom(type)) {
			throw new IllegalScoreboardException(type);
		}

		for(SporkScoreboard scoreboard : scoreboards) {
			try {
				type.cast(scoreboard);
				return (S) scoreboard;
			} catch(ClassCastException e) { /* nothing */ }
		}

		try {
			Constructor constructor = type.getConstructor(ScoreboardHandler.class);
			constructor.setAccessible(true);

			SporkScoreboard scoreboard = (SporkScoreboard) constructor.newInstance(this);
			for(SporkTeam team : teams) {
				team.register(scoreboard);
			}

			return (S) scoreboard;
		} catch(Exception e) {
			throw new IllegalScoreboardException(e);
		}
	}

	public SporkTeam register(TeamModule module) {
		for(SporkTeam team : teams) {
			if(team.module.equals(module)) {
				Log.debug("SporkTeam already exists for " + module.getName());
				module.setTeam(team);
				return team;
			}
		}

		try {
			SporkTeam team = new SporkTeam(this, module);

			module.setTeam(team);
			this.teams.add(team);
			for(SporkScoreboard scoreboard : scoreboards) {
				team.register(scoreboard);
			}

			return team;
		} catch(Exception e) {
			Log.exception(e);
			return null;
		}
	}

}
