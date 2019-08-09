package com.bnd.network.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bnd.core.domain.MultiStateUpdateType;
import com.bnd.network.BndNetworkException;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class LayeredNetworkBO<T> extends NetworkBO<T>{

	private final NetworkBO<T>[] layers; 	

	public LayeredNetworkBO(
		MultiStateUpdateType multiLayerUpdaterType,
		NetworkBO<T>[] layers
	) {
		super(getNodesFromLayers(layers), MultiStateUpdater.createInstance(multiLayerUpdaterType, Arrays.asList(layers)));
		this.layers = layers;
		if (!hasLayers()) {
			throw new BndNetworkException("At least one layer expected by LayeredNetworkBO.");
		}
	}

	public NetworkBO<T>[] getLayers() {
		return layers;
	}

	public int getLayersNum() {
		return layers.length;
	}

	public boolean hasLayers() {
		return layers != null && layers.length > 0;
	}

	public NetworkBO<T> getInputLayer() {
		return hasLayers() ? layers[0] : null;
	}

	public NetworkBO<T> getOutputLayer() {
		return hasLayers() ? layers[layers.length - 1] : null;
	}

	public List<T> getOutputNodeStates() {
		final NetworkBO<T> lastLayer = getOutputLayer();
		return lastLayer != null ? lastLayer.getMutableNodeStates() : null;
	}

	public void setInputNodeStates(List<T> inputNodeStates) {
		final NetworkBO<T> firstLayer = getInputLayer();
		if (firstLayer != null) {
			firstLayer.setMutableNodeStates(inputNodeStates);
		}
	}

	/**
	 * The same as <code>setInputNodeStates(List)</code>
	 */
	@Deprecated
	public void setInput(List<T> inputs) {
		setInputNodeStates(inputs);
	}

	/**
	 * The same as <code>getNodesStates()</code>
	 */
	@Deprecated
	public List<T> getOutput() {
		return getOutputNodeStates();
	}

	private static <T> List<NodeBO<T>> getNodesFromLayers(NetworkBO<T>[] layers) {
		List<NodeBO<T>> nodes = new ArrayList<NodeBO<T>>();
		for (final NetworkBO<T> layer : layers) {
			nodes.addAll(layer.getNodes());
		}
		return nodes;
	}
}