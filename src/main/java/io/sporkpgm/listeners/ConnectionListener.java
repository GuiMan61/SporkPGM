package io.sporkpgm.listeners;

import io.sporkpgm.match.MatchPhase;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.rotation.Rotation;
import io.sporkpgm.user.User;
import io.sporkpgm.util.Chars;
import io.sporkpgm.util.SchedulerUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class ConnectionListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final User player = new User(event.getPlayer()).add();
		final TeamModule obs = Rotation.getMap().getTeams().getObservers();

		player.setTeam(obs, false, false, false);

		new SchedulerUtil(new Runnable() {

			@Override
			public void run() {
				player.setTeam(obs, false, true, true);
				player.updateInventory();
			}

		}, false).delay(1);

		event.setJoinMessage(player.getTeam().getColor() + player.getName() + ChatColor.YELLOW + " joined the game");
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		User player = User.getUser(event.getPlayer());
		event.setQuitMessage(player.getTeam().getColor() + player.getName() + ChatColor.YELLOW + " left the game");
		player.remove();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		User player = User.getUser(event.getPlayer());
		event.setLeaveMessage(player.getTeam().getColor() + player.getName() + ChatColor.YELLOW + " left the game");
		player.remove();
	}

	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		MatchPhase phase = Rotation.getSlot().getMatch().getPhase();
		ChatColor status = null;
		if(phase == MatchPhase.WAITING) {
			status = ChatColor.GRAY;
		} else if(phase == MatchPhase.STARTING) {
			status = ChatColor.GREEN;
		} else if(phase == MatchPhase.PLAYING) {
			status = ChatColor.GOLD;
		} else if(phase == MatchPhase.CYCLING) {
			status = ChatColor.RED;
		}
		event.setMotd(status + "" + Chars.RAQUO + "" + ChatColor.AQUA + " " + Rotation.getMap().getName() + " " + status + Chars.LAQUO);
	}

}
