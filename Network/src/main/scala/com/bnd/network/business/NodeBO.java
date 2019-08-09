package com.bnd.network.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.bnd.core.util.ObjectUtil;
import com.bnd.network.BndNetworkException;
import com.bnd.network.domain.TopologicalNode;

/**
 * Node business object
 * 
 * @author © Peter Banda
 * @since 2012  
 */
public abstract class NodeBO<T> implements StateUpdatable, InWeightAccessible<T> {

	// corresponding topological node
	private final TopologicalNode topologicalNode;

	// is state of the node mutable
	private final boolean immutable;

	// input nodes (neighbors)
	private List<NodeBOEdge<T>> inEdges = new ArrayList<NodeBOEdge<T>>();

	// out nodes (neighbors)
	private List<NodeBOEdge<T>> outEdges = new ArrayList<NodeBOEdge<T>>();

	// current state	
	private T state;
	private T previousState;
	private T newStateCached;

	public NodeBO(TopologicalNode topologicalNode) {
		this(topologicalNode, false);
	}

	public NodeBO(
		TopologicalNode topologicalNode,
		boolean immutable
	) {
		this.topologicalNode = topologicalNode;
		this.immutable = immutable;
	}
	
	// In Edges

	public List<NodeBOEdge<T>> getInEdges() {
		return inEdges;
	}

	public int getInEdgesNum() {
		return hasInEdges() ? inEdges.size() : 0;
	}

	public boolean hasInEdges() {
		return inEdges != null && !inEdges.isEmpty();
	}

	public void addInEdge(NodeBOEdge<T> inEdge) {
		if (inEdges.contains(inEdge)) {
			return;
		}
		inEdges.add(inEdge);
		inEdge.getStart().addOutEdge(inEdge);
	}

	public void removeInEdge(NodeBOEdge<T> inEdge) {
		if (inEdges.contains(inEdge)) {
			return;
		}
		inEdges.remove(inEdge);
		inEdge.getStart().removeOutEdge(inEdge);
	}

	public void addNewInEdgeFrom(NodeBO<T> inNodeBO) {
		addInEdge(new NodeBOEdge<T>(inNodeBO, this));
	}

	// in Edge weights

	public List<T> getInNodesWeights() {
    	List<T> neighborWeights = new ArrayList<T>();
    	for (final NodeBOEdge<T> inEdge : getInEdges()) {
    		neighborWeights.add(inEdge.getWeight());
    	}
    	return neighborWeights;
    }

	public void setInNodesWeights(Collection<T> weights) {
		if (weights.size() != inEdges.size()) {
			throw new BndNetworkException("Insufficient weights provides: '" + weights.size() + "' vs. '" + inEdges.size() + "'.");
		}
		setInNodesWeights(weights.iterator());
    }

	public void setInNodesWeights(Iterator<T> weightIterator) {
    	for (final NodeBOEdge<T> inEdge : getInEdges()) {
    		if (!weightIterator.hasNext()) {
    			throw new BndNetworkException("Insufficient weights provides.");    			
    		}
    		inEdge.setWeight(weightIterator.next());
    	}
    }

	// Out edges

	public List<NodeBOEdge<T>> getOutEdges() {
		return outEdges;
	}

	public void addOutEdge(NodeBOEdge<T> outEdge) {
		if (outEdges.contains(outEdge)) {
			return;
		}
		outEdges.add(outEdge);
		outEdge.getEnd().addInEdge(outEdge);
	}

	public void removeOutEdge(NodeBOEdge<T> outEdge) {
		if (outEdges.contains(outEdge)) {
			return;
		}
		outEdges.remove(outEdge);
		outEdge.getEnd().removeInEdge(outEdge);
	}

	public void addNewOutEdgeTo(NodeBO<T> nodeBO) {
		addOutEdge(new NodeBOEdge<T>(this, nodeBO));
	}

	// state functions

	protected abstract T calcNewState();

	public void setState(T state) {
		if (immutable && this.state != null) {
			return;
		}
		previousState = this.state;
		this.state = state;
	}

	public List<T> getInNodeStates() {
    	List<T> neighborStates = new ArrayList<T>();
    	for (final NodeBOEdge<T> inEdge : getInEdges()) {
    		neighborStates.add(inEdge.getStartNodeState());
    	}
    	return neighborStates;
    }
	
	@Override
	public void updateState() {
		if (immutable) {
			return;
		}
		final T newState = calcNewState();
		if (newState != null) {
			setState(newState);
		}
	}

	@Override
	public void updateStateInCache() {
		if (immutable) {
			return;
		}
		final T newState = calcNewState();
		if (newState != null) {
			this.newStateCached = newState;
		}
	}

	@Override
	public void updateStateFromCache() {
		if (immutable) {
			return;
		}
		if (newStateCached != null) {
			state = newStateCached;
		}
	}

	@Override
	public void rollbackToPreviousState() {
		if (immutable) {
			return;
		}
		state = previousState;
	}

	public boolean hasNodeStateChanged() {
		if (state != null && immutable) {
			return false;
		}
		return !ObjectUtil.areObjectsEqual(state, previousState);				
	}

	// Basic Info getters

	public boolean isImmutable() {
		return immutable;
	}

	public T getState() {
		return state;
	}

	protected T getPreviousState() {
		return previousState;
	}

	protected T getNewStateCached() {
		return newStateCached;
	}

    public TopologicalNode getTopologicalNode() {
		return topologicalNode;
	}

    public Long getId() {
		return topologicalNode.getId();
	}

    public Integer getIndex() {
		return topologicalNode.getIndex();
    }

    public List<Integer> getLocation() {
    	return topologicalNode.getLocation();
    }

    public boolean hasLocation() {
    	return getLocation() != null;
    }

	@Override
	public void setWeights(Iterator<T> weightIterator) {
		for (final NodeBOEdge<T> edgeBO : getInEdges()) {
	    	if (!weightIterator.hasNext())
	    		throw new BndNetworkException("Insufficient weights provides.");    			
			edgeBO.setWeight(weightIterator.next());
		}
	}

	@Override
	public void setMutableWeights(Iterator<T> weightIterator) {
		for (final NodeBOEdge<T> edgeBO : getInEdges())
			if (!edgeBO.getStart().isImmutable()) {
	    		if (!weightIterator.hasNext())
	    			throw new BndNetworkException("Insufficient weights provides.");    			
				edgeBO.setWeight(weightIterator.next());
			}
	}

	@Override
	public void setImmutableWeights(Iterator<T> weightIterator) {
		for (final NodeBOEdge<T> edgeBO : getInEdges())
			if (edgeBO.getStart().isImmutable()) {
	    		if (!weightIterator.hasNext())
	    			throw new BndNetworkException("Insufficient weights provides.");    			
				edgeBO.setWeight(weightIterator.next());
			}
	}

	@Override
	public int getWeightsNum() {
		return getInEdgesNum();
	}
}