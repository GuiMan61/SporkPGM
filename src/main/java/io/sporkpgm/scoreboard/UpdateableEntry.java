package io.sporkpgm.scoreboard;

public abstract class UpdateableEntry extends ScoreboardEntry {

	public UpdateableEntry(String name, SporkScoreboard scoreboard) {
		super(name, scoreboard);
	}

	public void update() {
		update(false);
	}

	public abstract void update(boolean force);

}
