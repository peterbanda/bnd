package com.bnd.core.fsm;

import java.util.Set;

import com.bnd.core.fsm.action.TransitionAction;

class TransitionWithAction<S, T> extends Transition<S, T> {

	private final TransitionAction<T> action;

	protected TransitionWithAction(
		Node<S, T> fromNode,
		Node<S, T> toNode,
		Set<T> labels,
		TransitionAction<T> action
	) {
		super(fromNode, toNode, labels);
		this.action = action;
	}

	public TransitionAction<T> getAction() {
		return action;
	}
}