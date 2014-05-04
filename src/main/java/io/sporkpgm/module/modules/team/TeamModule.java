package io.sporkpgm.module.modules.team;

import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.user.User;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.ScoreboardUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(description = "Handles the teams that the players can join", builder = TeamBuilder.class)
public class TeamModule extends Module {

	private SporkMap map;
	private Team team;
	// private ScoredObjective scored;

	private Team scoreboard;
	private OfflinePlayer player;

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
		this.name = name;
		this.color = color;
		this.overhead = overhead;
		this.max = max;
		this.overfill = overfill;
		this.observers = observers;
		this.capped = true;
		this.closed = false;
		this.team = map.getScoreboard().registerNewTeam(name);
		this.team.setPrefix(getColor().toString());
		this.team.setDisplayName(getColoredName());
		this.team.setCanSeeFriendlyInvisibles(true);

		name();
	}

	public SporkMap getMap() {
		return map;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		update();
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

	public Team getTeam() {
		return team;
	}

	public OfflinePlayer getPlayer() {
		return player;
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

	public void update() {
		Objective objective = team.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		Score score = objective.getScore(player);
		int value = score.getScore();

		name();

		Score newScore = objective.getScore(player);
		ScoreboardUtil.reset(score);
		newScore.setScore(value);
	}

	public void name() {
		String original = getColoredName();
		String sb = getName();

		String prefix = "";
		String title = getColoredName();
		String suffix = "";

		if(sb.length() > 12) {
			sb = sb.substring(0, 12);
		}

		if(title.length() > 32) {
			prefix = original.substring(0, 16);
			title = original.substring(16, 32);
			Log.info(original + ": " + original.length());
			suffix = original.substring(32, (original.length() > 48 ? 48 : original.length()));
		} else if(title.length() > 16) {
			prefix = original.substring(0, 16);
			title = original.substring(16, original.length());
		}

		player = Spork.get().getServer().getOfflinePlayer(title);

		if(scoreboard == null) {
			scoreboard = map.getScoreboard().registerNewTeam(sb + "-obj");
		}

		scoreboard.setPrefix(prefix);
		scoreboard.setDisplayName(title);
		scoreboard.setSuffix(suffix);

		if(!scoreboard.hasPlayer(player)) {
			scoreboard.addPlayer(player);
		}
	}

	@Override
	public String toString() {
		return "TeamModule{name=" + name + ",color=" + color.name() + ",overhead=" + overhead.name() + ",max=" + max + ",overfill=" + overfill + "}";
	}

}
