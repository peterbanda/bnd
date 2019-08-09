package com.bnd.core.domain;

import java.util.*;

final public class ComponentRunTrace<T, C> extends RunTrace {

	private final class ComponentHistoryComparator implements Comparator<ComponentHistory<T, C>> {

		private final Comparator<C> componentComparator;
		
		private ComponentHistoryComparator(Comparator<C> componentComparator) {
			this.componentComparator = componentComparator;
		}

		@Override
		public int compare(ComponentHistory<T, C> o1, ComponentHistory<T, C> o2) {
			return componentComparator.compare(o1.component(), o2.component());
		}
	}

	private List<Double> timeSteps = new ArrayList<Double>();
	private List<ComponentHistory<T, C>> componentHistories = new ArrayList<ComponentHistory<T, C>>();
    
	public List<Double> timeSteps() {
		return timeSteps;
	}

	public void timeSteps(List<Double> timeSteps) {
		this.timeSteps = timeSteps;
	}

	protected void addTimeStep(Double timeStep) {
		timeSteps.add(timeStep);
	}

	public List<ComponentHistory<T, C>> componentHistories() {
		return componentHistories;
	}

	public void componentHistories(List<ComponentHistory<T, C>> componentHistories) {
		this.componentHistories = componentHistories;
	}

	protected void addComponentHistory(ComponentHistory<T, C> componentHistory) {
		componentHistories.add(componentHistory);
	}

	public void sortComponentHistoriesBy(Comparator<C> componentComparator) {
		Collections.sort(componentHistories, new ComponentHistoryComparator(componentComparator));
	}

	public Map<C, List<T>> componentHistoryMap() {
		Map<C, List<T>> map = new HashMap<C, List<T>>();
		for (ComponentHistory<T, C> componentHistory : componentHistories) {
			map.put(componentHistory.component(), componentHistory.history());
		}
		return map;
	}

	public TimeRunTrace<T, C> transpose() {
		TimeRunTrace<T, C> timeRunTrace = new TimeRunTrace<T, C>();
		timeRunTrace.runTime(runTime);

		List<Iterator<T>> componentHistoryIterators = new ArrayList<Iterator<T>>();
		for (final ComponentHistory<T, C> componentHistory : componentHistories) {
			timeRunTrace.addComponent(componentHistory.component());
			componentHistoryIterators.add(componentHistory.history().iterator());
		}

		for (final Double time : timeSteps) {
			List<T> state = new ArrayList<T>();
			for (Iterator<T> componentHistoryIterator : componentHistoryIterators) {
				state.add(componentHistoryIterator.next());
			}
			timeRunTrace.addTimeState(new TimeState<T>(time, state));
		}
		return timeRunTrace;
	}
}