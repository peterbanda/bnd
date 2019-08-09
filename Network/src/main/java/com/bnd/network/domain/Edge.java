package com.bnd.network.domain;

import com.bnd.core.domain.TechnicalDomainObject;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class Edge extends TechnicalDomainObject {

	private TopologicalNode start;
	private TopologicalNode end;

	public Edge() {
		super();
	}

	public Edge(TopologicalNode start, TopologicalNode end) {
		this();
		this.start = start;
		this.end = end;
	}

	public TopologicalNode getStart() {
		return start;
	}

	protected void setStart(TopologicalNode start) {
		this.start = start;
	}

	public TopologicalNode getEnd() {
		return end;
	}

	protected void setEnd(TopologicalNode end) {
		this.end = end;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(start.getIndex());
    	sb.append(" -> ");
    	sb.append(end.getIndex());
    	return sb.toString(); 
    }
}