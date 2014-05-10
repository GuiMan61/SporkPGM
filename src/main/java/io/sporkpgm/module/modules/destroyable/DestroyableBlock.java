package io.sporkpgm.module.modules.destroyable;

import io.sporkpgm.module.modules.region.types.BlockRegion;
import io.sporkpgm.user.User;

public class DestroyableBlock {

	private boolean broken;
	private String uuid;
	private BlockRegion block;

	public DestroyableBlock(BlockRegion block) {
		this.block = block;
	}

	public void setComplete(User player, boolean broken) {
		setUser(player);
		setBroken(broken);
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}

	public String getUser() {
		return uuid;
	}

	public void setUser(User user) {
		this.uuid = user.getUUID();
	}

	public void setUser(String uuid) {
		this.uuid = uuid;
	}

	public boolean isBroken() {
		return broken;
	}

	public boolean isComplete() {
		return broken;
	}

	public BlockRegion getBlock() {
		return block;
	}

}
