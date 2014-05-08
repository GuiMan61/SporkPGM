package io.sporkpgm.module.modules.team;

import io.sporkpgm.map.SporkMap;

import java.util.ArrayList;
import java.util.List;

public class TeamCollection {

	private SporkMap map;
	private TeamModule observers;
	private List<TeamModule> teams;

	public TeamCollection(SporkMap map, List<TeamModule> teams) {
		this.map = map;
		this.observers = TeamBuilder.observers(map);
		this.teams = new ArrayList<>();
		for(TeamModule team : teams) {
			this.teams.add(team);
		}
	}

	public SporkMap getMap() {
		return map;
	}

	public TeamModule getObservers() {
		return observers;
	}

	public List<TeamModule> getTeams() {
		return teams;
	}

	public List<TeamModule> getTeams(String search) {
		List<TeamModule> test = new ArrayList<>();
		test.addAll(getTeams());
		test.add(getObservers());

		List<TeamModule> teams = new ArrayList<>();
		for(TeamModule team : test) {
			if(!teams.contains(team)) {
				String name = team.getName().toLowerCase();
				String colour = team.getColor().name().replace("_", " ").toLowerCase();
				if(name.equalsIgnoreCase(search) || colour.equalsIgnoreCase(search)) {
					teams.add(team);
				}
			}
		}

		if(teams.size() > 0) {
			return teams;
		}

		for(TeamModule team : test) {
			if(!teams.contains(team)) {
				String name = team.getName().toLowerCase();
				String colour = team.getColor().name().replace("_", " ").toLowerCase();
				if(name.startsWith(search.toLowerCase()) || colour.startsWith(search.toLowerCase())) {
					teams.add(team);
				}
			}
		}

		if(teams.size() > 0) {
			return teams;
		}

		for(TeamModule team : test) {
			if(!teams.contains(team)) {
				String name = team.getName().toLowerCase();
				String colour = team.getColor().name().replace("_", " ").toLowerCase();
				if(name.contains(search.toLowerCase()) || colour.contains(search.toLowerCase())) {
					teams.add(team);
				}
			}
		}

		return teams;
	}

	public TeamModule getTeam(String search) {
		List<TeamModule> teams = getTeams(search);
		return (teams.size() > 0 ? teams.get(0) : null);
	}

}
