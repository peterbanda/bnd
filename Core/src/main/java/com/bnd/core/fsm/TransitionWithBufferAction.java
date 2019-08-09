package com.bnd.core.fsm;

import java.util.Set;

import com.bnd.core.fsm.action.BufferTransitionAction;

class TransitionWithBufferAction<S, T> extends Transition<S, T> {

	private final BufferTransitionAction<T> action;

	protected TransitionWithBufferAction(
		Node<S, T> fromNode,
		Node<S, T> toNode,
		Set<T> labels,
		BufferTransitionAction<T> action
	) {
		super(fromNode, toNode, labels);
		this.action = action;
	}

	public BufferTransitionAction<T> getAction() {
		return action;
	}
}