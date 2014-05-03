package io.sporkpgm.map.exceptions;

public class MapLoadException extends Exception {

	private static final long serialVersionUID = 4743495996323223505L;

	private String message;

	public MapLoadException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
