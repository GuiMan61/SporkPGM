package io.sporkpgm.module.modules.spawn;

import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.modules.kits.KitModule;
import io.sporkpgm.module.modules.region.Region;
import io.sporkpgm.module.modules.region.types.BlockRegion;
import io.sporkpgm.module.modules.region.types.groups.UnionRegion;
import io.sporkpgm.rotation.Rotation;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.RegionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

@ModuleInfo(description = "Stores information regarding spawns for a team", builder = SpawnModuleBuilder.class)
public class SpawnModule extends UnionRegion {

	private boolean safe;
	private String name;
	private KitModule kit;

	private float yaw;
	private float pitch;

	public SpawnModule(String name, List<Region> regions) {
		this(name, regions, null, 0, 0);
	}

	public SpawnModule(String name, List<Region> regions, float yaw) {
		this(name, regions, null, yaw, 0);
	}

	public SpawnModule(String name, List<Region> regions, float yaw, float pitch) {
		this(name, regions, null, yaw, pitch);
	}

	public SpawnModule(String name, List<Region> regions, KitModule kit) {
		this(name, regions, kit, 0, 0);
	}

	public SpawnModule(String name, List<Region> regions, KitModule kit, float yaw) {
		this(name, regions, kit, yaw, 0);
	}

	public SpawnModule(String name, List<Region> regions, KitModule kit, float yaw, float pitch) {
		super(name, regions);
		this.name = name;
		this.kit = kit;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public String getName() {
		return name;
	}

	public List<Region> getRegion() {
		return regions;
	}

	public Location getSpawn() {
		World world = Rotation.getMap().getWorld();
		List<BlockRegion> values = (safe ? getValues(Material.AIR, world) : getValues());
		if(values.size() == 0) {
			Log.warning("'" + getName() + "' has no values - error likely");
		}

		Location spawn = RegionUtil.getRandom(values).getLocation(world);
		spawn.setYaw(yaw);
		spawn.setPitch(pitch);
		return spawn;
	}

	public KitModule getKit() {
		return kit;
	}

	public boolean hasKit() {
		return kit != null;
	}

	public void setKit(KitModule kit) {
		this.kit = kit;
	}

}
