package io.sporkpgm.module.modules.kits;

import com.google.common.collect.Lists;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderInfo;
import io.sporkpgm.module.exceptions.ModuleBuildException;
import io.sporkpgm.util.ItemUtil;
import io.sporkpgm.util.NumberUtil;
import io.sporkpgm.util.OtherUtil;
import io.sporkpgm.util.ParserUtil;
import io.sporkpgm.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BuilderInfo
public class KitModuleBuilder extends Builder {

	@Override
	public KitModule[] array(BuilderContext context) throws ModuleBuildException {
		if(!context.only("map", "document")) {
			return null;
		}

		List<KitModule> sporks = new ArrayList<>();

		Document document = context.getDocument();
		Element root = document.getRootElement();
		
		Element kits = root.getChild("kits");
		List<Element> children = kits != null ? kits.getChildren("kit") : new ArrayList<Element>();
		for(Element element : children) {
			sporks.add(parseKit(element));
		}

		for(KitModule kit : sporks) {
			String parents = kit.getElement().getAttributeValue("parents");
			if(parents == null) {
				kit.setParents(new ArrayList<KitModule>());
				continue;
			}

			String[] names;
			if(parents.contains(";")) {
				names = parents.split(";");
			} else {
				names = new String[]{parents};
			}

			List<KitModule> parents1 = new ArrayList<>();
			for(String name : names) {
				KitModule parent = getKit(name, sporks);
				if(parent == null) {
					throw new ModuleBuildException("Could not find kit with the name '" + name + "' for '" + kit.getName() + "'");
				}

				parents1.add(parent);
			}

			kit.setParents(parents1);
		}

		return OtherUtil.toArray(KitModule.class, sporks);
	}

	public static KitModule getKit(String name, List<KitModule> kits) {
		for(KitModule kit : kits) {
			if(kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}

		return null;
	}

	public static KitModule parseKit(Element root) throws ModuleBuildException {
		String name = root.getAttributeValue("name");

		String[] items = new String[]{"item"};
		List<KitItem> kitItems = new ArrayList<>();

		String[] potions = new String[]{"potion"};
		List<PotionEffect> kitPotions = new ArrayList<>();

		String[] armour = new String[]{"helmet", "chestplate", "leggings", "boots"};
		List<KitArmor> kitArmor = new ArrayList<>();

		for(String string : items) {
			for(Element element : root.getChildren(string)) {
				int slot = parseSlot(element, name);
				ItemStack stack = parseItem(element, name);
				kitItems.add(new KitItem(slot, stack));
			}
		}

		for(String string : armour) {
			for(Element element : root.getChildren(string)) {
				KitArmorSlot slot = parseArmorSlot(element, name);
				ItemStack stack = parseItem(element, name);
				kitArmor.add(new KitArmor(slot, stack));
			}
		}

		for(String string : potions) {
			for(Element element : root.getChildren(string)) {
				PotionEffect effect = parsePotion(element, name);
				kitPotions.add(effect);
			}
		}

		return new KitModule(root, name, kitItems, kitPotions, kitArmor);
	}

	public static ItemStack parseItem(Element element, String name) throws ModuleBuildException {
		short damage = parseDamage(element, name);
		int amount = parseAmount(element, name);
		Material material = parseMaterial(element, name);
		Map<Enchantment, Integer> enchantments = parseEnchantments(element, name);

		ItemStack stack = new ItemStack(material, amount, damage);
		stack.addUnsafeEnchantments(enchantments);

		parseColor(stack, element, name);
		parseLore(stack, element);
		return stack;
	}

	public static PotionEffect parsePotion(Element element, String name) throws ModuleBuildException {
		PotionEffectType type = StringUtil.convertStringToPotionEffectType(element.getText());
		if(type == null) {
			throw new ModuleBuildException(element, "Invalid potion type: '" + element.getText() + "' for '" + name + "'");
		}
		String rawDuration = element.getAttributeValue("duration");
		String rawAmplifier = element.getAttributeValue("amplifier");
		if(rawDuration == null) {
			throw new ModuleBuildException(element, "Potion duration or amplifier cannot be blank for '" + name + "'");
		}
		int duration = (rawDuration.equals("oo") ? Integer.MAX_VALUE : Integer.parseInt(rawDuration));
		int amplifier = (rawAmplifier != null ? Integer.parseInt(rawAmplifier) : 0);
		boolean ambient = ParserUtil.parseBoolean(element.getAttributeValue("ambient"), false);
		return new PotionEffect(type, duration, amplifier, ambient);
	}

	public static void parseColor(ItemStack stack, Element element, String name) throws ModuleBuildException {
		if(element.getAttributeValue("color") == null) {
			return;
		}

		if(!supportsColor().contains(stack.getType())) {
			String material = StringUtils.capitalize(stack.getType().name().replace("_", " "));
			throw new ModuleBuildException(element, "'" + material + "' can't have a color set for '" + name + "'");
		}

		String attribute = element.getAttributeValue("color");
		Color color;

		try {
			color = StringUtil.convertHexStringToColor(attribute);
		} catch(Exception e) {
			throw new ModuleBuildException(element, "Invalid Color supplied for '" + name + "'");
		}

		ItemUtil.setColor(stack, color);
	}

	public static List<Material> supportsColor() {
		Material[] supports = new Material[]{Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET};
		return Lists.newArrayList(supports);
	}

	public static KitArmorSlot parseArmorSlot(Element element, String name) throws ModuleBuildException {
		KitArmorSlot slot = KitArmorSlot.getSlot(element.getName());

		if(slot == null) {
			throw new ModuleBuildException(element, "Unable to find armour slot named '" + element.getName() + "' for '" + name + "'");
		}

		return slot;
	}

	public static int parseSlot(Element element, String name) throws ModuleBuildException {
		int slot;
		try {
			slot = Integer.parseInt(element.getAttributeValue("slot"));
		} catch(NumberFormatException | NullPointerException e) {
			throw new ModuleBuildException(element, "Could not parse kit because slot was not a valid number for '" + name + "'");
		}

		return slot;
	}

	public static short parseDamage(Element element, String name) throws ModuleBuildException {
		short damage;
		try {
			String string = element.getAttributeValue("damage");
			if(string == null) {
				throw new NullPointerException(string);
			}

			damage = (short) Integer.parseInt(string);
		} catch(NumberFormatException e) {
			throw new ModuleBuildException(element, "Could not parse kit because damage was provided but was not a valid number for '" + name + "'");
		} catch(NullPointerException e) {
			damage = 0;
		}

		return damage;
	}

	public static int parseAmount(Element element, String name) throws ModuleBuildException {
		int amount;
		try {
			String string = element.getAttributeValue("amount");
			if(string == null) {
				throw new NullPointerException(string);
			}

			amount = Integer.parseInt(string);
		} catch(NumberFormatException e) {
			throw new ModuleBuildException(element, "Could not parse kit because amount was provided but was not a valid number for '" + name + "'");
		} catch(NullPointerException e) {
			amount = 1;
		}

		return amount;
	}

	public static Material parseMaterial(Element element, String name) throws ModuleBuildException {
		Material material = StringUtil.convertStringToMaterial(element.getText());
		if(material == null) {
			throw new ModuleBuildException(element, "Invalid Material name supplied for '" + name + "'");
		}

		return material;
	}

	public static Map<Enchantment, Integer> parseEnchantments(Element element, String name) throws ModuleBuildException {
		if(element.getAttributeValue("enchantment") == null) {
			return new HashMap<>();
		}

		String enchants = element.getAttributeValue("enchantment");
		Map<Enchantment, Integer> res = new HashMap<>();
		for(String string : enchants.split(";")) {
			Enchantment enchantment;
			int level;

			if(string.split(":").length < 2) {
				enchantment = StringUtil.convertStringToEnchantment(string);
				level = 1;
			} else {
				enchantment = StringUtil.convertStringToEnchantment(string.split(":")[0]);
				level = NumberUtil.parseInteger(string.split(":")[1]);
			}

			if(enchantment == null) {
				throw new ModuleBuildException(element, "Invalid enchantment: '" + string + "' for '" + name + "'");
			}

			res.put(enchantment, level);
		}
		return res;
	}

	public static void parseLore(ItemStack stack, Element element) {
		String rawLore = element.getAttributeValue("lore");
		String name = element.getAttributeValue("name");

		if(name != null) {
			name = StringUtil.colorize(name);
			ItemUtil.setName(stack, name);
		}

		if(rawLore != null) {
			List<String> lore = new ArrayList<>();
			for(String s : rawLore.split("|")) {
				lore.add(StringUtil.colorize(s));
			}
			ItemUtil.setLore(stack, lore);
		}
	}

}
