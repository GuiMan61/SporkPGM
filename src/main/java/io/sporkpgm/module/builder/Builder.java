package io.sporkpgm.module.builder;

import com.google.common.collect.Lists;
import io.sporkpgm.module.Module;

import java.util.List;

public class Builder {

	public Module single(BuilderContext context) {
		return null;
	}

	public Module[] array(BuilderContext context) {
		return null;
	}

	public List<Module> list(BuilderContext context) {
		Module[] array = array(context);
		if(array == null) {
			return null;
		}

		return Lists.newArrayList(array);
	}

}
