package com.bnd.network.domain;

import com.bnd.math.domain.rand.RandomDistribution;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class TemplateNetworkWeightSetting<T> extends NetworkWeightSetting<T> {

	private RandomDistribution<T> randomDistribution;

	public RandomDistribution<T> getRandomDistribution() {
		return randomDistribution;
	}

	public void setRandomDistribution(RandomDistribution<T> randomDistribution) {
		this.randomDistribution = randomDistribution;
	}

	@Override
	public boolean isTemplate() {
		return true;
	}

	@Override
	public boolean hasLayers() {
		return false;
	}
}