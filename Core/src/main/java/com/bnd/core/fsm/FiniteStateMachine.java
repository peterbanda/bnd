package com.bnd.core.fsm;

import java.util.*;

import com.bnd.core.Pair;
import com.bnd.core.fsm.action.StateAction;
import com.bnd.core.fsm.action.TransitionAction;
import org.apache.commons.lang.StringUtils;

import com.bnd.core.BndRuntimeException;

public class FiniteStateMachine<S, T> {

	private Map<S, Node<S, T>> nodeMap = new HashMap<S, Node<S, T>>();
	private Set<S> acceptStates = new HashSet<S>();
	protected Node<S, T> startNode;
	protected final boolean ignoreIllegalTransition;

	public FiniteStateMachine(boolean ignoreIllegalTransition) {
		this.ignoreIllegalTransition = ignoreIllegalTransition;
	}

	public void addTransition(S fromState, S toState, T transitionLabel) {
		addTransition(fromState, toState, Collections.singleton(transitionLabel));
	}

	public void addTransition(S fromState, S toState, T transitionLabel, TransitionAction<T> action) {
		addTransition(fromState, toState, Collections.singleton(transitionLabel), action);
	}

	public void addTransition(S fromState, S toState, Set<T> transitionLabels) {
		final Node<S, T> fromNode = getNode(fromState);
		final Node<S, T> toNode = getNode(toState);
		final Transition<S, T> newTransition = new Transition<S, T>(fromNode, toNode, transitionLabels);
		fromNode.addTransition(newTransition);
	}

	public void addTransition(S fromState, S toState, Set<T> transitionLabels, TransitionAction<T> action) {
		final Node<S, T> fromNode = getNode(fromState);
		final Node<S, T> toNode = getNode(toState);
		final Transition<S, T> newTransition = new TransitionWithAction<S, T>(fromNode, toNode, transitionLabels, action);
		fromNode.addTransition(newTransition);
	}

	public void setStartState(S state) {
		startNode = getNode(state);
	}

	public void addAcceptState(S state) {
		checkState(state);
		acceptStates.add(state);
	}

	public Pair<Boolean, S> process(Collection<T> tape) {
		Node<S, T> currentNode = startNode;
		if (currentNode == null) {
			throw new FiniteStateMachineException("No start state defined for the finite state machine.");
		}
		int tapePosition = 1;
		for (T transitionLabel : tape) {
			final Transition<S, T> transition = currentNode.getTransition(transitionLabel);
			if (transition == null && !ignoreIllegalTransition) {
				throw new FiniteStateMachineException("A transition label '" + transitionLabel + "' at position '" + tapePosition + "' of the input tape '" + StringUtils.join(tape, "") + "' is illegal.");
			} else {
				runTransitionActionIfSupported(transition, transitionLabel);
				currentNode = transition.getToNode();
				runNodeActionIfSupported(currentNode);
			}
			tapePosition++;
		}
		return new Pair<>(isAcceptState(currentNode), currentNode.getState());
	}

	protected void runTransitionActionIfSupported(Transition<S, T> transition, T transitionLabel) {
		if (transition instanceof TransitionWithAction) {
			final TransitionAction<T> action = ((TransitionWithAction<S, T>) transition).getAction();
			if (action != null) {
				action.run(transitionLabel);
			}			
		}
	}

	protected void runNodeActionIfSupported(Node<S, T> node) {
		if (node instanceof NodeWithAction) {
			final StateAction action = ((NodeWithAction<S, T>) node).getEnterStateAction();
			if (action != null) {
				action.run();
			}			
		}
	}

	protected boolean isAcceptState(Node<S , T> node) {
		if (node != null) {
			return acceptStates.contains(node.getState());
		}
		return false;
	}

	protected Node<S, T> getNode(S state) {
		checkState(state);
		return nodeMap.get(state);
	}

	private void checkState(S state) {
		if (!nodeMap.containsKey(state)) {
			addState(state);
		}
	}

	private void addState(S state) {
		if (nodeMap.containsKey(state)) {
			// should never happen
			throw new BndRuntimeException("A given state '" + state + "' already exists in the finite state machine.");
		}
		Node<S, T> newNode = new Node<S, T>(state);
		nodeMap.put(state, newNode);
	}
}