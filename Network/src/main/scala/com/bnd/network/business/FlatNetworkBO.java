package com.bnd.network.business;

import java.util.List;

import com.bnd.core.domain.MultiStateUpdateType;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class FlatNetworkBO<T> extends NetworkBO<T> {

	public FlatNetworkBO(
		List<NodeBO<T>> nodes,
		MultiStateUpdateType multiNodeUpdaterType
	) {
		super(nodes, MultiStateUpdater.createInstance(multiNodeUpdaterType, nodes));
	}

	public boolean hasNodes() {
		return getNodes() != null && !getNodes().isEmpty();
	}

	public int getNodesNum() {
		return hasNodes() ? getNodes().size() : 0;
	}
	
	/**
	 * The same as <code>setMutableNodeStates(List)</code>
	 */
	@Deprecated
	public void setInput(List<T> inputs) {
		setMutableNodeStates(inputs);
	}

	/**
	 * The same as <code>getMutableNodeStates()</code>
	 */
	@Deprecated
	public List<T> getOutput() {
		return getMutableNodeStates();
	}
}