package com.bnd.chemistry.domain;

public enum IllegalStateEventHandling {
	Ignore, FixSilently, FixAndCreateEvent, Stop, ThrowException;

	public boolean isIgnore() {
		return this == Ignore;
	}

	public boolean isFix() {
		return this == FixSilently;
	}

	public boolean isStop() {
		return this == Stop;
	}

	public boolean isThrowException() {
		return this == ThrowException;
	}
}