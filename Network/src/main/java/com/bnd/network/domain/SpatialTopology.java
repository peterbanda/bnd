package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bnd.core.metrics.MetricsType;

/**
 * @author © Peter Banda
 * @since 2013
 */
public class SpatialTopology extends Topology {

	private List<Integer> sizes = new ArrayList<Integer>();
	private boolean torusFlag;

	private MetricsType metricsType;
	private Integer radius;
	private boolean itsOwnNeighor;
	private SpatialNeighborhood neighborhood;

	public List<Integer> getSizes() {
		return sizes;
	}

	public void setSizes(List<Integer> sizes) {
		this.sizes = sizes;
	}

	public void addSize(Integer size) {
		sizes.add(size);
	}

	public int getDimensionsNumber() {
		return sizes != null ? sizes.size() : 0;
	}

	public boolean isTorusFlag() {
		return torusFlag;
	}

	public void setTorusFlag(boolean torusFlag) {
		this.torusFlag = torusFlag;
	}

	public Integer getRadius() {
		return radius;
	}

	public void setRadius(Integer radius) {
		this.radius = radius;
	}

	public MetricsType getMetricsType() {
		return metricsType;
	}

	public void setMetricsType(MetricsType metricsType) {
		this.metricsType = metricsType;
	}

	public SpatialNeighborhood getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(SpatialNeighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

	public boolean isItsOwnNeighor() {
		return itsOwnNeighor;
	}

	public void setItsOwnNeighor(boolean itsOwnNeighor) {
		this.itsOwnNeighor = itsOwnNeighor;
	}

	@Override
	public boolean isTemplate() {
		return false;
	}

	@Override
	public List<TopologicalNode> getAllNodes() {
		// no nodes available
		return null;
	}

	@Override
	public boolean supportLayers() {
		return false;
	}

	@Override
	public Collection<Topology> getLayers() {
		// no layers available
		return null;
	}

	@Override
	public boolean isSpatial() {
		return true;
	}
}