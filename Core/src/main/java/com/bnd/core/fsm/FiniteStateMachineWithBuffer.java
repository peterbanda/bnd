package com.bnd.core.fsm;

import java.util.*;

import com.bnd.core.Pair;
import com.bnd.core.fsm.action.BufferStateAction;
import com.bnd.core.fsm.action.BufferTransitionAction;
import org.apache.commons.lang.StringUtils;

public class FiniteStateMachineWithBuffer<S, T> extends FiniteStateMachine<S, T> {

	public FiniteStateMachineWithBuffer(boolean ignoreIllegalTransition) {
		super(ignoreIllegalTransition);
	}

	public void addTransition(S fromState, S toState, T transitionLabel, BufferTransitionAction<T> action) {
		addTransition(fromState, toState, Collections.singleton(transitionLabel), action);
	}

	public void addTransition(S fromState, S toState, Set<T> transitionLabels, BufferTransitionAction<T> action) {
		final Node<S, T> fromNode = getNode(fromState);
		final Node<S, T> toNode = getNode(toState);
		final Transition<S, T> newTransition = new TransitionWithBufferAction<S, T>(fromNode, toNode, transitionLabels, action);
		fromNode.addTransition(newTransition);
	}

	@Override
	public Pair<Boolean, S> process(Collection<T> tape) {
		final List<T> buffer = new ArrayList<T>();
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
				runTransitionActionIfSupported(transition, transitionLabel, buffer);
				currentNode = transition.getToNode();
				runNodeActionIfSupported(currentNode, buffer);
			}
			tapePosition++;
		}
		return new Pair<>(isAcceptState(currentNode), currentNode.getState());
	}

	protected void runTransitionActionIfSupported(Transition<S, T> transition, T transitionLabel, List<T> buffer) {
		super.runTransitionActionIfSupported(transition, transitionLabel);
		if (transition instanceof TransitionWithBufferAction) {
			final BufferTransitionAction<T> action = ((TransitionWithBufferAction<S, T>) transition).getAction();
			if (action != null) {
				action.run(buffer, transitionLabel);
			}			
		}
	}

	protected void runNodeActionIfSupported(Node<S, T> node, List<T> buffer) {
		super.runNodeActionIfSupported(node);
		if (node instanceof NodeWithBufferAction) {
			final BufferStateAction<T> action = ((NodeWithBufferAction<S, T>) node).getEnterStateAction();
			if (action != null) {
				action.run(buffer);
			}			
		}
	}
}