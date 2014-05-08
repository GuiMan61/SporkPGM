package io.sporkpgm.listeners;

import io.sporkpgm.event.map.BlockChangeEvent;
import io.sporkpgm.event.user.PlayingUserMoveEvent;
import io.sporkpgm.module.modules.filter.AppliedRegion;
import io.sporkpgm.module.modules.filter.exceptions.InvalidContextException;
import io.sporkpgm.module.modules.filter.other.Context;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.modules.region.Region;
import io.sporkpgm.rotation.Rotation;
import io.sporkpgm.util.Log;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class FilterTriggerListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockChange(BlockChangeEvent event) {
		/*
		if(event.hasPlayer()) {
			Log.info("Checking " + event.getPlayer().getName() + "'s Block " + (event.isBreak() ? "Break" : "Place") + " for any filter incursions (" + event.getRegion() + ")");
		}
		*/

		apply(event, false);
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayingUserMoveEvent event) {
		apply(event);
	}

	private void apply(Event event) {
		apply(event, false);
	}

	private void apply(Event event, boolean log) {
		SporkMap map = Rotation.getMap();
		if(log) {
			Log.info("Checking Regions from '" + map.getName() + "'");
		}

		List<Region> regions = map.getRegions().getRegions();
		if(log) {
			Log.info("Found " + regions.size() + " regions to check");
		}

		try {
			Context context = new Context(event);
			for(Region region : regions) {
				AppliedRegion applied = null;
				try {
					applied = (AppliedRegion) region;
				} catch(ClassCastException e) { /* nothing */ }

				if(log) {
					Log.info("Checking if " + region.getClass().getSimpleName() + " is an instance of AppliedRegion (" + (applied != null) + ")");
				}

				if(applied != null) {
					applied.apply(context, log);
				}
			}
		} catch(InvalidContextException e) {
			e.printStackTrace();
		}
	}

}
