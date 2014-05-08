package io.sporkpgm.module.modules.region.exception;

import io.sporkpgm.module.exceptions.ModuleBuildException;
import org.jdom2.Element;

public class InvalidRegionException extends ModuleBuildException {

	private static final long serialVersionUID = -8988962714866893205L;

	public InvalidRegionException(Element element, String message) {
		super(element, message);
	}

}
