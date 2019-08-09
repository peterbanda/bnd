package com.bnd.network.domain;

import java.util.List;

import com.bnd.core.domain.runnable.StateAction;
import com.bnd.math.domain.rand.RandomDistribution;

public class NetworkAction<T> extends StateAction {

	private List<T> states;
	private RandomDistribution<T> stateDistribution;

	private NetworkActionSeries<T> actionSeries;

	public NetworkActionSeries<T> getActionSeries() {
		return actionSeries;
	}

	protected void setActionSeries(NetworkActionSeries<T> actionSeries) {
		this.actionSeries = actionSeries;
	}

	public List<T> getStates() {
		return states;
	}

	public void setStates(List<T> states) {
		this.states = states;
	}

	public RandomDistribution<T> getStateDistribution() {
		return stateDistribution;
	}

	public void setStateDistribution(RandomDistribution<T> stateDistribution) {
		this.stateDistribution = stateDistribution;
	}
}