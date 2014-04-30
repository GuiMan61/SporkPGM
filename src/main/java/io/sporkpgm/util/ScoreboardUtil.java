package io.sporkpgm.util;

import org.bukkit.scoreboard.Score;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ScoreboardUtil {

	private static Class<?> CRAFT_SCORE = NMSUtil.getClassBukkit("scoreboard.CraftScore");
	private static Class<?> CRAFT_OBJECTIVE = NMSUtil.getClassBukkit("scoreboard.CraftObjective");
	private static Class<?> CRAFT_SCOREBOARD = NMSUtil.getClassBukkit("scoreboard.CraftScoreboard");
	private static Class<?> CRAFT_SCOREBOARD_COMPONENT = NMSUtil.getClassBukkit("scoreboard.CraftScoreboardComponent");
	private static Class<?> SCOREBOARD = NMSUtil.getClassNMS("Scoreboard");
	private static Class<?> SCOREBOARD_SCORE = NMSUtil.getClassNMS("ScoreboardScore");
	private static Class<?> SCOREBOARD_OBJECTIVE = NMSUtil.getClassNMS("ScoreboardObjective");

	public static boolean isSet(Score score) {
		try {
			return isSetException(score);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean isSetException(Score score) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
		Object craftScore = CRAFT_SCORE.cast(score);

		Object craftObjective = CRAFT_OBJECTIVE.cast(score.getObjective());
		Method craftHandle = CRAFT_OBJECTIVE.getDeclaredMethod("getHandle");
		craftHandle.setAccessible(true);
		Object craftObjectiveHandle = craftHandle.invoke(craftObjective);

		Field objective = CRAFT_SCORE.getDeclaredField("objective");
		objective.setAccessible(true);
		Object craftScoreboard = checkState(objective.get(craftScore));

		Field craftBoard = CRAFT_SCOREBOARD.getDeclaredField("board");
		craftBoard.setAccessible(true);
		Object scoreboard = craftBoard.get(craftScoreboard);
		Method playerObjectives = SCOREBOARD.getDeclaredMethod("getPlayerObjectives", String.class);
		playerObjectives.setAccessible(true);

		Field playerField = CRAFT_SCORE.getDeclaredField("playerName");
		playerField.setAccessible(true);
		String playerName = (String) playerField.get(craftScore);
		Map map = (Map) playerObjectives.invoke(scoreboard, playerName);

		// return objective.checkState().board.getPlayerObjectives(playerName).containsKey(objective.getHandle());
		return map.containsKey(craftObjectiveHandle);
	}

	public static void reset(Score score) {
		try {
			resetException(score);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void resetException(Score score) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
		Object craftScore = CRAFT_SCORE.cast(score);

		Object craftObjective = CRAFT_OBJECTIVE.cast(score.getObjective());
		Method craftHandle = CRAFT_OBJECTIVE.getDeclaredMethod("getHandle");
		craftHandle.setAccessible(true);
		Object craftObjectiveHandle = craftHandle.invoke(craftObjective);

		Field objective = CRAFT_SCORE.getDeclaredField("objective");
		objective.setAccessible(true);
		Object craftScoreboard = checkState(objective.get(craftScore));

			/*
			Method checkState = CRAFT_OBJECTIVE.getDeclaredMethod("checkState");
			checkState.setAccessible(true);
			craftScoreboard = checkState.invoke(CRAFT_SCORE.getDeclaredField("objective").get(craftScore));
			*/

		Field scoreboardField = CRAFT_SCOREBOARD.getDeclaredField("board");
		scoreboardField.setAccessible(true);
		Object scoreboard = scoreboardField.get(craftScoreboard);
		Method playerObjectives = SCOREBOARD.getDeclaredMethod("getPlayerObjectives", String.class);
		playerObjectives.setAccessible(true);

		Field playerField = CRAFT_SCORE.getDeclaredField("playerName");
		playerField.setAccessible(true);
		String playerName = (String) playerField.get(craftScore);
		Map map = (Map) playerObjectives.invoke(scoreboard, playerName);

		if(map.remove(craftObjectiveHandle) == null) {
			// If they don't have a score to delete, don't delete it.
			return;
		}

		Method resetScores = SCOREBOARD.getDeclaredMethod("resetPlayerScores", String.class);
		resetScores.setAccessible(true);
		resetScores.invoke(scoreboard, playerName);

		for(Object key : map.keySet()) {
			Object value = map.get(key);
			Method playerScoreMethod = SCOREBOARD.getDeclaredMethod("getPlayerScoreForObjective", String.class, SCOREBOARD_OBJECTIVE);
			playerScoreMethod.setAccessible(true);
			Object scoreboardScore = playerScoreMethod.invoke(scoreboard, playerName, key);

			Method getScore = SCOREBOARD_SCORE.getDeclaredMethod("getScore");
			getScore.setAccessible(true);
			int setScoreTo = (int) getScore.invoke(value);

			Method setScore = SCOREBOARD_SCORE.getDeclaredMethod("setScore", int.class);
			setScore.setAccessible(true);
			setScore.invoke(scoreboardScore, setScoreTo);
		}

			/*
			CraftScoreboard myBoard = objective.checkState();
			Map<ScoreboardObjective, ScoreboardScore> savedScores = myBoard.board.getPlayerObjectives(playerName);
			if(savedScores.remove(objective.getHandle()) == null) {
				// If they don't have a score to delete, don't delete it.
				return;
			}
			myBoard.board.resetPlayerScores(playerName);
			for(Map.Entry<ScoreboardObjective, ScoreboardScore> e : savedScores.entrySet()) {
				myBoard.board.getPlayerScoreForObjective(playerName, e.getKey()).setScore(e.getValue().getScore());
			}
			*/
	}

	public static Object checkState(Object craftObjective) {
		try {
			return checkStateException(craftObjective);
		} catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Object checkStateException(Object craftObjective) throws NoSuchFieldException, IllegalAccessException {
		Object boardComponent = CRAFT_SCOREBOARD_COMPONENT.cast(craftObjective);

		Field scoreboard = CRAFT_SCOREBOARD_COMPONENT.getDeclaredField("scoreboard");
		scoreboard.setAccessible(true);
		Object craftBoard = scoreboard.get(boardComponent);

		if(craftBoard == null) {
			throw new IllegalStateException("Unregistered scoreboard component");
		}

		return craftBoard;

			/*
			CraftScoreboard scoreboard = this.scoreboard;
			if(scoreboard == null) {
				throw new IllegalStateException("Unregistered scoreboard component");
			}
			return scoreboard;
			*/
	}

}
