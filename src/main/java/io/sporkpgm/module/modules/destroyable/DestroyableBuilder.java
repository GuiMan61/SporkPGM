package io.sporkpgm.module.modules.destroyable;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleBuildException;
import io.sporkpgm.module.modules.region.Region;
import io.sporkpgm.module.modules.region.RegionBuilder;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.util.OtherUtil;
import io.sporkpgm.util.StringUtil;
import io.sporkpgm.util.XMLUtil;
import org.bukkit.Material;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo
public class DestroyableBuilder extends Builder {

	@Override
	public DestroyableModule[] array(BuilderContext context) throws ModuleBuildException {
		if(!context.only("document", "loader", "map")) {
			return null;
		}

		SporkMap map = context.getMap();
		Document document = context.getDocument();
		List<DestroyableModule> modules = new ArrayList<>();
		modules.addAll(destroyables(map, document.getRootElement()));
		return OtherUtil.toArray(DestroyableModule.class, modules);
	}

	public List<DestroyableModule> destroyables(SporkMap map, Element element) throws ModuleBuildException {
		List<DestroyableModule> sporks = new ArrayList<>();

		sporks.addAll(parseDestroyables(map, element.getChildren("destroyable")));
		if(element.getChild("destroyables") != null) {
			for(Element spawns : element.getChildren("destroyables")) {
				sporks.addAll(destroyables(map, spawns));
			}
		}

		return sporks;
	}

	public List<DestroyableModule> parseDestroyables(SporkMap map, List<Element> elements) throws ModuleBuildException {
		List<DestroyableModule> objectives = new ArrayList<>();
		for(Element element : elements) {
			objectives.add(parseDestroyable(map, element));
		}
		return objectives;
	}

	public DestroyableModule parseDestroyable(SporkMap map, Element element) throws ModuleBuildException {
		String name = XMLUtil.getElementOrParentValue(element, "name");

		String[] names = new String[0];
		String types = XMLUtil.getElementOrParentValue(element, "materials");
		if(types != null) {
			names = new String[]{types};
			if(types.contains(";")) {
				types.split(";");
			}
		}

		List<Material> materialList = new ArrayList<>();
		for(String type : names) {
			Material material = StringUtil.convertStringToMaterial(type);
			if(material == null) {
				throw new ModuleBuildException("'" + type + "' is not a valid Minecraft material");
			}
			materialList.add(material);
		}

		int i = 0;
		Material[] materials = new Material[materialList.size()];
		for(Material material : materialList) {
			materials[i] = material;
			i++;
		}

		int completion = 0;
		String complete = XMLUtil.getElementOrParentValue(element, "completion");
		if(complete != null) {
			if(complete.endsWith("%")) {
				complete = complete.substring(0, complete.length() - 1);
			}

			completion = Integer.parseInt(complete);
		}

		TeamModule other = null;
		String team = XMLUtil.getElementOrParentValue(element, "owner");
		if(team != null) {
			other = map.getTeams().getTeam(team);
		}

		Region region = RegionBuilder.parseCuboid(element.getChildren().get(0));
		TeamModule owner = other.getOpposite();

		if(name == null) {
			throw new ModuleBuildException("A Destroyable name could not be found");
		} else if(materials.length == 0) {
			throw new ModuleBuildException("No Materials were supplied");
		} else if(materialList.contains(null)) {
			throw new ModuleBuildException("An invalid list of Materials was found");
		} else if(completion <= 0) {
			throw new ModuleBuildException("Completion % must be greater than 0");
		} else if(owner == null) {
			throw new ModuleBuildException("The owner of a Destroyable can't be null");
		} else if(region == null) {
			throw new ModuleBuildException("The region of a Destroyable can't be null");
		}

		return new DestroyableModule(name, owner, region, materials, completion);
	}
	
}
