package io.sporkpgm.module.modules.team;

import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleBuildException;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.OtherUtil;
import io.sporkpgm.util.StringUtil;
import org.bukkit.ChatColor;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.List;

@BuilderInfo
public class TeamBuilder extends Builder {

	@Override
	public TeamModule[] array(BuilderContext context) {
		if(!context.only("map", "document")) {
			return null;
		}

		SporkMap map = context.getMap();
		Document document = context.getDocument();
		Element root = document.getRootElement();

		List<TeamModule> teams = new ArrayList<>();

		Element teamsElement = root.getChild("teams");
		for(Element team : teamsElement.getChildren("team")) {
			try {
				String nameAttribute = team.getText();
				Attribute colorAttribute = team.getAttribute("color");
				Attribute maxAttribute = team.getAttribute("max");
				Attribute overflowAttribute = team.getAttribute("max-overfill");
				Attribute overheadAttribute = team.getAttribute("overhead-color");

				if(colorAttribute == null || maxAttribute == null || nameAttribute == null) {
					String name = "name[" + (nameAttribute != null ? nameAttribute : "null") + "]";
					String color = "color[" + (colorAttribute != null ? colorAttribute.getValue() : "null") + "]";
					String max = "max[" + (maxAttribute != null ? maxAttribute.getValue() : "null") + "]";

					throw new ModuleBuildException("A team was missing required arguments: " + name + ", " + color + ", " + max);
				}

				ChatColor color = StringUtil.convertStringToChatColor(colorAttribute.getValue());
				if(color == null)
					throw new ModuleBuildException("A ChatColor for a team was invalid");

				int max = -1;
				try {
					max = StringUtil.convertStringToInteger(maxAttribute.getValue());
				} catch(Exception ignored) {
				}

				if(max < 1)
					throw new ModuleBuildException("A max players for a team was invalid");

				int overfill;
				try {
					overfill = StringUtil.convertStringToInteger(overflowAttribute.getValue());
				} catch(Exception e) {
					Log.debug("Failed to parse value of max-overfill ('" + overflowAttribute.getValue() + "') to an Integer");
					overfill = max + (max / 4);
				}

				ChatColor overhead = color;
				if(overheadAttribute != null) {
					ChatColor possible = StringUtil.convertStringToChatColor(overheadAttribute.getValue());
					if(possible != null)
						overhead = possible;
				}

				teams.add(new TeamModule(map, nameAttribute, color, overhead, max, overfill, false));
			} catch(ModuleBuildException e) {
				Log.warning("Invalid Team: " + e.getMessage());
			}
		}

		return OtherUtil.toArray(TeamModule.class, teams);
	}

	public static TeamModule observers(SporkMap map) {
		return new TeamModule(map, "Observers", ChatColor.AQUA, ChatColor.AQUA, 0, 0, true);
	}

}
