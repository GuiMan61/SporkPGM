package io.sporkpgm.scoreboard.exceptions;

import io.sporkpgm.scoreboard.SporkScoreboard;

public class IllegalScoreboardException extends Exception {

	private static final long serialVersionUID = -2665242363957986177L;

	private Class<?> type;
	private Throwable throwable;

	public IllegalScoreboardException(Class<?> type) {
		this.type = type;
	}

	public IllegalScoreboardException(Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public String getMessage() {
		return (type != null ? type.getName() + " could not be cast to " + SporkScoreboard.class.getName() : throwable != null ? throwable.getMessage() : "Unknown");
	}

	@Override
	public void printStackTrace() {
		if(throwable != null) {
			throwable.printStackTrace();
		} else {
			super.printStackTrace();
		}
	}

}
