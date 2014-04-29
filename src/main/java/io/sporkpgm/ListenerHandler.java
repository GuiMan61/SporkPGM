package io.sporkpgm;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class ListenerHandler {

	public static void registerListeners(Listener... listeners) {
		for(Listener listener : listeners) {
			registerListener(listener);
		}
	}

	public static void registerListener(Listener listener) {
		Spork.get().getServer().getPluginManager().registerEvents(listener, Spork.get());
	}

	public static void unregisterListeners(Listener... listeners) {
		for(Listener listener : listeners) {
			unregisterListener(listener);
		}
	}

	public static void unregisterListener(Listener listener) {
		HandlerList.unregisterAll(listener);
	}

	public static void callEvents(Event... events) {
		for(Event event : events) {
			Spork.get().getServer().getPluginManager().callEvent(event);
		}
	}

	public static void callEvent(Event event) {
		Spork.get().getServer().getPluginManager().callEvent(event);
	}

}
