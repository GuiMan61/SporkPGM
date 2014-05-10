package io.sporkpgm.module.modules.spawn;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleBuildException;
import io.sporkpgm.module.modules.kits.KitModule;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.module.modules.region.Region;
import io.sporkpgm.module.modules.region.RegionBuilder;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.OtherUtil;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo
public class SpawnModuleBuilder extends Builder {

	@Override
	public SpawnModule[] array(BuilderContext context) throws ModuleBuildException {
		if(!context.only("map", "document")) {
			return null;
		}

		SporkMap map = context.getMap();
		Log.debug("Map is null: " + (map == null));

		List<SpawnModule> sporks = new ArrayList<>();
		Document document = context.getDocument();
		Element root = document.getRootElement();
		sporks.addAll(spawns(map, root));

		return OtherUtil.toArray(SpawnModule.class, sporks);
	}

	public static List<SpawnModule> spawns(SporkMap map, Element element) throws ModuleBuildException {
		List<SpawnModule> sporks = new ArrayList<>();

		sporks.addAll(parseSpawns(map, element.getChildren("spawn")));
		sporks.addAll(parseSpawns(map, element.getChildren("default")));
		if(element.getChild("spawns") != null) {
			for(Element spawns : element.getChildren("spawns")) {
				sporks.addAll(spawns(map, spawns));
			}
		}

		return sporks;
	}

	public static List<SpawnModule> parseSpawns(SporkMap map, List<Element> spawns) throws ModuleBuildException {
		List<SpawnModule> sporks = new ArrayList<>();

		for(Element element : spawns) {
			SpawnModule spawn = parseSpawn(map, element);
			sporks.add(spawn);
		}

		return sporks;
	}

	public static SpawnModule parseSpawn(SporkMap map, Element element) throws ModuleBuildException {
		String nameS = null;

		// Log.info("Parsing " + element.asXML());
		String teamS = element.getParentElement().getAttributeValue("team");
		String yawS = element.getParentElement().getAttributeValue("yaw");
		String pitchS = element.getParentElement().getAttributeValue("pitch");
		String kitS = element.getParentElement().getAttributeValue("kit");

		teamS = (element.getAttributeValue("team") != null ? element.getAttributeValue("team") : teamS);
		if(element.getName().equalsIgnoreCase("default")) {
			teamS = map.getTeams().getObservers().getName();
		}

		yawS = (element.getAttributeValue("yaw") != null ? element.getAttributeValue("yaw") : yawS);
		pitchS = (element.getAttributeValue("pitch") != null ? element.getAttributeValue("pitch") : pitchS);
		kitS = (element.getAttributeValue("kit") != null ? element.getAttributeValue("kit") : kitS);

		String name = (nameS == null ? "noname" : nameS);

		Log.debug("Map is null: " + (map == null));
		Log.debug("Map.getTeams() is null: " + (map.getTeams() == null));
		Log.debug("Map.getTeams().getTeam(" + '"' + teamS + '"' + ") is null: " + (map.getTeams().getTeam(teamS) == null));
		TeamModule team = map.getTeams().getTeam(teamS);

		List<Region> regions = RegionBuilder.parseSubRegions(element);
		float yaw = 0;
		float pitch = 0;

		try {
			yaw = Float.parseFloat(yawS);
		} catch(Exception ignored) {
		}

		try {
			pitch = Float.parseFloat(pitchS);
		} catch(Exception ignored) {
		}

		KitModule match = null;
		if(kitS != null) {
			for(KitModule ak : map.getKits()) {
				if(ak.getName().equalsIgnoreCase(kitS)) {
					match = ak;
					break;
				}
			}
			if(match == null)
				throw new ModuleBuildException(element, "Kit '" + kitS + "' not found!");
		}

		SpawnModule spawn = new SpawnModule(name, regions, match, yaw, pitch);
		team.getSpawns().add(spawn);
		return spawn;
	}

}
