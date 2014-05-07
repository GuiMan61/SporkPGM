package io.sporkpgm.scoreboard;

import io.sporkpgm.module.modules.team.TeamModule;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

public abstract class SporkScoreboard {

	protected Scoreboard scoreboard;

	public SporkScoreboard() {
		this.scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
	}

	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	public abstract void setup();

	public SporkTeam register(TeamModule module) {
		return new SporkTeam(this, module);
	}

}
