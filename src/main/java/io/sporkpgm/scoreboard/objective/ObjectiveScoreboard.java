package io.sporkpgm.scoreboard.objective;

import com.google.common.collect.Lists;
import io.sporkpgm.module.ObjectiveModule;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.scoreboard.ScoreboardEntry;
import io.sporkpgm.scoreboard.ScoreboardHandler;
import io.sporkpgm.scoreboard.SporkScoreboard;
import io.sporkpgm.scoreboard.UpdateableEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ObjectiveScoreboard extends SporkScoreboard {

	private List<ObjectiveModule> objectives;
	private Map<TeamModule, List<ObjectiveModule>> teams;

	public ObjectiveScoreboard(ScoreboardHandler handler) {
		super(handler);
	}

	public void setup() {
		this.objectives = handler.getMap().getModules().getModules(ObjectiveModule.class);
		this.teams = new HashMap<>();

		for(ObjectiveModule objective : objectives) {
			if(teams.get(objective.getTeam()) == null) {
				teams.put(objective.getTeam(), new ArrayList<ObjectiveModule>());
			}

			teams.get(objective.getTeam()).add(objective);
		}

		int score = 1;
		List<Entry<TeamModule, List<ObjectiveModule>>> entries = Lists.newArrayList(teams.entrySet());
		for(int i = 0; i < teams.size(); i++) {
			Entry<TeamModule, List<ObjectiveModule>> entry = entries.get(i);
			TeamModule team = entry.getKey();
			List<ObjectiveModule> objectives = entry.getValue();

			if(objectives.size() > 0) {
				if(i > 0) {
					blank(score);
					score++;
				}

				getTeam(team).setValue(score);
				score++;

				for(ObjectiveModule objective : objectives) {
					getObjective(objective).setValue(score);
					score++;
				}
			}
		}
	}

	public void update() {
		for(ScoreboardEntry entry : entries) {
			if(entry instanceof UpdateableEntry) {
				((UpdateableEntry) entry).update();
			}
		}
	}

}
