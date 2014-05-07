package io.sporkpgm.map;

import io.sporkpgm.match.Match;
import io.sporkpgm.module.ModuleCollection;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderFactory;
import io.sporkpgm.module.modules.info.Contributor;
import io.sporkpgm.module.modules.team.TeamCollection;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.scoreboard.ScoreboardHandler;
import io.sporkpgm.user.User;

import java.util.ArrayList;
import java.util.List;

public class SporkMap {

	private SporkLoader loader;
	private TeamCollection teams;

	private ScoreboardHandler scoreboard;
	private ModuleCollection modules;

	public SporkMap(SporkLoader loader) {
		this.loader = loader;
		this.modules = loader.getModules().clone(this);
		this.modules.add(BuilderFactory.get().getBuilders(), new BuilderContext(this, loader, loader.getDocument()));
	}

	public String getName() {
		return loader.getName();
	}

	public String getObjective() {
		return loader.getObjective();
	}

	public List<Contributor> getContributors() {
		return loader.getContributors();
	}

	public List<Contributor> getAuthors() {
		return loader.getAuthors();
	}

	public List<String> getRules() {
		return loader.getRules();
	}

	public String getVersion() {
		return loader.getVersion();
	}

	public ScoreboardHandler getScoreboard() {
		return scoreboard;
	}

	public TeamModule getWinner() {
		return null;
	}

	public boolean load(Match match) {
		return true;
	}

	public boolean unload(Match match) {
		return true;
	}

	public TeamCollection getTeams() {
		return teams;
	}

	public List<User> getPlayers() {
		List<User> users = new ArrayList<>();
		for(TeamModule team : teams.getTeams()) {
			users.addAll(team.getPlayers());
		}
		return users;
	}

}
