package com.bnd.network.business;

import java.util.Comparator;

import com.bnd.network.domain.TopologicalNode;
import com.bnd.network.domain.TopologicalNodeLocationComparator;

public class NodeBOLocationComparator implements Comparator<NodeBO<?>> {

	private final Comparator<TopologicalNode> locationComparator = new TopologicalNodeLocationComparator();
	
	@Override
	public int compare(NodeBO<?> nodeBO1, NodeBO<?> nodeBO2) {
		return locationComparator.compare(nodeBO1.getTopologicalNode(), nodeBO2.getTopologicalNode());
	}
}