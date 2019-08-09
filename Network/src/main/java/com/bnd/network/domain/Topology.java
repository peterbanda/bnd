package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;
import com.bnd.core.util.ObjectUtil;

/**
 * @author © Peter Banda
 * @since 2012
 */
// TODO: should be abstract
public class Topology extends TechnicalDomainObject implements Comparable<Topology> {

	private String name;
	private Date timeCreated = new Date();
	private User createdBy;

	private Integer index;
	private List<Topology> parents = new ArrayList<Topology>();
	private Collection<Network<?>> networks = new ArrayList<Network<?>>();

    public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

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

	public Collection<Network<?>> getNetworks() {
		return networks;
	}

	public void setNetworks(Collection<Network<?>> networks) {
		this.networks = networks;
	}

	public void addNetwork(Network<?> network) {
		networks.add(network);
		network.setTopology(this);
	}

	public void removeNetwork(Network<?> network) {
		networks.remove(network);
		network.setTopology(null);
	}

	public List<Topology> getParents() {
		return parents;
	}

	public void setParents(List<Topology> parents) {
		this.parents = parents;
	}

	protected void addParent(Topology parent) {
		parents.add(parent);
	}

	protected void removeParent(Topology parent) {
		parents.remove(parent);
	}

	public boolean supportLayers() {
		return false;
	}

	public boolean hasLayers() {
		return getLayers() != null && !getLayers().isEmpty();
	}

	public Collection<Topology> getLayers() {
		return null;
	}

	public boolean hasNodes() {
		return getAllNodes() != null && !getAllNodes().isEmpty();
	}

	public List<TopologicalNode> getAllNodes() {
		return new ArrayList<TopologicalNode>();
	}

	public List<TopologicalNode> getNonBiasNodes() {
		List<TopologicalNode> nonBiasNodes = new ArrayList<TopologicalNode>();
		for (TopologicalNode node : getAllNodes())
			if (!node.isBias())
				nonBiasNodes.add(node);

		return nonBiasNodes;
	}

	public List<TopologicalNode> getNodesWithInputs() {
		List<TopologicalNode> nonInputNodes = new ArrayList<TopologicalNode>();
		for (TopologicalNode node : getAllNodes())
			if (!node.getInEdges().isEmpty())
				nonInputNodes.add(node);

		return nonInputNodes;
	}

	public List<TopologicalNode> getBiasNodes() {
		List<TopologicalNode> biasNodes = new ArrayList<TopologicalNode>();
		for (TopologicalNode node : getAllNodes()) {
			if (node.isBias()) {
				biasNodes.add(node);
			}
		}
		return biasNodes;
	}

	public boolean isTemplate() {
		return false;
	}

	public boolean isSpatial() {
		return false;
	}

	@Override
	public int compareTo(Topology anotherTopology) {
		return ObjectUtil.compareObjects(index, anotherTopology.getIndex());
	}

	public void copyFrom(Topology anotherTopology) {
		setId(anotherTopology.getId());
		setVersion(anotherTopology.getVersion());
		setName(anotherTopology.getName());
		setTimeCreated(anotherTopology.getTimeCreated());
		setIndex(anotherTopology.getIndex());
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