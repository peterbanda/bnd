package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class TopologicalNode extends TechnicalDomainObject implements Comparable<TopologicalNode> {

	private Integer index;
	private Topology topology;

	private List<Edge> outEdges = new ArrayList<Edge>();
	private List<Edge> inEdges = new ArrayList<Edge>();

	private boolean bias = false;

	// used when metric space is needed
	private List<Integer> location; 

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public void setLocation(List<Integer> location) {
		this.location = location;
	}

	public List<Integer> getLocation() {
		return location;
	}

	public boolean hasLocation() {
		return location != null; 
	}

	// In edges

	public List<Edge> getInEdges() {
		return inEdges;
	}

	protected void setInEdges(List<Edge> inEdges) {
		this.inEdges = inEdges;
	}

	public void addInEdge(Edge inEdge) {
		if (inEdges.contains(inEdge)) {
			return;
		}
		inEdges.add(inEdge);
		inEdge.getStart().addOutEdge(inEdge);
	}

	public void removeInEdge(Edge inEdge) {
		if (inEdges.contains(inEdge)) {
			return;
		}
		inEdges.remove(inEdge);
		inEdge.getStart().removeOutEdge(inEdge);
	}

	public void addNewInEdgesFrom(Collection<TopologicalNode> nodes) {
		for (TopologicalNode node : nodes) {
			addNewInEdgeFrom(node);
		}
	}

	public void addNewInEdgeFrom(TopologicalNode node) {
		addInEdge(new Edge(node, this));
	}

	// Out edges

	public List<Edge> getOutEdges() {
		return outEdges;
	}

	protected void setOutEdges(List<Edge> outEdges) {
		this.outEdges = outEdges;
	}

	public Integer getOutEdgesNum() {
		return outEdges != null ? outEdges.size() : null;
	}

	public void addOutEdge(Edge outEdge) {
		if (outEdges.contains(outEdge)) {
			return;
		}
		outEdges.add(outEdge);
		outEdge.getEnd().addInEdge(outEdge);
	}

	public void removeOutEdge(Edge outEdge) {
		if (outEdges.contains(outEdge)) {
			return;
		}
		outEdges.remove(outEdge);
		outEdge.getEnd().removeInEdge(outEdge);
	}

	public void addNewOutEdgesTo(Collection<TopologicalNode> nodes) {
		for (TopologicalNode node : nodes) {
			addNewOutEdgeTo(node);
		}
	}

	public void addNewOutEdgeTo(TopologicalNode node) {
		addOutEdge(new Edge(this, node));
	}

	public List<TopologicalNode> getInNeighbors() {
		List<TopologicalNode> inNeighbors = new ArrayList<TopologicalNode>();
		for (Edge edge : inEdges) {
			inNeighbors.add(edge.getStart());
		}
		return inNeighbors;
	}

	public List<TopologicalNode> getOutNeighbors() {
		List<TopologicalNode> outNeighbors = new ArrayList<TopologicalNode>();
		for (Edge edge : outEdges) {
			outNeighbors.add(edge.getEnd());
		}
		return outNeighbors;
	}

	public List<TopologicalNode> getNeighbors() {
		List<TopologicalNode> neighbors = new ArrayList<TopologicalNode>();
		neighbors.addAll(getInNeighbors());
		neighbors.addAll(getOutNeighbors());
		return neighbors;
	}

	public Topology getTopology() {
		return topology;
	}

	protected void setTopology(Topology topology) {
		this.topology = topology;
	}

	public boolean isBias() {
		return bias;
	}

	public void setBias(boolean bias) {
		this.bias = bias;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(index);
    	sb.append(hasLocation() ? " - " + getLocation() : "");
    	sb.append(" / [IN:");
    	for (TopologicalNode inNeighbor : getInNeighbors()) {
    		sb.append(inNeighbor.getIndex());
    		sb.append(',');
    	}
    	sb.replace(sb.length() - 1, sb.length(), "");
    	sb.append("][OUT:");
    	for (TopologicalNode outNeighbor : getOutNeighbors()) {
    		sb.append(outNeighbor.getIndex());
    		sb.append(',');
    	}
    	sb.replace(sb.length() - 1, sb.length(), "]");
    	return sb.toString(); 
    }

	@Override
	public int compareTo(TopologicalNode anotherTopologicalNode) {
		final Topology anotherTopology = anotherTopologicalNode.getTopology();
		if (topology == null && anotherTopology == null) {
			return ObjectUtil.compareObjects(getIndex(), anotherTopologicalNode.getIndex());
		}
		final int topologyComparisonResult = ObjectUtil.compareObjects(topology, anotherTopology);
		if (topologyComparisonResult != 0) {
			return topologyComparisonResult;
		}
		return ObjectUtil.compareObjects(getIndex(), anotherTopologicalNode.getIndex());
	}
}