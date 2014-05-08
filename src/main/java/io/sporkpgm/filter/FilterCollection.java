package io.sporkpgm.filter;

import io.sporkpgm.map.SporkMap;

import java.util.ArrayList;
import java.util.List;

public class FilterCollection {

	private SporkMap map;

	public FilterCollection(SporkMap map) {
		this.map = map;
	}

	public SporkMap getMap() {
		return map;
	}

	public List<Filter> getFilters() {
		return map.getModules().getModules(Filter.class);
	}

	public List<Filter> getFilters(String search) {
		List<Filter> test = getFilters();

		List<Filter> filters = new ArrayList<>();
		for(Filter filter : test) {
			if(filter.getName().equals(search)) {
				filters.add(filter);
			}
		}

		return filters;
	}

	public Filter getFilter(String search) {
		List<Filter> filters = getFilters(search);
		return (filters.size() > 0 ? filters.get(0) : null);
	}

}
