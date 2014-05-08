package io.sporkpgm.user;

import com.google.common.collect.Lists;
import io.sporkpgm.ListenerHandler;
import io.sporkpgm.Spork;
import io.sporkpgm.event.user.UserChatEvent;
import io.sporkpgm.event.user.UserAddEvent;
import io.sporkpgm.event.user.UserJoinTeamEvent;
import io.sporkpgm.event.user.UserRemoveEvent;
import io.sporkpgm.module.modules.spawn.SpawnModule;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.rotation.Rotation;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.NMSUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

public class User {

	protected static List<User> users = new ArrayList<>();

	protected String uuid;
	protected String name;
	protected Player player;
	protected PermissionAttachment attachment;

	protected TeamModule team;
	protected Inventory inventory;
	protected Map<Inventory, Inventory> inventories;

	public User(Player player) {
		this.uuid = Spork.getUUID(player.getName());
		this.name = player.getName();
		this.player = player;
		this.attachment = getPlayer().addAttachment(Spork.get());
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

	public TeamModule setTeam(TeamModule team) {
		boolean display = getTeam() != null;
		boolean running = Rotation.getSlot().getMatch().isRunning();
		boolean inventory = running || !display;
		boolean teleport = !display || running;
		return setTeam(team, display, inventory, teleport);
	}

	public TeamModule setTeam(TeamModule team, boolean display, boolean inventory, boolean teleport) {
		UserJoinTeamEvent event = new UserJoinTeamEvent(this, team);
		ListenerHandler.callEvent(event);

		if(event.isCancelled()) {
			getPlayer().sendMessage(ChatColor.RED + event.getReason());
			return this.team;
		}

		this.team = team;

		SpawnModule spawn = team.getSpawn();

		if(display)
			display();
		if(inventory)
			inventory(spawn);
		if(teleport)
			teleport(spawn);

		getPlayer().setDisplayName(team.getColor() + getPlayer().getName());
		getTeam().getTeam().addPlayer(this);
		// getPlayer().setScoreboard();

		return team;
	}

	public void display() {
		TeamModule team = getTeam();
		if(team.isObservers())
			getPlayer().sendMessage(ChatColor.GRAY + "You are now " + ChatColor.AQUA + "Observing" + ChatColor.GRAY + ".");
		else
			getPlayer().sendMessage(ChatColor.GRAY + "You have joined the " + team.getColoredName() + ChatColor.GRAY + ".");
	}

	public void inventory(SpawnModule spawn) {
		empty();
		boolean update = false;
		GameMode mode = GameMode.CREATIVE;
		String[] perms = {"worldedit.navigation.jump.*", "worldedit.navigation.thru.*", "commandbook.teleport"};
		if(getTeam().isObservers() || !Rotation.getSlot().getMatch().isRunning()) {
			ItemStack compass = new ItemStack(Material.COMPASS);
			ItemMeta meta = compass.getItemMeta();
			meta.setDisplayName(ChatColor.AQUA + "Teleportation Device");
			compass.setItemMeta(meta);
			getPlayer().getInventory().setItem(0, compass);
			for(String permission : perms)
				attachment.setPermission(permission, true);
		} else {
			update = true;
			mode = GameMode.SURVIVAL;
			/*
			if(spawn.hasKit()) {
				Log.info("SpawnModule.hasKit() == true... Applying kit!");
				spawn.getKit().apply(this);
			}
			 */
			for(String permission : perms)
				attachment.setPermission(permission, false);
		}

		try {
			// getPlayer().setAffectsSpawning(update);
			Method spawning = Player.class.getMethod("setAffectsSpawning", boolean.class);
			spawning.setAccessible(true);
			spawning.invoke(getPlayer(), update);
		} catch(Exception e) {
			Log.warning("Not running SportBukkit or AthenaBukkit, skipping affects spawning...");
		}

		try {
			// getPlayer().setCollidesWithEntities(update);
			Method collides = Player.class.getMethod("setCollidesWithEntities", boolean.class);
			collides.setAccessible(true);
			collides.invoke(getPlayer(), update);
		} catch(Exception e) {
			Log.warning("Not running SportBukkit or AthenaBukkit, skipping collides with entities...");
		}

		try {
			// getPlayer().setArrowsStuck(0);
			Method arrows = Player.class.getMethod("setArrowsStuck", int.class);
			arrows.setAccessible(true);
			arrows.invoke(getPlayer(), 0);
		} catch(Exception e) {
			Log.warning("Not running SportBukkit or AthenaBukkit, attempting to set arrows stuck manually...");

			try {
			/*
			 * CraftPlayer player = (CraftPlayer) getPlayer();
			 * player.getHandle().p(0);
			 *
			 * Set a players Arrows Stuck to 0
			 */

				Player player = getPlayer();
				Object craft = NMSUtil.getClassBukkit("entity.CraftPlayer").cast(player);
				Method method = NMSUtil.getClassBukkit("entity.CraftPlayer").getMethod("getHandle");
				method.setAccessible(true);
				Object handle = method.invoke(craft);
				method = NMSUtil.getClassNMS("EntityLiving").getMethod("p", int.class);
				method.setAccessible(true);
				method.invoke(NMSUtil.getClassNMS("EntityLiving").cast(handle), 0);
			} catch(Exception e2) {
				Log.warning("Failed to set Arrows Stuck manually");
				e2.printStackTrace();
			}
		}

		getPlayer().setCanPickupItems(update);
		getPlayer().setFireTicks(0);
		getPlayer().setFallDistance(0);
		getPlayer().setExp(0);
		getPlayer().setLevel(0);
		getPlayer().setHealth(20);
		getPlayer().setFoodLevel(20);
		getPlayer().setSaturation(20);
		getPlayer().setGameMode(mode);

		vanish();
	}

	public void teleport(SpawnModule spawn) {
		try {
			getPlayer().teleport(spawn.getSpawn());
		} catch(ConcurrentModificationException e) {
			e.printStackTrace();
			teleport(spawn);
		}
	}

	public void empty() {
		ItemStack air = new ItemStack(Material.AIR, 0);

		clearPotionEffects();

		boolean cancel = false;
		for(int i = 0; !cancel; i++) {
			try {
				getPlayer().getInventory().setItem(i, air);
			} catch(Exception e) {
				cancel = true;
			}
		}

		getPlayer().getInventory().setHelmet(air);
		getPlayer().getInventory().setChestplate(air);
		getPlayer().getInventory().setLeggings(air);
		getPlayer().getInventory().setBoots(air);
		getPlayer().updateInventory();
	}

	public void clearPotionEffects() {
		clearPotionEffects(PotionEffectType.values());
	}

	public void clearPotionEffects(PotionEffectType[] types) {
		for(PotionEffectType type : types)
			if(type != null)
				try {
					if(getPlayer().hasPotionEffect(type) && getPlayer() != null)
						getPlayer().removePotionEffect(type);
				} catch(NullPointerException e) {
					Log.warning("NullPointerException thrown when trying to remove '" + type.getName() + "' from '" + getPlayer().getName() + "'");
				}
	}

	public boolean isObserver() {
		return team.isObservers() || !Rotation.getSlot().getMatch().isRunning();
	}

	public Inventory getInventory() {
		updateInventory();
		return inventory;
	}

	public void updateInventory() {
		if(inventory == null) {
			inventory = getPlayer().getServer().createInventory(null, 45, getTeam() + getName());
		}

		int health = getPlayer().getHealth() <= 0 ? 1 : (int) getPlayer().getHealth();
		ItemStack healthBar = new ItemStack(Material.POTION, health, (short) 16389);
		PotionMeta potionMeta = (PotionMeta) healthBar.getItemMeta();
		potionMeta.setDisplayName(ChatColor.RED + "Health");
		potionMeta.setLore(Lists.newArrayList(getPlayer().getFoodLevel() + " Health."));
		healthBar.setItemMeta(potionMeta);

		int food = getPlayer().getFoodLevel() <= 0 ? 1 : getPlayer().getFoodLevel();
		ItemStack foodBar = new ItemStack(Material.SPECKLED_MELON, food, (short) 59);
		ItemMeta foodMeta = foodBar.getItemMeta();
		foodMeta.setDisplayName(ChatColor.GOLD + "Food");
		foodMeta.setLore(Lists.newArrayList(getPlayer().getFoodLevel() + " Food."));
		foodBar.setItemMeta(foodMeta);

		inventory.setItem(7, healthBar);
		inventory.setItem(8, foodBar);
		inventory.setItem(0, getPlayer().getInventory().getBoots());
		inventory.setItem(1, getPlayer().getInventory().getLeggings());
		inventory.setItem(2, getPlayer().getInventory().getChestplate());
		inventory.setItem(3, getPlayer().getInventory().getHelmet());

		for(int i = 0; i < getPlayer().getInventory().getContents().length; i++) {
			inventory.setItem(i < 9 ? 36 + i : i, getPlayer().getInventory().getContents()[i]);
		}
	}

	public void open(Inventory inventory) {
		Inventory fake = Bukkit.createInventory(getPlayer(), inventory.getSize(), inventory.getTitle());
		fake.setContents(inventory.getContents());
		getPlayer().openInventory(fake);
		inventories.put(inventory, fake);
	}

	public void close(Inventory inventory) {
		inventories.remove(inventory);
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

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(event.getPlayer() != getPlayer())
			return;
		event.setCancelled(true);
		UserChatEvent pce = new UserChatEvent(this, event.getMessage(), true);
		ListenerHandler.callEvent(pce);
	}

	@EventHandler
	public void onPlayerChat(UserChatEvent event) {
		if(event.getUser() != this)
			return;
		if(event.getUser().getTeam() == null) {
			Log.info(event.getUser().getPlayer().getName() + "'s team was null!");
			setTeam(Rotation.getMap().getTeams().getObservers());
		}

		boolean team = event.isTeam() && getTeam() != null;
		String pre = (team ? getTeam().getColor() + "[Team] " : "");
		String full = pre + getTeam().getColor() + getName() + ChatColor.WHITE + ": " + event.getMessage();
		if(team) {
			for(User player : getTeam().getPlayers()) {
				player.getPlayer().sendMessage(full);
			}
		} else {
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(full);
			}
		}
		Bukkit.getConsoleSender().sendMessage(full);
	}

	public boolean shouldHide(User player) {
		return isObserver() && !player.isObserver();
	}

	public void vanish() {
		for(User user : getUsers()) {
			if(shouldHide(user)) getPlayer().hidePlayer(user.getPlayer());
			else getPlayer().showPlayer(user.getPlayer());
			if(user.shouldHide(this)) user.getPlayer().hidePlayer(getPlayer());
			else user.getPlayer().showPlayer(getPlayer());
		}
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
