package io.sporkpgm.listeners;

import io.sporkpgm.ListenerHandler;
import io.sporkpgm.Spork;
import io.sporkpgm.event.map.BlockChangeEvent;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.match.Match;
import io.sporkpgm.rotation.Rotation;
import io.sporkpgm.user.User;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.material.MaterialData;

public class BlockListener implements Listener {

	@EventHandler
	public void onBlockChange(BlockChangeEvent event) {
		event.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLowPriorityBlockChange(BlockChangeEvent event) {
		event.setCancelled(false);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}
		BlockChangeEvent change = new BlockChangeEvent(event, map, User.getUser(player), event.getBlockReplacedState(), event.getBlockPlaced().getState());
		ListenerHandler.callEvent(change);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}

		BlockState newState = event.getBlock().getState();
		newState.setData(new MaterialData(Material.AIR, (byte) 0));

		BlockChangeEvent change = new BlockChangeEvent(event, map, User.getUser(player), event.getBlock().getState(), newState);
		ListenerHandler.callEvent(change);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}

		BlockState newState = block.getState();
		Material update = Material.LAVA;
		if(event.getBucket() == Material.WATER_BUCKET) {
			update = Material.WATER;
		}
		newState.setData(new MaterialData(update, (byte) 0));

		BlockState oldState = block.getState();
		BlockChangeEvent change = new BlockChangeEvent(event, map, User.getUser(player), oldState, newState);
		ListenerHandler.callEvent(change);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockForm(BlockFormEvent event) {
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}

		BlockChangeEvent change = new BlockChangeEvent(event, map, event.getBlock().getState(), event.getNewState());
		ListenerHandler.callEvent(change);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockSpread(BlockSpreadEvent event) {
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}
		BlockChangeEvent change = new BlockChangeEvent(event, map, event.getBlock().getState(), event.getNewState());
		ListenerHandler.callEvent(change);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockFromTo(BlockFromToEvent event) {
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}
		if(event.getToBlock().getType() != event.getBlock().getType()) {
			BlockChangeEvent change = new BlockChangeEvent(event, map, event.getBlock().getState(), event.getToBlock().getState());
			ListenerHandler.callEvent(change);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}

		User player = null;
		if(event.getEntity() instanceof TNTPrimed) {
			TNTPrimed tnt = (TNTPrimed) event.getEntity();
			if(tnt.getSource() instanceof Player) {
				player = User.getUser((Player) tnt.getSource());
			}
		}

		for(Block block : event.blockList()) {
			BlockState newState = block.getState();
			newState.setData(new MaterialData(Material.AIR, (byte) 0));
			BlockChangeEvent change = new BlockChangeEvent(event, map, player, block.getState(), newState);
			ListenerHandler.callEvent(change);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBurn(BlockBurnEvent event) {
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}
		BlockState newState = event.getBlock().getState();
		newState.setData(new MaterialData(Material.AIR, (byte) 0));

		BlockChangeEvent change = new BlockChangeEvent(event, map, event.getBlock().getState(), newState);
		ListenerHandler.callEvent(change);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockFade(BlockFadeEvent event) {
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}
		BlockState newState = event.getBlock().getState();
		newState.setData(new MaterialData(Material.AIR, (byte) 0));

		BlockChangeEvent change = new BlockChangeEvent(event, map, event.getBlock().getState(), newState);
		ListenerHandler.callEvent(change);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}
		for(Block block : event.getBlocks()) {
			BlockState newState = block.getRelative(event.getDirection()).getState();
			newState.setData(new MaterialData(block.getType(), block.getData()));
			BlockChangeEvent change = new BlockChangeEvent(event, map, block.getRelative(event.getDirection()).getState(), newState);
			ListenerHandler.callEvent(change);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		SporkMap map = null;
		try {
			Match match = Rotation.getSlot().getMatch();
			map = (match != null ? match.getMap() : null);
		} catch(NullPointerException ignored) {
		}
		if(event.isSticky()) {
			BlockState state = event.getBlock().getWorld().getBlockAt(event.getRetractLocation()).getState();
			BlockState newState = state;
			newState.setData(new MaterialData(Material.AIR, (byte) 0));
			BlockChangeEvent change = new BlockChangeEvent(event, map, state, newState);
			ListenerHandler.callEvent(change);
		}
	}

}
