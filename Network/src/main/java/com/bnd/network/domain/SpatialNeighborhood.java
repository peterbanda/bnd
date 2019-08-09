package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

/**
 * @author © Peter Banda
 * @since 2013
 */
public class SpatialNeighborhood extends TechnicalDomainObject {

	private String name;	
	private Date timeCreated = new Date();
	private User createdBy;

	private List<SpatialNeighbor> neighbors = new ArrayList<SpatialNeighbor>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public List<SpatialNeighbor> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<SpatialNeighbor> neighbors) {
		this.neighbors = neighbors;
	}

	public void addNeighbor(SpatialNeighbor neighbor) {
		neighbors.add(neighbor);
		neighbor.setParent(this);
	}

	public void removeNeighbor(SpatialNeighbor neighbor) {
		neighbors.remove(neighbor);
		neighbor.setParent(null);
	}
}