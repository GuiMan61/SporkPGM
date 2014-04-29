package io.sporkpgm.module.builder;

import com.google.common.base.Preconditions;
import io.sporkpgm.map.SporkLoader;
import io.sporkpgm.map.SporkMap;
import io.sporkpgm.match.Match;
import org.jdom2.Document;

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

		Preconditions.checkState(passed.size() > 0, "Contexts require at least 1 field");
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

}
