package io.sporkpgm.module.modules.region;

import com.google.common.collect.Lists;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.modules.region.exception.InvalidRegionException;
import org.jdom2.Element;

import java.util.List;

public enum RegionType {

	BLOCK(new String[]{"block", "point"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseBlock(element);
		}
	},
	RECTANGLE(new String[]{"rectangle"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseRectange(element);
		}
	},
	CUBOID(new String[]{"cuboid"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseCuboid(element);
		}
	},
	CIRCLE(new String[]{"circle"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseCircle(element);
		}
	},
	CYLINDER(new String[]{"cylinder"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseCylinder(element);
		}
	},
	SPHERE(new String[]{"sphere"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseSphere(element);
		}
	},
	VOID(new String[]{"void"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseVoid(element);
		}
	},
	NEGATIVE(new String[]{"negative"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseNegative(element);
		}
	},
	UNION(new String[]{"union"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseUnion(element);
		}
	},
	COMPLEMENT(new String[]{"complement"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseComplement(element);
		}
	},
	INTERSECT(new String[]{"intersect"}) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseIntersect(element);
		}
	},
	REGION(new String[]{"region"}, true) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseRegion(element, map);
		}
	},
	FILTER(new String[]{"apply"}, true) {
		@Override
		public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
			return RegionBuilder.parseFiltered(map, element);
		}
	};

	private String[] names;
	private boolean map;

	RegionType(String[] names) {
		this.names = names;
	}

	RegionType(String[] names, boolean map) {
		this.names = names;
		this.map = map;
	}

	public String[] getNames() {
		return names;
	}

	public List<String> getList() {
		return Lists.newArrayList(names);
	}

	public Region getRegion(Element element, SporkMap map) throws InvalidRegionException {
		return null;
	}

	public static Region get(Element element, SporkMap map) throws InvalidRegionException {
		boolean has = map != null;
		for(RegionType type : values()) {
			if(type.getList().contains(element.getName())) {
				if(type.map == has) {
					Region region = type.getRegion(element, map);
					if(region != null) {
						return region;
					}
				}
			}
		}

		return null;
	}

}
