package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class FlatTopology extends Topology {

	private List<TopologicalNode> nodes = new ArrayList<TopologicalNode>();

	@Override
	public List<TopologicalNode> getAllNodes() {
		return nodes;
	}

	public void setNodes(List<TopologicalNode> nodes) {
		this.nodes = nodes;
	}

	public boolean hasNodes() {
		return nodes != null && !nodes.isEmpty();
	}

	public void addNode(TopologicalNode node) {
		nodes.add(node);
		node.setTopology(this);
	}

	public void removeNode(TopologicalNode node) {
		nodes.remove(node);
		node.setTopology(null);
	}

	@Override
	public Collection<Topology> getLayers() {
		// no layers available
		return null;
	}

	@Override
	public boolean isTemplate() {
		return false;
	}

	@Override
	public boolean supportLayers() {
		return false;
	}

	@Override
	public boolean isSpatial() {
		return false;
	}
}