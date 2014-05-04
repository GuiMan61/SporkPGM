package io.sporkpgm.util.uuid;

import io.sporkpgm.util.Log;
import io.sporkpgm.util.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MojangUUID implements HandleUUID {

	public String getName(final String uuid) {
		final DataStore store = new DataStore(getPlayerName(uuid));
		if(store.getData() != null) {
			return store.getData();
		}

		return getData(store, new Runnable() {
			@Override
			public void run() {
				store.setData(MojangQuery.getName(uuid));
			}
		}, 4);
	}

	public String getUUID(final String username) {
		final DataStore store = new DataStore(getPlayerUUID(username));
		if(store.getData() != null) {
			return store.getData();
		}

		return getData(store, new Runnable() {
			@Override
			public void run() {
				store.setData(MojangQuery.getUUID(username));
			}
		}, 4);
	}

	private String getData(final DataStore store, Runnable runnable, int timeout) {
		SchedulerUtil fetch = new SchedulerUtil(runnable, true);

		SchedulerUtil delay = new SchedulerUtil(new Runnable() {
			@Override
			public void run() { /* nothing */ }
		}, true);

		fetch.run();
		delay.delay(timeout * 20);

		while(store.getData() == null) {
			if(!delay.isRunning()) {
				break;
			}
		}

		return store.getData();
	}

	public static String getPlayerUUID(String username) {
		Log.debug("Searching for a Player called " + username);
		if(Bukkit.getPlayerExact(username) != null) {
			Player player = Bukkit.getPlayerExact(username);
			Log.debug("Found a player called " + username + " (" + player + ")");
			return player.getUniqueId().toString().replace("-", "");
		}

		Log.debug("Could not find a Player called " + username);
		return null;
	}

	public static String getPlayerName(String uuid) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			Log.debug("Testing " + player.getUniqueId().toString().replace("-", "") + " against " + uuid);
			if(player.getUniqueId().toString().replace("-", "").equals(uuid)) {
				Log.debug(player.getName() + " is a match for " + uuid);
				return player.getName();
			}
		}

		Log.debug("Could not find a Player matching " + uuid);
		return null;
	}

	public class DataStore {

		private String data;

		public DataStore(String data) {
			this.data = data;
		}

		public DataStore() {
			this(null);
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

}
