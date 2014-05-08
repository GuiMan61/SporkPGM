package io.sporkpgm.module.modules.region.types;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.modules.region.Region;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class VoidRegion extends Region {

	public VoidRegion() {
		this(null);
	}

	public VoidRegion(String name) {
		super(name);
	}

	public List<BlockRegion> getValues() {
		return new ArrayList<>();
	}

	@Override
	public boolean isInside(BlockRegion block) {
		SporkMap map = SporkMap.getMap();
		if(map == null || map.getWorld() == null) {
			return false;
		}

		World world = map.getWorld();
		BlockRegion check = new BlockRegion(block.x, "0", block.z);
		// Log.info("Checking if " + check + " is AIR (" + check.getBlock(world).getType().name() + ")");
		if(check.getBlock(world).getType() == Material.AIR) {
			return true;
		}

		return false;
	}

	public boolean isAbove(BlockRegion region) {
		return false;
	}

	public boolean isBelow(BlockRegion region) {
		return false;
	}

	@Override
	public String toString() {
		return "VoidRegion{name=" + getName() + "}";
	}

}
