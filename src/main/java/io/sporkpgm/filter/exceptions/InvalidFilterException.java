package io.sporkpgm.filter.exceptions;

import io.sporkpgm.module.exceptions.ModuleBuildException;
import org.jdom2.Element;

public class InvalidFilterException extends ModuleBuildException {

	private static final long serialVersionUID = 7383940129572041205L;

	public InvalidFilterException(String message) {
		super(message);
	}

	public InvalidFilterException(Element element, String message) {
		super(element, message);
	}

}
