package io.sporkpgm.scoreboard;

import io.sporkpgm.module.modules.team.TeamModule;

import static io.sporkpgm.util.Chars.CROSS;
import static io.sporkpgm.util.Chars.TICK;
import static org.bukkit.ChatColor.*;

public class TeamEntry extends UpdateableEntry {

	protected String current;
	protected TeamModule team;

	public TeamEntry(TeamModule team, SporkScoreboard scoreboard) {
		super(scoreboard.getEntry(team.getColoredName(), true).getName(), scoreboard);

		ScoreboardEntry entry = scoreboard.getEntry(name, true);
		this.current = team.getName();
		scoreboard.getEntries().remove(entry);

		this.team = team;
		update(true);
	}

	public TeamModule getTeam() {
		return team;
	}

	public void update(boolean force) {
		if(current.equals(team.getName()) && !force) {
			return;
		}

		String name = team.getColoredName();
		ScoreboardEntry entry = scoreboard.getEntry(name, true);
		name = entry.getName();
		scoreboard.getEntries().remove(entry);

		update(name);
	}

}
