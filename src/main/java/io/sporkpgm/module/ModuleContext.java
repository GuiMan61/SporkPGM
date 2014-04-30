package io.sporkpgm.module;

import io.sporkpgm.map.SporkLoader;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderFactory;
import io.sporkpgm.module.modules.info.InfoModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleContext implements Cloneable {

	private SporkLoader loader;
	private List<Module> modules;
	private SporkMap map;

	private ModuleContext() {}

	public ModuleContext(SporkLoader loader) {
		this.loader = loader;
		this.modules = new ArrayList<>();
	}

	public SporkLoader getLoader() {
		return loader;
	}

	public <T> T getModule(Class<T> type) {
		for(Module module : modules) {
			T cast = convertInstanceOfObject(module, type);
			if(cast != null) {
				return cast;
			}
		}

		return null;
	}

	public <T> List<T> getModules(Class<T> type) {
		List<T> results = new ArrayList<>();

		for(Module module : modules) {
			T cast = convertInstanceOfObject(module, type);
			if(cast != null) {
				results.add(cast);
			}
		}

		return results;
	}

	public void add(Class<? extends Module> module, BuilderContext context) {
		this.modules.addAll(BuilderFactory.build(module, context));
	}

	public ModuleContext clone() {
		return clone(null);
	}

	public ModuleContext clone(SporkMap map) {
		ModuleContext context = new ModuleContext();
		context.loader = loader;
		context.modules = new ArrayList<>();
		context.modules.addAll(modules);
		context.map = map;
		return context;
	}

	private static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
		try {
			return clazz.cast(o);
		} catch(ClassCastException e) {
			return null;
		}
	}

}
