package io.sporkpgm.scoreboard;

import com.google.common.base.Preconditions;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.ScoreboardUtil;
import io.sporkpgm.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.lang.reflect.Method;

public class ScoreboardEntry {

	private boolean active = true;

	private String name;
	private SporkScoreboard scoreboard;
	private Score score;

	public ScoreboardEntry(String name, SporkScoreboard scoreboard) {
		this.name = name;
		this.scoreboard = scoreboard;

		Objective objective = scoreboard.getObjective();
		String[] split = StringUtil.trim(name, 48, 3);

		try {
			Method getScore = Objective.class.getDeclaredMethod("getScore", String.class);
			getScore.setAccessible(true);
			this.score = (Score) getScore.invoke(objective, split[1]);
		} catch(Exception e) {
			Log.exception(e);
		}

		if(score == null) {
			try {
				this.score = objective.getScore(Bukkit.getOfflinePlayer(split[1]));
			} catch(Exception e) {
				Log.exception(e);
			}
		}

		Preconditions.checkState(score != null, "Unable to create a Score for " + scoreboard);
	}

	public String getName() {
		return name;
	}

	public SporkScoreboard getScoreboard() {
		return scoreboard;
	}

	public Score getScore() {
		return score;
	}

	public int getValue() {
		Preconditions.checkState(active, "Scoreboard Entry is inactive");
		return score.getScore();
	}

	public void setValue(int value) {
		Preconditions.checkState(active, "Scoreboard Entry is inactive");
		this.score.setScore(value);
	}

	public void setValue(int... scores) {
		for(int score : scores) {
			setValue(score);
		}
	}

	public void remove() {
		this.active = false;
		ScoreboardUtil.reset(score);
	}

	public boolean isSet() {
		return ScoreboardUtil.isSet(score);
	}

}
