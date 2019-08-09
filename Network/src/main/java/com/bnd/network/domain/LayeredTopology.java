package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class LayeredTopology extends Topology {

	// layers are assumed to partition the whole network
	private List<Topology> layers = new ArrayList<Topology>();

	@Override
	public List<Topology> getLayers() {
		return layers;
	}

	public void setLayers(List<Topology> layers) {
		this.layers = layers;
	}

	public void addLayer(Topology layer) {
		layers.add(layer);
		layer.addParent(this);
	}

	public void removeLayer(Topology layer) {
		layers.remove(layer);
		layer.removeParent(this);
	}

	@Override
	public List<TopologicalNode> getAllNodes() {
		List<TopologicalNode> nodes = new ArrayList<TopologicalNode>();
		for (final Topology layer : layers) {
			nodes.addAll(layer.getAllNodes());
		}
		return nodes;
	}

	@Override
	public boolean isTemplate() {
		return false;
	}

	@Override
	public boolean supportLayers() {
		return true;
	}

	@Override
	public boolean isSpatial() {
		return false;
	}
}