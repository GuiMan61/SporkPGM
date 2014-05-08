package io.sporkpgm.map;

import io.sporkpgm.module.modules.filter.FilterCollection;
import io.sporkpgm.match.Match;
import io.sporkpgm.module.ModuleCollection;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderFactory;
import io.sporkpgm.module.modules.info.Contributor;
import io.sporkpgm.module.modules.kits.KitModule;
import io.sporkpgm.module.modules.team.TeamCollection;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.module.modules.region.RegionCollection;
import io.sporkpgm.scoreboard.ScoreboardHandler;
import io.sporkpgm.user.User;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class SporkMap {

	private SporkLoader loader;
	private ModuleCollection modules;

	private RegionCollection regions;
	private FilterCollection filters;
	private TeamCollection teams;
	private ScoreboardHandler scoreboard;

	private World world;

	public SporkMap(SporkLoader loader) {
		this.loader = loader;
		this.modules = loader.getModules().clone(this);
		this.modules.add(BuilderFactory.get().getBuilders(), new BuilderContext(this, loader, loader.getDocument()));
		this.regions = new RegionCollection(this);
		this.filters = new FilterCollection(this);
		this.teams = new TeamCollection(this, modules.getModules(TeamModule.class));
		this.scoreboard = new ScoreboardHandler(this);
	}

	public SporkLoader getLoader() {
		return loader;
	}

	public ModuleCollection getModules() {
		return modules;
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

	public RegionCollection getRegions() {
		return regions;
	}

	public FilterCollection getFilters() {
		return filters;
	}

	public TeamCollection getTeams() {
		return teams;
	}

	public ScoreboardHandler getScoreboard() {
		return scoreboard;
	}

	public List<KitModule> getKits() {
		return modules.getModules(KitModule.class);
	}

	public World getWorld() {
		return world;
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

	public List<User> getPlayers() {
		List<User> users = new ArrayList<>();
		for(TeamModule team : teams.getTeams()) {
			users.addAll(team.getPlayers());
		}
		return users;
	}

}
