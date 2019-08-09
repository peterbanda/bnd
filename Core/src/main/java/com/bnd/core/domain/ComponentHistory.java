package com.bnd.core.domain;

import java.util.List;

final public class ComponentHistory<T, C> {

	private final C component;
	private final List<T> history; 

	public ComponentHistory(C component, List<T> history) {
		this.component = component;
		this.history = history;
	}

	public C component() {
		return component;
	}

	public List<T> history() {
		return history;
	}
}