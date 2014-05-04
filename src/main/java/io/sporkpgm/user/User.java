package io.sporkpgm.user;

import io.sporkpgm.ListenerHandler;
import io.sporkpgm.Spork;
import io.sporkpgm.event.user.UserAddEvent;
import io.sporkpgm.event.user.UserRemoveEvent;
import io.sporkpgm.module.modules.team.TeamModule;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class User {

	protected static List<User> users = new ArrayList<>();

	protected String uuid;
	protected String name;
	protected Player player;
	protected TeamModule team;

	public User(Player player) {
		this.uuid = Spork.getUUID(player.getName());
		this.name = player.getName();
		this.player = player;
	}

	public String getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public Player getPlayer() {
		return player;
	}

	public TeamModule getTeam() {
		return team;
	}

	public User add() {
		if(!users.contains(this)) {
			UserAddEvent event = new UserAddEvent(this);
			ListenerHandler.callEvent(event);
			users.add(this);
		}

		return this;
	}

	public void remove() {
		if(users.contains(this)) {
			UserRemoveEvent event = new UserRemoveEvent(this);
			ListenerHandler.callEvent(event);
		}

		users.remove(this);
		this.player = null;
	}

	public static List<User> getUsers() {
		return users;
	}

	public static User getUser(Player player) {
		for(User user : users) {
			if(user.player.equals(player)) {
				return user;
			}
		}

		return null;
	}

}
