package io.sporkpgm.module.exceptions;

import io.sporkpgm.Spork;
import org.jdom2.Element;

public class ModuleBuildException extends Exception {

	private static final long serialVersionUID = 4743495996323223505L;

	private Element element;
	private String message;

	public ModuleBuildException(String message) {
		this.element = element;
		this.message = message;
	}

	public ModuleBuildException(Element element, String message) {
		this.element = element;
		this.message = message;
	}

	@Override
	public String getMessage() {
		String sep = System.getProperty("line.separator");

		String message = this.message;
		if(element != null) {
			message = message + ": " + sep + Spork.getOutputter().outputString(element);
		}

		return message;
	}

}
