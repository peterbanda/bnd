package com.bnd.network.domain;

import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.runnable.BaseManipulationSeries;

public class NetworkActionSeries<T> extends BaseManipulationSeries {

	private Set<NetworkAction<T>> actions = new HashSet<NetworkAction<T>>();

	public Set<NetworkAction<T>> getActions() {
		return actions;
	}

	public void setActions(Set<NetworkAction<T>> actions) {
		this.actions = actions;
	}

	public void addAction(NetworkAction<T> action) {
		actions.add(action);
		action.setActionSeries(this);
	}
}