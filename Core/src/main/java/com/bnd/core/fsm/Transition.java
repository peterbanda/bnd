package com.bnd.core.fsm;

import java.util.Collections;
import java.util.Set;

class Transition<S, T> {

	private final Node<S ,T> fromNode;
	private final Node<S, T> toNode;
	private final Set<T> labels;

	protected Transition(
		Node<S, T> fromNode,
		Node<S, T> toNode,
		T label
	) {
		this(fromNode, toNode, Collections.singleton(label));
	}

	protected Transition(
		Node<S, T> fromNode,
		Node<S, T> toNode,
		Set<T> labels
	) {
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.labels = labels;
	}

	protected Node<S, T> getStartNode() {
		return fromNode;
	}

	protected Node<S, T> getToNode() {
		return toNode;
	}

	protected Set<T> getLabels() {
		return labels;
	}
}