package io.sporkpgm.match;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.event.match.MatchEndEvent;
import io.sporkpgm.event.match.MatchPhaseChangeEvent;
import io.sporkpgm.event.match.MatchStartEvent;
import io.sporkpgm.match.phase.ServerCycling;
import io.sporkpgm.match.phase.ServerPhase;
import io.sporkpgm.match.phase.ServerStarting;
import io.sporkpgm.rotation.Rotation;
import io.sporkpgm.util.SchedulerUtil;
import io.sporkpgm.util.TimeUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;

import java.util.ArrayList;
import java.util.List;

import static io.sporkpgm.ListenerHandler.*;

public class Match {

	private DateTime start;
	private DateTime end;

	private int id;
	private SporkMap map;
	private MatchPhase phase;
	private ServerPhase runnable;
	private SchedulerUtil schedule;
	private boolean forced;

	private List<MatchPhase> phases;

	public Match(SporkMap map, int id) {
		this.id = id;
		this.map = map;
		setPhase(MatchPhase.WAITING);
	}

	public boolean load() {
		return map.load(this);
	}

	public boolean unload() {
		return map.unload(this);
	}

	public int getID() {
		return id;
	}

	public SporkMap getMap() {
		return map;
	}

	public MatchPhase getPhase() {
		return phase;
	}

	public String getMatchTime() {
		if(start == null) {
			return "Not started yet";
		}

		int seconds = getDuration();
		String out = TimeUtil.formatSeconds(seconds, "s");
		if(seconds >= 60)
			out = TimeUtil.formatSeconds(seconds, "m") + ":" + out;
		if(seconds >= 3600)
			out = TimeUtil.formatSeconds(seconds, "h") + ":" + out;
		return out;
	}

	public void setPhase(MatchPhase phase) {
		setPhase(phase, phase.getDuration());
	}

	public void setPhase(MatchPhase phase, boolean forced) {
		this.forced = forced;
		setPhase(phase);
	}

	public void setPhase(MatchPhase phase, int duration) {
		if(phase == MatchPhase.CYCLING) {
			MatchEndEvent end = new MatchEndEvent(this);
			callEvent(end);
		}

		stop();
		if(phases == null)
			phases = new ArrayList<>();
		ServerPhase from = runnable;
		ServerPhase to = phase.getPhase(this);
		MatchPhaseChangeEvent event = new MatchPhaseChangeEvent(this, from, to);
		callEvent(event);
		this.phase = phase;
		this.runnable = to;
		this.schedule = new SchedulerUtil(runnable, false);
		this.phases.add(phase);
		if(this.phase == MatchPhase.PLAYING)
			this.start = new DateTime(DateTimeZone.UTC);
		else if(this.phase == MatchPhase.CYCLING)
			this.end = new DateTime(DateTimeZone.UTC);
		setDuration(duration);
		start();

		if(phase == MatchPhase.PLAYING) {
			MatchStartEvent start = new MatchStartEvent(this);
			callEvent(start);
		}
	}

	public void setPhase(MatchPhase phase, int duration, boolean forced) {
		this.forced = forced;
		setPhase(phase, duration);
	}

	public String getMessage() {
		return runnable.getMessage();
	}

	public DateTime getStart() {
		return start;
	}

	public DateTime getEnd() {
		return end;
	}

	public int getDuration() {
		DateTime start = this.start;
		DateTime end = (this.end != null ? this.end : new DateTime(DateTimeZone.UTC));
		return Seconds.secondsBetween(start, end).getSeconds();
	}

	/**
	 * Sets the duration for supported phases (ServerStarting, ServerCycling)
	 *
	 * @param seconds The duration in seconds
	 * @return If the duration setting was supported
	 */
	public boolean setDuration(int seconds) {
		if(runnable.getClass() == ServerStarting.class) {
			ServerStarting phase = (ServerStarting) runnable;
			phase.setSeconds(seconds);
			return true;
		} else if(runnable.getClass() == ServerCycling.class) {
			ServerCycling phase = (ServerCycling) runnable;
			phase.setSeconds(seconds);
			return true;
		}

		return false;
	}

	public boolean isForced() {
		return forced;
	}

	public boolean isRunning() {
		return getPhase() == MatchPhase.PLAYING;
	}

	public void start() {
		if(schedule != null)
			schedule.startRepeat(0, 1);
	}

	public void stop() {
		if(schedule != null)
			schedule.stopRepeat();
	}

	public static Match getMatch() {
		return Rotation.get().getRotation().getCurrent().getMatch();
	}

	@Override
	public String toString() {
		return "Match{map=" + map.toString() + ",phase=" + phase.name() + "}";
	}

}
