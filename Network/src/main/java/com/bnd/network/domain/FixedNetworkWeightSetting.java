package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class FixedNetworkWeightSetting<T> extends NetworkWeightSetting<T> {

	private FixedNetworkWeightSettingOrder settingOrder = FixedNetworkWeightSettingOrder.SimpleOrder;
	private List<T> weights = new ArrayList<T>();

	public List<T> getWeights() {
		return weights;
	}

	public void setWeights(List<T> weights) {
		this.weights = weights;
	}

	public void addWeight(T weight) {
		weights.add(weight);
	}

	@Override
	public boolean isTemplate() {
		return false;
	}

	@Override
	public boolean hasLayers() {
		return false;
	}

	public FixedNetworkWeightSettingOrder getSettingOrder() {
		return settingOrder;
	}

	public void setSettingOrder(FixedNetworkWeightSettingOrder settingOrder) {
		this.settingOrder = settingOrder;
	}
}