package com.bnd.network.domain;

import com.bnd.math.domain.StatsSequence;

public class SpatialNetworkPerformance<T> extends AbstractNetworkPerformance<T> {

	private Integer sizeFrom;
	private Integer sizeTo;
	private StatsSequence results;

	public Integer getSizeFrom() {
		return sizeFrom;
	}

	public void setSizeFrom(Integer sizeFrom) {
		this.sizeFrom = sizeFrom;
	}

	public Integer getSizeTo() {
		return sizeTo;
	}

	public void setSizeTo(Integer sizeTo) {
		this.sizeTo = sizeTo;
	}

	public StatsSequence getResults() {
		return results;
	}

	public void setResults(StatsSequence results) {
		this.results = results;
	}	
}