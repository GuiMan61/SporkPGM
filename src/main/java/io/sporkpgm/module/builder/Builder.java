package io.sporkpgm.module.builder;

import com.google.common.collect.Lists;
import io.sporkpgm.module.Module;
import io.sporkpgm.module.exceptions.ModuleBuildException;

import java.util.List;

public class Builder {

	public Module single(BuilderContext context) throws ModuleBuildException {
		return null;
	}

	public Module[] array(BuilderContext context) throws ModuleBuildException {
		return null;
	}

	public List<Module> list(BuilderContext context) throws ModuleBuildException {
		Module[] array = array(context);
		if(array == null) {
			return null;
		}

		return Lists.newArrayList(array);
	}

}
