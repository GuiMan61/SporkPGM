package io.sporkpgm;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import io.sporkpgm.map.SporkFactory;
import io.sporkpgm.module.builder.BuilderFactory;
import io.sporkpgm.module.modules.info.InfoModule;
import io.sporkpgm.module.modules.team.TeamModule;
import io.sporkpgm.util.Config;
import io.sporkpgm.util.Log;
import io.sporkpgm.util.SporkConfig.Maps;
import io.sporkpgm.util.uuid.HandleUUID;
import io.sporkpgm.util.uuid.MojangUUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;

import static io.sporkpgm.ListenerHandler.*;

public class Spork extends JavaPlugin {

	private static Spork instance;
	private static boolean debug;
	private static HandleUUID uuid = new MojangUUID();

	private Config config;
	private BuilderFactory factory;
	private CommandsManager<CommandSender> commands;
	private CommandsManagerRegistration registration;

	@Override
	public void onEnable() {
		instance = this;
		config = Config.load(this, "config.yml");

		register();
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	@Override
	public Config getConfig() {
		return config;
	}

	public void register() {
		new SporkFactory();
		for(File file : Maps.getFiles()) {
			SporkFactory.register(file);
		}

		this.factory = new BuilderFactory();
		this.factory.register(InfoModule.class);
		this.factory.register(TeamModule.class);

		this.commands = new CommandsManager<CommandSender>() {
			public boolean hasPermission(CommandSender sender, String perm) {
				return sender.hasPermission(perm);
			}
		};

		this.registration = new CommandsManagerRegistration(this, this.commands);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		try {
			this.commands.execute(cmd.getName(), args, sender, sender);
		} catch(CommandPermissionsException e) {
			sender.sendMessage(ChatColor.RED + "You don't have permission.");
		} catch(MissingNestedCommandException e) {
			sender.sendMessage(ChatColor.RED + e.getUsage());
		} catch(CommandUsageException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
			sender.sendMessage(ChatColor.RED + e.getUsage());
		} catch(WrappedCommandException e) {
			if(e.getCause() instanceof NumberFormatException) {
				sender.sendMessage(ChatColor.RED + "Number expected, string received.");
			} else {
				sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
				e.printStackTrace();
			}
		} catch(CommandException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}

		return true;
	}

	public static Spork get() {
		return instance;
	}

	public static BuilderFactory getFactory() {
		return get().factory;
	}

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean value) {
		debug = value;
		Log.setDebugging(value);
	}

	public static XMLOutputter getOutputter() {
		return new XMLOutputter(Format.getPrettyFormat());
	}

	public static File getRoot() {
		return get().getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
	}

	public static String getUUID(String string) {
		return uuid.getUUID(string);
	}

	public static String getName(String string) {
		return uuid.getName(string);
	}

}
