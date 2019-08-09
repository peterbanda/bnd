package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class LayeredNetworkWeightSetting<T> extends NetworkWeightSetting<T> {

	private List<NetworkWeightSetting<T>> layers = new ArrayList<NetworkWeightSetting<T>>();

	public List<NetworkWeightSetting<T>> getLayers() {
		return layers;
	}

	public void setLayers(List<NetworkWeightSetting<T>> layers) {
		this.layers = layers;
	}

	public void addLayer(NetworkWeightSetting<T> layer){
		layers.add(layer);
		layer.setParent(this);
	}

	public void removeLayer(NetworkWeightSetting<T> layer) {
		layers.remove(layer);
		layer.setParent(null);
	}

	@Override
	public boolean hasLayers() {
		return layers != null && !layers.isEmpty();
	}

	@Override
	public boolean isTemplate() {
		return false;
	}
}