package com.bnd.network.domain;

import com.bnd.math.domain.Stats;

public class NetworkPerformance<T> extends AbstractNetworkPerformance<T> {

	private Stats result;

	public Stats getResult() {
		return result;
	}

	public void setResult(Stats result) {
		this.result = result;
	}
}