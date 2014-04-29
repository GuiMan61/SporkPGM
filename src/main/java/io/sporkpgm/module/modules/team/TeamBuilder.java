package io.sporkpgm.module.modules.team;

import io.sporkpgm.module.builder.Builder;
import io.sporkpgm.module.builder.BuilderContext;
import io.sporkpgm.module.builder.BuilderInfo;

@BuilderInfo
public class TeamBuilder extends Builder {

	@Override
	public TeamModule[] array(BuilderContext context) {
		return new TeamModule[0];
	}

}
