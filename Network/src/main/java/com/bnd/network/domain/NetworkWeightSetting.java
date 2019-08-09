package com.bnd.network.domain;

import java.util.Date;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;
import com.bnd.core.util.ObjectUtil;

/**
 * @author © Peter Banda
 * @since 2012
 */
public abstract class NetworkWeightSetting<T> extends TechnicalDomainObject implements Comparable<Topology> {

	private String name;
	private Date timeCreated = new Date();
	private User createdBy;

	private Integer index;
	private NetworkWeightSetting<T> parent;

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

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public NetworkWeightSetting<T> getParent() {
		return parent;
	}

	protected void setParent(NetworkWeightSetting<T> parent) {
		this.parent = parent;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public abstract boolean isTemplate();

	public abstract boolean hasLayers();

	@Override
	public int compareTo(Topology anotherTopology) {
		return ObjectUtil.compareObjects(index, anotherTopology.getIndex());
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (name != null) {
    		sb.append(name);
    	} else {
    		sb.append("<no name>");
    	}
    	return sb.toString();
    }
}