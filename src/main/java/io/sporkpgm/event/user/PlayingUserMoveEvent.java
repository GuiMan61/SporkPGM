package io.sporkpgm.event.user;

import io.sporkpgm.user.User;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayingUserMoveEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private Event event;
	private User user;

	private Location from;
	private Location to;

	public PlayingUserMoveEvent(Event event, User user, Location from, Location to) {
		this.event = event;
		this.user = user;
		this.from = from;
		this.to = to;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
		if(event instanceof Cancellable) {
			((Cancellable) event).setCancelled(cancelled);
			return;
		}
	}

	public Event getEvent() {
		return event;
	}

	public User getPlayer() {
		return user;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}

	public void cancel() {
		if(!(getEvent() instanceof PlayerMoveEvent)) {
			return;
		}

		PlayerMoveEvent event = (PlayerMoveEvent) getEvent();
		event.getPlayer().teleport(event.getFrom());
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
