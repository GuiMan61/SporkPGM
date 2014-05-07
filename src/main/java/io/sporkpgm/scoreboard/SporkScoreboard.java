package io.sporkpgm.scoreboard;

import io.sporkpgm.module.modules.team.TeamModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class SporkScoreboard {

	protected ScoreboardHandler handler;
	protected Scoreboard scoreboard;
	protected Objective objective;

	public SporkScoreboard(ScoreboardHandler handler) {
		this.handler = handler;
		this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		this.objective = scoreboard.registerNewObjective("Sidebar", "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.objective.setDisplayName(ChatColor.GOLD + "Sidebar");
		setup();
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public abstract void setup();

	public abstract void update();

	public SporkTeam register(TeamModule module) {
		
	}

}
