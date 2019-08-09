package com.bnd.network.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scala.math.BigDecimal;

import com.bnd.core.runnable.StateAccessible;
import com.bnd.core.runnable.TimeRunnable;
import com.bnd.network.BndNetworkException;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public abstract class NetworkBO<T> implements StateUpdatable, TimeRunnable, StateAccessible<T> {

	private final BigDecimal ONE = BigDecimal.valueOf(1d);

	private final MultiStateUpdater componentsUpdater;
	private final List<NodeBO<T>> nodes;
	private final List<NodeBO<T>> mutableNodes;
	private final List<NodeBO<T>> immutableNodes;
	private final List<NodeBO<T>> nodesInLocationOrder;

	public NetworkBO(List<NodeBO<T>> nodes, MultiStateUpdater componentsUpdater) {
		this.nodes = nodes;
		this.mutableNodes = filterMutableNodes(nodes);
		this.immutableNodes = filterImmutableNodes(nodes);
		this.nodesInLocationOrder = sortNodesByLocationIfProvided(nodes);
		this.componentsUpdater = componentsUpdater;
	}

	public List<NodeBO<T>> getNodes() {
		return nodes;
	}

	public List<NodeBO<T>> getMutableNodes() {
		return mutableNodes;
	}

	public List<NodeBO<T>> getImmutableNodes() {
		return immutableNodes;
	}

	public List<T> getNodeStates() {
		List<T> nodeStates = new ArrayList<T>();
		for (NodeBO<T> node : nodes) {
			nodeStates.add(node.getState());
		}
		return nodeStates;
	}

	public List<T> getNodeStatesInLocationOrder() {
		if (nodesInLocationOrder == null) {
			return null;
		}
		List<T> nodeStates = new ArrayList<T>();
		for (NodeBO<T> node : nodesInLocationOrder) {
			nodeStates.add(node.getState());
		}
		return nodeStates;
	}

	public List<T> getMutableNodeStates() {
		List<T> mutableNodeStates = new ArrayList<T>();
		for (NodeBO<T> node : mutableNodes) {
			mutableNodeStates.add(node.getState());
		}
		return mutableNodeStates;
	}

	public void setNodeStates(List<T> nodeStates) {
		final Iterator<T> nodeStateIterator = nodeStates.iterator();
		for (NodeBO<T> node : nodes) {
			node.setState(nodeStateIterator.next());
		}
	}

	public void setMutableNodeStates(List<T> nodeStates) {
		final Iterator<T> nodeStateIterator = nodeStates.iterator();
		for (NodeBO<T> node : mutableNodes) {
			node.setState(nodeStateIterator.next());
		}
	}

	protected List<T> getPreviousNodeStates() {
		List<T> prevNodeStates = new ArrayList<T>();
		for (NodeBO<T> node : nodes) {
			prevNodeStates.add(node.getPreviousState());
		}
		return prevNodeStates;
	}

	@Override
	public void setStates(List<T> states) {
		setNodeStates(states);
	}

	public List<NodeBO<T>> getNodesInLocationOrder() {
		return nodesInLocationOrder;
	}

//	@Override
//	public void setStates(List<T> states, List<Integer> indices) {
//		Iterator<T> stateIterator = states.iterator();
//		for (Integer index : indices) {
//			nodes.get(index).setState(stateIterator.next());			
//		}
//	}

	@Override
	public List<T> getStates() {
		return getNodeStates();
	}

	@Override
	public void updateState() {
		componentsUpdater.updateState();	
	}

	@Override
	public void updateStateInCache() {
		componentsUpdater.updateStateInCache();
	}

	@Override
	public void updateStateFromCache() {
		componentsUpdater.updateStateFromCache();
	}

	@Override
	public void rollbackToPreviousState() {
		componentsUpdater.rollbackToPreviousState();
	}

	@Override
	public BigDecimal nextTimeStepSize() {
		return ONE;
	}

	/**
	 * The same as <code>updateState()</code>
	 */
	@Override
	public void runFor(BigDecimal timeDiff) {
		if (timeDiff.$less(ONE)) {
			throw new BndNetworkException("Network BO uses discrete time. Can't run it for " + timeDiff + " time steps.");
		}
		Double diff = timeDiff.toDouble();
		for (int i = 0; i < diff; i++) {
			updateState();
		}
	}
	
	private static <T> List<NodeBO<T>> filterMutableNodes(List<NodeBO<T>> nodes) {
		List<NodeBO<T>> mutableNodes = new ArrayList<NodeBO<T>>();
		for (final NodeBO<T> node : nodes) {
			if (!node.isImmutable()) {
				mutableNodes.add(node);
			}
		}
		return mutableNodes;
	}

	private static <T> List<NodeBO<T>> filterImmutableNodes(List<NodeBO<T>> nodes) {
		List<NodeBO<T>> immutableNodes = new ArrayList<NodeBO<T>>();
		for (final NodeBO<T> node : nodes) {
			if (node.isImmutable()) {
				immutableNodes.add(node);
			}
		}
		return immutableNodes;
	}

	private static <T> List<NodeBO<T>> sortNodesByLocationIfProvided(List<NodeBO<T>> nodes) {
		if (!nodes.get(0).hasLocation()) {
			return null;
		}
		List<NodeBO<T>> nodesInLocationOrder = new ArrayList<NodeBO<T>>(nodes);
		Collections.sort(nodesInLocationOrder, new NodeBOLocationComparator());
		return nodesInLocationOrder;
	}

	@Override
	public BigDecimal currentTime() {
		throw new RuntimeException("NetworkBO.currentTime() needs to be implemented.");
	}

	@Override
	public void runUntil(BigDecimal finalTime) {
		throw new RuntimeException("NetworkBO.runUntil() needs to be implemented.");
	}

	public abstract void setInput(List<T> inputs);

	public abstract List<T> getOutput();
}