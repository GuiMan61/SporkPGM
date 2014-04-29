package io.sporkpgm.module.builder;

import io.sporkpgm.Spork;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.ModuleInfo;
import io.sporkpgm.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class BuilderFactory {

	private static BuilderFactory instance;

	private List<Builder> builders;

	public BuilderFactory() {
		instance = this;
		builders = new ArrayList<>();
	}

	public void register(Class<? extends Module> module) {
		ModuleInfo moduleInfo = module.getAnnotation(ModuleInfo.class);
		Class<? extends Builder> builderClass = moduleInfo.builder();
		if(builderClass == null) {
			Log.warning("Invalid Builder present for " + module.getSimpleName() + " ignoring it");
			return;
		}

		try {
			Constructor constructor = builderClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			Builder builder = (Builder) constructor.newInstance();
			builders.add(builder);
		} catch(Exception e) {
			if(Spork.isDebug()) {
				e.printStackTrace();
			}
		}
	}

	public static BuilderFactory get() {
		return instance;
	}

	public static Builder get(Class<? extends Module> clazz) {
		List<Builder> builders = get().builders;
		ModuleInfo moduleInfo = clazz.getAnnotation(ModuleInfo.class);
		Class<? extends Builder> builderClass = moduleInfo.builder();
		if(builderClass == null) {
			Log.warning("Invalid Builder present for " + clazz.getSimpleName() + " ignoring it");
			return null;
		}

		for(Builder builder : builders) {
			if(builder.getClass().equals(builderClass)) {
				return builder;
			}
		}

		return null;
	}

	public static List<Module> build(Class<? extends Module> clazz, BuilderContext context) {
		return BuilderResult.build(clazz, context);
	}

}
