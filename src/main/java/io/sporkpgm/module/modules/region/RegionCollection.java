package io.sporkpgm.module.modules.region;

import io.sporkpgm.map.SporkMap;

import java.util.ArrayList;
import java.util.List;

public class RegionCollection {

	private SporkMap map;

	public RegionCollection(SporkMap map) {
		this.map = map;
	}

	public SporkMap getMap() {
		return map;
	}

	public List<Region> getRegions() {
		return map.getModules().getModules(Region.class);
	}

	public List<Region> getRegions(String search) {
		List<Region> test = getRegions();

		List<Region> regions = new ArrayList<>();
		for(Region region : test) {
			if(region.getName().equals(search)) {
				regions.add(region);
			}
		}

		return regions;
	}

	public Region getRegion(String search) {
		List<Region> regions = getRegions(search);
		return (regions.size() > 0 ? regions.get(0) : null);
	}

}
