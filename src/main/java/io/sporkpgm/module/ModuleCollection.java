package io.sporkpgm.module;

import io.sporkpgm.map.SporkLoader;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderFactory;

import java.util.ArrayList;
import java.util.List;

public class ModuleCollection implements Cloneable {

	private SporkLoader loader;
	private List<Module> modules;
	private SporkMap map;

	private ModuleCollection() {}

	public ModuleCollection(SporkLoader loader) {
		this.loader = loader;
		this.modules = new ArrayList<>();
	}

	public SporkLoader getLoader() {
		return loader;
	}

	public boolean hasModule(Class<?> type) {
		return getModule(type) != null;
	}

	public List<Module> getModules() {
		return modules;
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

	public void add(BuilderContext context) {
		add(BuilderFactory.get().getBuilders(), context);
	}

	public void add(Class<? extends Module> module, BuilderContext context) {
		this.modules.addAll(BuilderFactory.build(module, context));
	}

	public void add(Builder builder, BuilderContext context) {
		this.modules.addAll(BuilderFactory.build(builder, context));
	}

	public void add(List<Builder> builders, BuilderContext context) {
		for(Builder builder : builders) {
			this.modules.addAll(BuilderFactory.build(builder, context));
		}
	}

	public void add(BuilderContext context, Builder... builders) {
		for(Builder builder : builders) {
			this.modules.addAll(BuilderFactory.build(builder, context));
		}
	}

	public ModuleCollection clone() {
		return clone(null);
	}

	public ModuleCollection clone(SporkMap map) {
		ModuleCollection context = new ModuleCollection();
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
