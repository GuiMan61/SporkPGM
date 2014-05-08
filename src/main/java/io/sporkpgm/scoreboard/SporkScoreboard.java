package io.sporkpgm.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public abstract class SporkScoreboard {

	protected ScoreboardHandler handler;
	protected Scoreboard scoreboard;
	protected Objective objective;

	private List<ScoreboardEntry> entries;

	public SporkScoreboard(ScoreboardHandler handler) {
		this.handler = handler;
		this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		this.objective = scoreboard.registerNewObjective("Sidebar", "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objective.setDisplayName(ChatColor.GOLD + "Sidebar");
		this.entries = new ArrayList<>();
		setup();
	}

	public ScoreboardHandler getHandler() {
		return handler;
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public Objective getObjective() {
		return objective;
	}

	public ScoreboardEntry getEntry(String name) {
		for(ScoreboardEntry entry : entries) {
			if(entry.getName().equals(name)) {
				return entry;
			}
		}

		return new ScoreboardEntry(name, this);
	}

	public abstract void setup();

	public abstract void update();

}
