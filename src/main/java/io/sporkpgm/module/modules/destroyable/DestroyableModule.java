package io.sporkpgm.module.modules.destroyable;

import com.google.common.collect.Lists;
import io.sporkpgm.Spork;
import io.sporkpgm.event.map.BlockChangeEvent;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.ObjectiveModule;
import io.sporkpgm.module.modules.region.Region;
import io.sporkpgm.module.modules.region.types.BlockRegion;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.user.User;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleInfo(description = "Handles Objectives which have to be destroyed", builder = DestroyableBuilder.class)
public class DestroyableModule extends ObjectiveModule implements Listener {

	private Region region;
	private Material[] materials;
	private int completion;
	private List<DestroyableBlock> blocks;

	public DestroyableModule(String name, TeamModule team, Region region, Material[] materials, int completion) {
		super(name, team);
		this.complete = false;
		this.region = region;
		this.materials = materials;
		this.completion = completion;
	}

	public Region getRegion() {
		return region;
	}

	public Material[] getMaterials() {
		return materials;
	}

	public List<DestroyableBlock> getBlocks() {
		return blocks;
	}

	public List<DestroyableBlock> getBrokenBlocks() {
		List<DestroyableBlock> blocks = new ArrayList<>();

		for(DestroyableBlock block : this.blocks) {
			if(block.isBroken()) {
				blocks.add(block);
			}
		}

		return blocks;
	}

	public DestroyableBlock getBlock(Location location) {
		return getBlock(new BlockRegion(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
	}

	public DestroyableBlock getBlock(BlockRegion region) {
		if(blocks == null) return null;
		for(DestroyableBlock block : blocks) {
			if(block.getBlock().isInside(region, false)) {
				return block;
			}
		}

		return null;
	}

	@Override
	public void setComplete(boolean complete) {
		super.setComplete(complete);
		if(!complete) {
			return;
		}

		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.AQUA).append(name).append(ChatColor.GRAY).append(" was completed by ");

		int i = 1;
		int max = 3;
		Map<String, List<DestroyableBlock>> ordered = getOrderedPlayerBlocks();

		List<String> toEnglish = new ArrayList<>();
		for(String string : ordered.keySet()) {
			if(i <= max) {
				String name = Spork.getName(string);
				if(name == null) {
					name = string;
				}

				String user = team.getColor() + name;
				String percentage = " " + ChatColor.GRAY + "(" + getCompletionPercentage(string) + "%)";
				toEnglish.add(user + percentage);
			} else if(i == max + 1) {
				int remaining = ordered.keySet().size() - max;
				String others = ChatColor.GRAY + "" + remaining + " others";
				toEnglish.add(others);
			}
		}

		builder.append(StringUtil.listToEnglishCompound(toEnglish));

		Bukkit.broadcastMessage(builder.toString());
	}


	@EventHandler
	public void onBlockChange(BlockChangeEvent event) {
		DestroyableBlock broken = getBlock(event.getLocation());

		if(event.isPlace()) {
			return;
		}

		/*
		for(DestroyableBlock block : blocks) {
			Log.info("Monument handling Block change, @" + event.getRegion() + " compared to " + block.getBlock());
		}
		*/

		if(broken == null) {
			return;
		}

		if(isComplete()) {
			return;
		}

		if(!event.hasPlayer()) {
			event.setCancelled(true);
			return;
		}

		if(event.getPlayer().getTeam() != getTeam()) {
			event.setCancelled(true);
			event.getPlayer().getPlayer().sendMessage(ChatColor.RED + "You can't break your own monument");
			return;
		}

		broken.setComplete(event.getPlayer(), true);
		setComplete(getCompletionPercentage() >= completion);
	}

	@Override
	public void start() {
		/*
		Thread.dumpStack();

		Log.info("Adding blocks");
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < materials.length; i++) {
			if(i != 0) {
				builder.append(", ");
			}

			builder.append(materials[i].name());
		}
		Log.info(builder.toString());
		*/

		blocks = new ArrayList<>();
		for(BlockRegion block : region.getValues(materials, team.getMap().getWorld())) {
			Log.info(block.toString());
			Log.info(String.valueOf(blocks.size()));
			blocks.add(new DestroyableBlock(block));
		}
	}

	public Map<String, List<DestroyableBlock>> getOrderedPlayerBlocks() {
		Map<String, List<DestroyableBlock>> map = new HashMap<>();
		Map<String, List<DestroyableBlock>> list = getPlayerBlocks();

		while(list.size() > 0) {
			List<String> names = getHighest(list);
			for(String name : names) {
				map.put(name, list.get(name));
				list.remove(name);
			}
		}

		return map;
	}

	public List<String> getHighest(Map<String, List<DestroyableBlock>> placements) {
		Object[] objectNames = placements.keySet().toArray();
		final String[] allNames = new String[objectNames.length];
		for(int i = 0; i < objectNames.length; i++) {
			allNames[i] = (String) objectNames[i];
		}

		List<String> names = Lists.newArrayList(allNames[0]);
		int highest = placements.get(names.get(0)).size();

		for(String name : allNames) {
			if(!names.contains(name)) {
				int score = placements.get(name).size();
				if(score > highest) {
					highest = score;
					names.clear();
					names.add(name);
				} else if(score == highest) {
					names.add(name);
				}
			}
		}

		return names;
	}

	public Map<String, List<DestroyableBlock>> getPlayerBlocks() {
		Map<String, List<DestroyableBlock>> map = new HashMap<>();

		for(DestroyableBlock place : blocks) {
			if(map.get(place.getUser()) == null) {
				map.put(place.getUser(), new ArrayList<DestroyableBlock>());
			}

			map.get(place.getUser()).add(place);
		}

		return map;
	}

	public int getBrokenBlocks(User player) {
		return getBrokenBlocks(player.getUUID());
	}

	public int getBrokenBlocks(String uuid) {
		int i = 0;

		for(DestroyableBlock place : blocks) {
			boolean match = place.getUser().equals(uuid);
			// Log.info("Viewing placement by " + place.getPlayer().getName() + " attempting to match to " + name + " (" + match + ")");
			if(match) {
				i++;
			}
		}

		return i;
	}

	public int getCompletionPercentage() {
		double completed = getBrokenBlocks().size();
		double possible = getBlocks().size();

		double complete = completed / possible;
		double percent = complete * 100;
		return (int) percent;
	}

	public int getCompletionPercentage(User player) {
		return getCompletionPercentage(player.getUUID());
	}

	public int getCompletionPercentage(String uuid) {
		double completed = getBrokenBlocks(uuid);
		double possible = getBlocks().size();

		double complete = completed / possible;

		double percent = complete * 100;
		return (int) percent;
	}

}
