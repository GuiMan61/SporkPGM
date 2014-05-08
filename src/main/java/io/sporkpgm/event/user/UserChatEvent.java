package io.sporkpgm.event.user;

import io.sporkpgm.user.User;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class UserChatEvent extends UserEvent {

	private static final HandlerList handlers = new HandlerList();

	private String message;
	private boolean team;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public UserChatEvent(User user, String message, boolean team) throws NullArgumentException {
		super(user);
		this.message = message;
		this.team = team;
		if(this.user == null)
			throw new NullArgumentException("player");
	}

	public UserChatEvent(Player player, String message, boolean team) throws NullArgumentException {
		this(User.getUser(player), message, team);
	}

	public String getMessage() {
		return message;
	}

	public boolean isTeam() {
		return team;
	}

}
