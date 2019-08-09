package com.bnd.network.business;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public final class NodeBOEdge<T> {

	private final NodeBO<T> start;
	private final NodeBO<T> end;
	private T weight;

	NodeBOEdge(NodeBO<T> startNodeBO, NodeBO<T> endNodeBO, T weight) {
		this(startNodeBO, endNodeBO);
		this.weight = weight;
	}

	NodeBOEdge(NodeBO<T> startNodeBO, NodeBO<T> endNodeBO) {
		this.start = startNodeBO;
		this.end = endNodeBO;
	}

	public NodeBO<T> getStart() {
		return start;
	}

	public NodeBO<T> getEnd() {
		return end;
	}

	public T getStartNodeState() {
		return start.getState();
	}

	public T getEndNodeState() {
		return end.getState();
	}

	public T getWeight() {
		return weight;
	}

	public void setWeight(T weight) {
		this.weight = weight;
	}
}