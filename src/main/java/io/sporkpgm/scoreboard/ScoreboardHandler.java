package io.sporkpgm.scoreboard;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.scoreboard.exceptions.IllegalScoreboardException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardHandler {

	protected SporkMap map;
	protected List<SporkScoreboard> scoreboards;
	protected List<SporkTeam> teams;

	public ScoreboardHandler(SporkMap map) {
		this.map = map;
		this.scoreboards = new ArrayList<>();
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
			return (S) constructor.newInstance(this);
		} catch(Exception e) {
			throw new IllegalScoreboardException(e);
		}
	}

	public SporkTeam register(TeamModule module) {
		try {
			return new SporkTeam(this, module);
		} catch(Exception e) {

		}
	}

}
