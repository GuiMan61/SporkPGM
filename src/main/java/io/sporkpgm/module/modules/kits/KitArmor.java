package io.sporkpgm.module.modules.kits;

import org.bukkit.inventory.ItemStack;

public class KitArmor {

	private KitArmorSlot slot;
	private ItemStack item;

	public KitArmor(KitArmorSlot slot, ItemStack item) {
		this.slot = slot;
		this.item = item;
	}

	public KitArmorSlot getSlot() {
		return slot;
	}

	public ItemStack getItem() {
		return item;
	}
}
