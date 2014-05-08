package io.sporkpgm.util;

public class ParserUtil {

	public static boolean parseBoolean(String string) throws ParsingException {
		if(string == null) {
			throw new ParsingException(string, boolean.class, "String was null");
		}

		if(string.equalsIgnoreCase("true")) {
			return true;
		} else if(string.equalsIgnoreCase("false")) {
			return false;
		}

		throw new ParsingException(string, boolean.class, "String did not match true or false");
	}

	public static boolean parseBoolean(String string, boolean def) {
		try {
			return parseBoolean(string);
		} catch(ParsingException e) {
			return def;
		}
	}

	public static class ParsingException extends Exception {

		private static final long serialVersionUID = -1917708786175881090L;

		private String string;
		private Class<?> type;

		private String reason;
		private Throwable throwable;

		public ParsingException(String string, Class<?> type, String reason) {
			this.string = string;
			this.type = type;
			this.reason = reason;
		}

		public ParsingException(String string, Class<?> type, Throwable throwable) {
			this.throwable = throwable;
			this.string = string;
			this.type = type;
		}

		@Override
		public void printStackTrace() {
			throwable.printStackTrace();
		}

		@Override
		public String getMessage() {
			return string + " could not be cast to " + type.getName() + (throwable != null ? ": " + throwable.getMessage() : reason);
		}

	}

}
