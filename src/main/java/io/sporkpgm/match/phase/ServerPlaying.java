package io.sporkpgm.match.phase;

import io.sporkpgm.match.Match;
import io.sporkpgm.match.MatchPhase;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.user.User;
import io.sporkpgm.util.Chars;
import org.bukkit.ChatColor;

import java.util.List;

public class ServerPlaying extends ServerPhase {

	public ServerPlaying(Match match, MatchPhase phase) {
		this.match = match;
		this.phase = phase;
	}

	@Override
	public void run() {
		if(complete)
			return;
		duration++;

		/*
		List<User> players = match.getMap().getPlayers();
		if(duration % 5 == 0) {
			for(User player : players) {
				player.updateInventory();
			}
		}

		match.getMap().checkEnded();
		if(match.getMap().hasEnded()) {
			end();
		}
		*/
	}

	public void end() {
		match.setPhase(MatchPhase.CYCLING);

		for(User player : match.getMap().getPlayers()) {
			// player.setTeam(team, false, true, false);
		}
		// match.getMap().stop();

		complete = true;

		TeamModule winner = match.getMap().getWinner();
		String message = null;
		if(winner != null && !winner.isObservers()) {
			message = winner.getColoredName() + ChatColor.GOLD + " wins";
		}

		broadcast(ChatColor.DARK_PURPLE + "# # # # # # # # # # # # # # # # ");
		broadcast(ChatColor.DARK_PURPLE + "# # " + ChatColor.GOLD + "The match has ended!" + ChatColor.DARK_PURPLE + " # #");
		if(message != null) {
			broadcast(ChatColor.DARK_PURPLE + "# # " + message + ChatColor.DARK_PURPLE + " # #");
		}
		broadcast(ChatColor.DARK_PURPLE + "# # # # # # # # # # # # # # # #");
	}

	public String arrows(Chars chars) {
		return chars + " " + chars;
	}

}
