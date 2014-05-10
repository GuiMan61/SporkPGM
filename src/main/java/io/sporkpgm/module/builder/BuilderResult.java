package io.sporkpgm.module.builder;

import com.google.common.collect.Lists;
import io.sporkpgm.ListenerHandler;
import io.sporkpgm.event.module.ModuleLoadEvent;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.module.exceptions.ModuleBuildException;
import io.sporkpgm.util.Log;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public enum BuilderResult {

	SINGLE {

		@Override
		public List<Module> result(Builder builder, BuilderContext context) {
			try {
				List<Module> modules = Lists.newArrayList(builder.single(context));
				return check(builder, modules, this);
			} catch(ModuleBuildException e) {
				e.printStackTrace();
			}

			return Lists.newArrayList();
		}

	},
	LIST {

		@Override
		public List<Module> result(Builder builder, BuilderContext context) {
			try {
				List<Module> modules = builder.list(context);
				return check(builder, modules, this);
			} catch(ModuleBuildException e) {
				e.printStackTrace();
			}

			return Lists.newArrayList();
		}

	};

	public List<Module> result(Builder builder, BuilderContext context) {
		return new ArrayList<>();
	}

	public static List<Module> check(Builder builder, List<Module> modules, BuilderResult result) {
		List<Module> results = new ArrayList<>();

		if(modules == null) {
			return results;
		}

		for(Module module : modules) {
			if(module == null) {
				if(result != SINGLE) {
					Log.warning("Module failed to load because it was null (" + builder.getClass().getSimpleName() + ")");
				}
				continue;
			}

			ModuleLoadEvent event = new ModuleLoadEvent(module);
			ListenerHandler.callEvent(event);
			if(!event.isCancelled()) {
				results.add(module);
			} else {
				Log.info("Unable to load " + module.getClass().getSimpleName() + "; " + event.getReason());
			}

			module.load();
			if(module instanceof Listener) {
				Listener listener = (Listener) module;
				ListenerHandler.registerListener(listener);
			}
		}

		return results;
	}

	public static List<Module> build(Class<? extends Module> module, BuilderContext context) {
		if(!module.isAnnotationPresent(ModuleInfo.class)) {
			Log.warning("No ModuleInfo present for " + module.getSimpleName() + " ignoring it");
			return new ArrayList<>();
		}

		ModuleInfo moduleInfo = module.getAnnotation(ModuleInfo.class);
		Class<? extends Builder> builderClass = moduleInfo.builder();
		if(builderClass == null) {
			Log.warning("Invalid Builder present for " + module.getSimpleName() + " ignoring it");
			return new ArrayList<>();
		}

		if(!module.isAnnotationPresent(BuilderInfo.class)) {
			Log.warning("No BuilderInfo present for " + module.getSimpleName() + " ignoring it");
			return new ArrayList<>();
		}

		BuilderInfo builderInfo = module.getAnnotation(BuilderInfo.class);
		Builder builder = BuilderFactory.get(module);

		return builderInfo.result().result(builder, context);
	}

	public static List<Module> build(Builder builder, BuilderContext context) {
		BuilderInfo builderInfo = builder.getClass().getAnnotation(BuilderInfo.class);
		return builderInfo.result().result(builder, context);
	}

}
