package io.sporkpgm.event.user;

import io.sporkpgm.user.User;
import org.bukkit.event.Event;

public abstract class UserEvent extends Event {

	protected User user;

	public UserEvent(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

}
