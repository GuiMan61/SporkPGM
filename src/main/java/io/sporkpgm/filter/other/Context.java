package io.sporkpgm.filter.other;

import io.sporkpgm.event.map.BlockChangeEvent;
import io.sporkpgm.event.user.PlayingUserMoveEvent;
import io.sporkpgm.filter.exceptions.InvalidContextException;
import io.sporkpgm.user.User;
import org.bukkit.Location;

public class Context {

	private boolean denied;
	private boolean messaged;

	private User user;
	private BlockChangeEvent block;
	private BlockChangeEvent transformation;
	private PlayingUserMoveEvent movement;

	public Context(User user, BlockChangeEvent block, BlockChangeEvent transformation, PlayingUserMoveEvent movement) {
		this.user = user;
		this.block = block;
		this.transformation = transformation;
		this.movement = movement;
	}
	
	public Context(Object... objects) throws InvalidContextException {
		for(Object object : objects) {
			fill(object);
		}
	}

	private void fill(Object object) throws InvalidContextException {
		if(object instanceof User) {
			this.user = (User) object;
		} else if(object instanceof BlockChangeEvent) {
			BlockChangeEvent event = (BlockChangeEvent) object;
			if(event.hasPlayer()) {
				this.block = event;
				this.user = event.getPlayer();
			} else {
				this.transformation = event;
			}
		} else if(object instanceof PlayingUserMoveEvent) {
			PlayingUserMoveEvent event = (PlayingUserMoveEvent) object;
			this.movement = event;
			this.user = event.getPlayer();
		}

		/*
		throw new InvalidContextException("Attempted to supply an Object which was unsupported");
		*/
	}

	public boolean isDenied() {
		return denied;
	}

	public boolean isMessaged() {
		return messaged;
	}

	public void setMessaged(boolean messaged) {
		this.messaged = messaged;
	}

	public User getPlayer() {
		return user;
	}

	public boolean hasPlayer() {
		return user != null;
	}

	public BlockChangeEvent getBlock() {
		return block;
	}

	public boolean hasBlock() {
		return block != null;
	}

	public BlockChangeEvent getTransformation() {
		return transformation;
	}

	public boolean hasTransformation() {
		return transformation != null;
	}

	public BlockChangeEvent getModification() {
		return block != null ? block : transformation;
	}

	public boolean hasModification() {
		return getModification() != null;
	}

	public PlayingUserMoveEvent getMovement() {
		return movement;
	}

	public boolean hasMovement() {
		return movement != null;
	}

	public void deny() {
		this.denied = true;

		if(hasModification()) {
			getModification().setCancelled(true);
		}

		if(hasMovement()) {
			PlayingUserMoveEvent move = getMovement();
			double x = Math.floor(move.getFrom().getX()) + 0.5D;
			double y = move.getFrom().getY();
			double z = Math.floor(move.getFrom().getZ()) + 0.5D;

			float yaw = move.getTo().getYaw();
			float pitch = move.getTo().getPitch();
			getPlayer().getPlayer().teleport(new Location(move.getFrom().getWorld(), x, y, z, yaw, pitch));
		}
	}

}
