package io.sporkpgm.event.module;

import io.sporkpgm.module.Module;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ModuleLoadEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private Module module;
	private boolean cancelled = false;
	private String reason = "Unknown";

	public ModuleLoadEvent(Module module) {
		this.module = module;
	}

	public Module getModule() {
		return module;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	public void setReason(String reason) {
		this.reason = (reason == null ? "Reason" : reason);
	}

	public String getReason() {
		return reason;
	}

	public void setCancelled(boolean cancelled, String reason) {
		setCancelled(cancelled);
		setReason(reason);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
