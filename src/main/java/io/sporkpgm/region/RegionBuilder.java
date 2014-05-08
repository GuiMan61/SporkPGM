package io.sporkpgm.region;

import com.google.common.collect.Lists;
import io.sporkpgm.filter.AppliedRegion;
import io.sporkpgm.filter.AppliedRegion.AppliedValue;
import io.sporkpgm.filter.Filter;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleBuildException;
import io.sporkpgm.region.exception.InvalidRegionException;
import io.sporkpgm.region.types.*;
import io.sporkpgm.region.types.groups.ComplementRegion;
import io.sporkpgm.region.types.groups.IntersectRegion;
import io.sporkpgm.region.types.groups.NegativeRegion;
import io.sporkpgm.region.types.groups.UnionRegion;
import io.sporkpgm.util.OtherUtil;
import org.jdom2.Attribute;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BuilderInfo
public class RegionBuilder extends Builder {

	@Override
	public Region[] array(BuilderContext context) throws ModuleBuildException {
		if(context.only("document") || context.only("document", "map")) {
			return OtherUtil.toArray(Region.class, parseSubRegions(context.getDocument().getRootElement().getChild("regions")));
		}

		return null;
	}

	public static List<Region> parseSubRegions(Element ele) throws InvalidRegionException {
		return parseSubRegions(ele, null);
	}

	public static List<Region> parseSubRegions(Element ele, SporkMap map) throws InvalidRegionException {
		if(ele == null) {
			return new ArrayList<>();
		}

		List<Region> regions = Lists.newArrayList();

		for(Element element : ele.getChildren()) {
			Region region = parseRegion(element);
			if(region != null) {
				regions.add(region);
			}
		}
		return regions;
	}

	public static Region parseRegion(Element ele) throws InvalidRegionException {
		return parseRegion(ele, null);
	}

	public static Region parseRegion(Element ele, SporkMap map) throws InvalidRegionException {
		return RegionType.get(ele, map);
	}

	public static BlockRegion parseBlock(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");

		String text = ele.getText();
		if(text == null || text.isEmpty()) {
			text = ele.getAttributeValue("location");
		}

		if(text == null) {
			throw new InvalidRegionException(ele, "BlockRegions must have an X, a Y and a Z in the Element text or location attribute");
		}

		String[] split = text.split(",");
		if(split.length != 3) {
			throw new InvalidRegionException(ele, "BlockRegions must have an X, a Y and a Z");
		}

		String x = split[0];
		String y = split[1];
		String z = split[2];

		if(isUsable(x) && isUsable(y) && isUsable(z)) {
			return new BlockRegion(name, x, y, z);
		}

		return null;
	}

	public static RectangleRegion parseRectange(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");
		if(ele.getAttributeValue("min") == null || ele.getAttributeValue("max") == null) {
			throw new InvalidRegionException(ele, "Both the minimum and the maximum values can't be null");
		}

		String[] minS = ele.getAttributeValue("min").split(",");
		String[] maxS = ele.getAttributeValue("max").split(",");
		if(minS.length != 2 || maxS.length != 2) {
			throw new InvalidRegionException(ele, "Both the minimum and maximum values should have an X and a Y");
		}

		String y = "oo"; // infinite y
		BlockRegion min = new BlockRegion(minS[0], "-" + y, minS[1]);
		BlockRegion max = new BlockRegion(maxS[0], y, maxS[1]);
		return new RectangleRegion(name, min, max);
	}

	public static CuboidRegion parseCuboid(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");
		List<BlockRegion> blocks = new ArrayList<>();

		String[] values = new String[]{"min", "max"};
		for(String attr : values) {
			Attribute attribute = ele.getAttribute(attr);
			if(attribute == null) {
				throw new InvalidRegionException(ele, "The " + attr + "imum value can't be null");
			}

			String[] split = attribute.getValue().split(",");
			if(split.length != 3) {
				throw new InvalidRegionException(ele, "BlockRegions must have an X, a Y and a Z ('" + attr + "')");
			}

			String x = split[0];
			String y = split[1];
			String z = split[2];

			if(isUsable(x) && isUsable(y) && isUsable(z)) {
				blocks.add(new BlockRegion(name, x, y, z));
			} else {
				throw new InvalidRegionException(ele, "Unsupported X, Y or Z value for '" + attr + "'");
			}
		}

		if(blocks.size() != 2) {
			throw new InvalidRegionException(ele, "CuboidRegions require a minimum and a maximum value");
		}

		return new CuboidRegion(name, blocks.get(0), blocks.get(1));
	}

	public static CircleRegion parseCircle(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");
		double radius;
		try {
			radius = Double.parseDouble(ele.getAttributeValue("radius"));
		} catch(Exception e) {
			throw new InvalidRegionException(ele, "Radius was not a valid double");
		}

		if(ele.getAttributeValue("center") == null) {
			throw new InvalidRegionException(ele, "The center point of a circle can't be null");
		}

		String[] split = ele.getAttributeValue("center").split(",");
		if(split.length != 2) {
			throw new InvalidRegionException(ele, "The center point of a circle only accepts X and Z values");
		}

		String x = split[0];
		String y = "oo";
		String z = split[1];
		BlockRegion center = new BlockRegion(x, y, z);

		return new CircleRegion(name, center, radius, false);
	}

	public static CylinderRegion parseCylinder(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");
		double radius;
		try {
			radius = Double.parseDouble(ele.getAttributeValue("radius"));
		} catch(Exception e) {
			throw new InvalidRegionException(ele, "Radius was not a valid double");
		}

		double height;
		try {
			height = Double.parseDouble(ele.getAttributeValue("height"));
		} catch(Exception e) {
			throw new InvalidRegionException(ele, "Height was not a valid double");
		}

		if(ele.getAttributeValue("base") == null) {
			throw new InvalidRegionException(ele, "The base point of a cylinder can't be null");
		}

		String[] split = ele.getAttributeValue("base").split(",");
		if(split.length != 3) {
			throw new InvalidRegionException(ele, "The base point of a cylinder requires X, Y and Z values");
		}

		String x = split[0];
		String y = split[1];
		String z = split[2];
		BlockRegion center = new BlockRegion(x, y, z);

		return new CylinderRegion(name, center, radius, height, false);
	}

	public static SphereRegion parseSphere(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");
		double radius;
		try {
			radius = Double.parseDouble(ele.getAttributeValue("radius"));
		} catch(Exception e) {
			throw new InvalidRegionException(ele, "Radius was not a valid double");
		}

		if(ele.getAttributeValue("center") == null) {
			throw new InvalidRegionException(ele, "The center point of a sphere can't be null");
		}

		String[] split = ele.getAttributeValue("center").split(",");
		if(split.length != 3) {
			throw new InvalidRegionException(ele, "The center point of a sphere requires X, Y and Z values");
		}

		String x = split[0];
		String y = split[1];
		String z = split[2];
		BlockRegion center = new BlockRegion(x, y, z);

		return new SphereRegion(name, center, radius, false);
	}

	public static VoidRegion parseVoid(Element ele) {
		String name = ele.getAttributeValue("name");
		return new VoidRegion(name);
	}

	public static NegativeRegion parseNegative(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");
		return new NegativeRegion(name, parseSubRegions(ele));
	}

	public static UnionRegion parseUnion(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");
		return new UnionRegion(name, parseSubRegions(ele));
	}

	public static ComplementRegion parseComplement(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");
		List<Region> regions = parseSubRegions(ele);
		return new ComplementRegion(name, regions.get(0), regions.subList(1, regions.size()));
	}

	public static IntersectRegion parseIntersect(Element ele) throws InvalidRegionException {
		String name = ele.getAttributeValue("name");
		return new IntersectRegion(name, parseSubRegions(ele));
	}

	public static AppliedRegion parseFiltered(SporkMap map, Element element) throws InvalidRegionException {
		String name = element.getAttributeValue("name");
		List<Region> regions = parseSubRegions(element);
		List<Filter> filters = new ArrayList<>();

		Map<AppliedValue, Object> hash = new HashMap<>();
		/*
		for(AppliedValue key : AppliedValue.values()) {
			Object value = null;
			if(key.getReturns() == Filter.class) {
				Filter filter = map.getFilter(element.getAttributeValue(key.getAttribute()));
				value = filter;
				if(!filters.contains(filter)) {
					filters.add(filter);
				}
			} else if(key.getReturns() == SporkKit.class) {
				value = map.getKit(element.attributeValue(key.getAttribute()));
			} else if(key.getReturns() == String.class) {
				value = element.attributeValue(key.getAttribute());
			}

			hash.put(key, value);
		}
		*/

		return new AppliedRegion(name, hash, regions, filters);
	}

	public static boolean isUsable(String value) {
		if(value.equals("oo") || value.equals("-oo")) {
			return true;
		}

		try {
			Double.valueOf(value);
			return true;
		} catch(NumberFormatException ignored) {
		}

		return false;
	}

}