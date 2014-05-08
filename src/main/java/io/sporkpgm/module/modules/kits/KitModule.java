package io.sporkpgm.module.modules.kits;

import com.google.common.collect.Lists;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.user.User;
import io.sporkpgm.util.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.jdom2.Element;

import java.util.List;

@ModuleInfo(description = "Holds a set of items which will be added to a player's inventory", builder = KitModuleBuilder.class)
public class KitModule extends Module {

	private Element element;

	private String name;
	private List<KitItem> items;
	private List<PotionEffect> potions;
	private List<KitArmor> armors;
	private List<KitModule> parents;

	public KitModule(Element element, String name, List<KitItem> items) {
		this(element, name, items, null, null, null);
	}

	public KitModule(Element element, String name, List<KitItem> items, List<PotionEffect> potions) {
		this(element, name, items, potions, null, null);
	}

	public KitModule(Element element, String name, List<KitItem> items, List<PotionEffect> potions, List<KitArmor> armor) {
		this(element, name, items, potions, null, armor);
	}

	public KitModule(Element element, String name, List<KitItem> items, List<PotionEffect> potions, List<KitModule> parents, List<KitArmor> armor) {
		this.element = element;
		this.name = name;
		this.items = items;
		this.potions = potions;
		this.armors = armor;

		if(parents != null && !parents.isEmpty()) {
			this.parents = parents;
		} else {
			this.parents = Lists.newArrayList();
		}
	}

	public void apply(User player) {
		apply(player.getPlayer());
	}

	public void apply(Player player) {
		PlayerInventory inv = player.getInventory();
		Log.info("Actually applying kit..." + name);

		if(items != null) {
			Log.info(items.size() + " items in '" + name + "'");
		}

		for(KitItem item : items) {
			inv.setItem(item.getSlot(), item.getItem().clone());
		}

		if(potions != null) {
			Log.info(potions.size() + " potions in '" + name + "'");
			// player.addPotionEffects(potions);
		}

		if(armors != null) {
			Log.info(armors.size() + " armor slots used in '" + name + "'");
			for(KitArmor armor : armors) {
				switch(armor.getSlot()) {
					case HELMET:
						inv.setHelmet(armor.getItem().clone());
					case CHESTPLATE:
						inv.setChestplate(armor.getItem().clone());
					case LEGGINGS:
						inv.setLeggings(armor.getItem().clone());
					case BOOTS:
						inv.setBoots(armor.getItem().clone());
				}
			}
		}
		if(parents != null) {
			Log.info(parents.size() + " parents applying in " + name);
			for(KitModule kit : parents) {
				kit.apply(player);
			}
		}
	}

	public void apply(TeamModule team) {
		for(User p : team.getPlayers()) {
			apply(p);
		}
	}

	public Element getElement() {
		return element;
	}

	public String getName() {
		return name;
	}

	public List<PotionEffect> getPotions() {
		return potions;
	}

	public List<KitItem> getItems() {
		return items;
	}

	public List<KitModule> getParents() {
		return parents;
	}

	public boolean hasParents() {
		return parents.isEmpty();
	}

	public void setParents(List<KitModule> parents) {
		this.parents = parents;
	}

	public void addParent(KitModule parent) {
		parents.add(parent);
	}

	public void removeParent(KitModule parent) {
		parents.remove(parent);
	}

	public void removeParentById(int id) {
		parents.remove(id);
	}

}
