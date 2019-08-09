package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.List;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;

/**
 * @author © Peter Banda
 * @since 2013
 */
public class SpatialNeighbor extends TechnicalDomainObject implements Comparable<SpatialNeighbor> {

	private Integer index;
	private List<Integer> coordinateDiffs = new ArrayList<Integer>();
	private SpatialNeighborhood parent;

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public List<Integer> getCoordinateDiffs() {
		return coordinateDiffs;
	}

	public void setCoordinateDiffs(List<Integer> coordinateDiffs) {
		this.coordinateDiffs = coordinateDiffs;
	}

	public void addCoordinateDiff(Integer coordinateDiff) {
		coordinateDiffs.add(coordinateDiff);
	}

	public SpatialNeighborhood getParent() {
		return parent;
	}

	protected void setParent(SpatialNeighborhood parent) {
		this.parent = parent;
	}

	@Override
	public int compareTo(SpatialNeighbor spatialNeighbor) {
		return ObjectUtil.compareObjects(index, spatialNeighbor.index);
	}
}