package com.bnd.core.fsm;

import java.util.HashSet;
import java.util.Set;

class Node<S, T> {

	private final S state;
	private Set<Transition<S, T>> transitions = new HashSet<Transition<S, T>>();

	protected Node(S state) {
		this.state = state;
	}

	protected void addTransition(Transition<S, T> transition) {
		transitions.add(transition);
	}

	protected Transition<S, T> getTransition(T transitionLabel) {
		for (Transition<S, T> transition : transitions) {
			if (transition.getLabels().contains(transitionLabel)) {
				return transition;
			}
		}
		return null;
	}

	protected S getState() {
		return state;
	}
}