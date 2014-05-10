package io.sporkpgm.module.builder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkLoader;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.match.Match;
import io.sporkpgm.util.ClassUtils;
import io.sporkpgm.util.Log;
import org.jdom2.Document;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BuilderContext {

	private Document document;
	private SporkLoader loader;
	private SporkMap map;
	private Match match;

	public BuilderContext(Object... fields) {
		Preconditions.checkArgument(fields.length > 0, "Contexts require at least 1 field");
		List<Object> passed = new ArrayList<>();

		for(Object field : fields) {
			passed.add(field);
			if(field instanceof Document) {
				document = (Document) field;
				continue;
			} else if(field instanceof SporkLoader) {
				loader = (SporkLoader) field;
				continue;
			} else if(field instanceof SporkMap) {
				map = (SporkMap) field;
				continue;
			} else if(field instanceof Match) {
				match = (Match) field;
				continue;
			}
			passed.remove(field);
		}

		Preconditions.checkState(passed.size() > 0, "Contexts require at acceptable least 1 field");
	}

	public Document getDocument() {
		return document;
	}

	public SporkLoader getLoader() {
		return loader;
	}

	public SporkMap getMap() {
		return map;
	}

	public Match getMatch() {
		return match;
	}

	public boolean all(String... fields) {
		return all(Lists.newArrayList(fields));
	}

	public boolean all(List<String> fields) {
		for(String string : fields) {
			try {
				Field field = getClass().getDeclaredField(string);
				field.setAccessible(true);
				if(!has(field.getName())) {
					return false;
				}
			} catch(Exception e) {
				if(Spork.isDebug()) {
					e.printStackTrace();
				}

				return false;
			}
		}

		return true;
	}

	public boolean only(String... fields) {
		return only(Lists.newArrayList(fields));
	}

	public boolean only(List<String> fields) {
		List<String> available = new ArrayList<>();

		for(Field field : getClass().getDeclaredFields()) {
			if(has(field.getName())) {
				available.add(field.getName());
			}
		}

		Log.debug("Checking " + fields + " against " + available);

		if(fields.size() != available.size()) {
			return false;
		}

		for(String string : available) {
			if(!fields.contains(string)) {
				return false;
			}
		}

		return true;
	}

	public boolean has(String field) {
		switch(field) {
			case "document": return document != null;
			case "loader": return loader != null;
			case "map": return map != null;
			case "match": return match != null;
		}

		return false;
	}

	@Override
	public String toString() {
		return ClassUtils.build(getClass(), this);
	}

}
