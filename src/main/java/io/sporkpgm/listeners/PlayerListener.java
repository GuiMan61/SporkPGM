package io.sporkpgm.listeners;

import io.sporkpgm.ListenerHandler;
import io.sporkpgm.Spork;
import io.sporkpgm.event.user.PlayingUserMoveEvent;
import io.sporkpgm.match.MatchPhase;
import io.sporkpgm.rotation.Rotation;
import io.sporkpgm.rotation.RotationSlot;
import io.sporkpgm.user.User;
import io.sporkpgm.util.Log;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		User player = User.getUser(event.getPlayer());
		if(player.isObserver())
			return;

		PlayingUserMoveEvent ppme = new PlayingUserMoveEvent(event, player, event.getFrom(), event.getTo());
		ListenerHandler.callEvent(ppme);
		if(ppme.isCancelled()) {
			event.setTo(event.getFrom());
		}
	}

	@EventHandler
	public void onObserverMove(PlayerMoveEvent event) {
		User player = User.getUser(event.getPlayer());
		if(!player.isObserver())
			return;

		if(player.getPlayer().getLocation().getBlockY() <= 40) {
			player.setTeam(player.getTeam(), false, false, true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		User player = User.getUser(event.getPlayer());
		if(player.isObserver())
			return;

		PlayingUserMoveEvent ppme = new PlayingUserMoveEvent(event, player, event.getFrom(), event.getTo());
		ListenerHandler.callEvent(ppme);
		if(ppme.isCancelled()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onVehicleEnter(VehicleEnterEvent event) {
		if(event.getEntered() instanceof Player) {
			User player = User.getUser((Player) event.getEntered());
			if(player.isObserver())
				return;

			PlayingUserMoveEvent ppme = new PlayingUserMoveEvent(event, player, player.getPlayer().getLocation(), event.getVehicle().getLocation());
			ListenerHandler.callEvent(ppme);
			if(ppme.isCancelled()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onAchievementAwarded(PlayerAchievementAwardedEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		User player = User.getUser(event.getPlayer());
		player.updateInventory();
		if(!player.isObserver()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		User player = User.getUser(event.getPlayer());
		player.updateInventory();
		if(player.isObserver()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			User damager = User.getUser((Player) event.getDamager());
			User victim = User.getUser((Player) event.getEntity());

			if(!damager.isObserver()) {
				event.setCancelled(true);
			} else {
				victim.updateInventory();
				damager.updateInventory();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		User player = User.getUser(event.getPlayer());
		if(!player.isObserver()) {
			event.setCancelled(true);
		}

		player.updateInventory();
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().equals(Material.CHEST)) {
			if(player.isObserver() || Rotation.getSlot().getMatch().getPhase() != MatchPhase.PLAYING) {
				Chest chest = (Chest) event.getClickedBlock().getState();
				player.open(chest.getInventory());
				event.setUseInteractedBlock(Result.DENY);
				event.setCancelled(true);
			}
		} else if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(player.isObserver() && (!event.getPlayer().getItemInHand().getType().equals(Material.AIR) || event.getPlayer().getItemInHand().getType().equals(Material.COMPASS))) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Log.info(event.getRightClicked().getClass().getSimpleName() + " has been right clicked by " + event.getPlayer().getName());

		User player = User.getUser(event.getPlayer());
		if(event.getRightClicked() instanceof Player) {
			User clicked = User.getUser((Player) event.getRightClicked());
			Log.info(clicked.getName() + " has been right clicked by " + event.getPlayer().getName());

			if(!player.isObserver()) {
				event.getPlayer().openInventory(clicked.getInventory());
				Log.info(clicked.getName() + " has had their inventory opened by " + event.getPlayer().getName());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked() instanceof Player) {
			User player = User.getUser((Player) event.getWhoClicked());

			if(!player.isObserver() && !event.getInventory().equals(player.getPlayer().getInventory())) {
				event.setCancelled(true);
			}

			if(!event.isCancelled()) {
				player.updateInventory();
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getPlayer() instanceof Player == false) {
			return;
		}

		User player = User.getUser((Player) event.getPlayer());
		if(player.isObserver()) {
			return;
		}

		player.close(event.getInventory());
	}

}
