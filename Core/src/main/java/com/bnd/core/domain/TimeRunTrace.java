package com.bnd.core.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final public class TimeRunTrace<T, C> extends RunTrace {

	private List<C> components = new ArrayList<C>();
	private List<TimeState<T>> timeStates = new ArrayList<TimeState<T>>();

	public List<C> components() {
		return components;
	}

	public void components(List<C> components) {
		this.components = components;
	}

	protected void addComponent(C component) {
		components.add(component);
	}

	public List<TimeState<T>> timeStates() {
		return timeStates;
	}

	public void timeStates(List<TimeState<T>> timeStates) {
		this.timeStates = timeStates;
	}

	protected void addTimeState(TimeState<T> timeState) {
		timeStates.add(timeState);
	}

	public ComponentRunTrace<T, C> transpose() {
		ComponentRunTrace<T, C> componentRunTrace = new ComponentRunTrace<T, C>();
		componentRunTrace.runTime(runTime);

		List<Iterator<T>> stateIterators = new ArrayList<Iterator<T>>();
		for (final TimeState<T> timeState : timeStates) {
			componentRunTrace.addTimeStep(timeState.time());
			stateIterators.add(timeState.state().iterator());
		}

		for (final C component : components) {
			List<T> history = new ArrayList<T>();
			for (Iterator<T> stateIterator : stateIterators) {
				history.add(stateIterator.next());
			}
			componentRunTrace.addComponentHistory(new ComponentHistory<T, C>(component, history));
		}
		return componentRunTrace;
	}
}