package com.bnd.core.domain;

import java.util.List;

final public class TimeState<T> {

	private final Double time;
	private final List<T> state; 

	public TimeState(Double time, List<T> state) {
		this.time = time;
		this.state = state;
	}

	public Double time() {
		return time;
	}

	public List<T> state() {
		return state;
	}
}