package io.sporkpgm.listeners;

import io.sporkpgm.event.map.BlockChangeEvent;
import io.sporkpgm.match.Match;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MapListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockChange(BlockChangeEvent event) {
		if(!Match.getMatch().isRunning()) {
			event.setCancelled(true);
			event.setLocked(true);
		}
	}

}
