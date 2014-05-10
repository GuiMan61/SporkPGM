package io.sporkpgm.module.modules.team;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.modules.spawn.SpawnModule;
import io.sporkpgm.scoreboard.SporkTeam;
import io.sporkpgm.user.User;
import io.sporkpgm.util.OtherUtil;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(description = "Handles the teams that the players can join", builder = TeamBuilder.class)
public class TeamModule extends Module {

	private SporkMap map;
	private SporkTeam team;

	private List<SpawnModule> spawns;
	private String name;
	private ChatColor color;
	private ChatColor overhead;
	private int max;
	private int overfill;
	private boolean observers;
	private boolean capped;
	private boolean closed;
	private boolean ready;

	TeamModule(SporkMap map, String name, ChatColor color) {
		this(map, name, color, color, 0, 0, true);
	}

	TeamModule(SporkMap map, String name, ChatColor color, int max) {
		this(map, name, color, color, max, max + max / 4, false);
	}

	TeamModule(SporkMap map, String name, ChatColor color, int max, int overfill) {
		this(map, name, color, color, max, overfill, false);
	}

	TeamModule(SporkMap map, String name, ChatColor color, ChatColor overhead, int max, int overfill, boolean observers) {
		this.map = map;
		this.spawns = new ArrayList<>();
		this.name = name;
		this.color = color;
		this.overhead = overhead;
		this.max = max;
		this.overfill = overfill;
		this.observers = observers;
		this.capped = true;
		this.closed = false;
	}

	public SporkMap getMap() {
		return map;
	}

	public List<SpawnModule> getSpawns() {
		return spawns;
	}

	public SpawnModule getSpawn() {
		return OtherUtil.getRandom(getSpawns());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if(team != null) {
			team.setName(name);
		}
	}

	public ChatColor getColor() {
		return color;
	}

	public ChatColor getOverhead() {
		return overhead;
	}

	public int getMax() {
		return max;
	}

	public int getOverfill() {
		return overfill;
	}

	public boolean isObservers() {
		return observers;
	}

	public boolean isCapped() {
		return capped;
	}

	public void setCapped(boolean capped) {
		this.capped = capped;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public String getColoredName() {
		return getColor() + getName();
	}

	public List<User> getPlayers() {
		List<User> users = new ArrayList<>();
		for(User user : User.getUsers()) {
			if(user.getTeam().equals(this)) {
				users.add(user);
			}
		}
		return users;
	}

	/*
	public ScoredObjective getScored() {
		return scored;
	}

	public void setScored(ScoredObjective objective) {
		this.scored = objective;
	}
	*/

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public SporkTeam getTeam() {
		return team;
	}

	public void setTeam(SporkTeam team) {
		this.team = team;
	}

	public TeamModule getOpposite() {
		for(TeamModule team : map.getTeams().getTeams()) {
			if(!team.equals(this) && !team.isObservers()) {
				return team;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return "TeamModule{name=" + name + ",color=" + color.name() + ",overhead=" + overhead.name() + ",max=" + max + ",overfill=" + overfill + "}";
	}

}
