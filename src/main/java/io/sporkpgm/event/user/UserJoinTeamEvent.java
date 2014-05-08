package io.sporkpgm.event.user;

import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.user.User;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class UserJoinTeamEvent extends UserEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private TeamModule team;
	private String reason = "No reason";
	private boolean cancelled = false;

	public UserJoinTeamEvent(User user, TeamModule team) {
		super(user);
		this.team = team;
	}

	public void setCancelled(boolean cancelled, String reason) {
		setCancelled(cancelled);
		setReason(reason);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public TeamModule getTeam() {
		return team;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
