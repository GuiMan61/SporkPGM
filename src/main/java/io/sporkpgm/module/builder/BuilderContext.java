package io.sporkpgm.module.builder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.sporkpgm.Spork;
import io.sporkpgm.map.SporkLoader;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.match.Match;
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
				if(field.get(this) == null) {
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

		for(String string : fields) {
			try {
				for(Field field : getClass().getDeclaredFields()) {
					field.setAccessible(true);
					Log.debug("Field '" + string + "' = " + (field.get(this) != null ? field.get(this) : "null"));
					if(field.get(this) != null) {
						available.add(string);
					}
				}
			} catch(Exception e) {
				if(Spork.isDebug()) {
					e.printStackTrace();
				}
			}
		}

		return available.containsAll(fields) && fields.size() == available.size();
	}

}
