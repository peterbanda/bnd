package com.bnd.network.domain;

import java.util.Comparator;
import java.util.List;

import com.bnd.core.ListComparator;
import com.bnd.network.BndNetworkException;

public class TopologicalNodeLocationComparator implements Comparator<TopologicalNode> {

	final Comparator<List<Integer>> listComparator = new ListComparator<Integer>();

	@Override
	public int compare(TopologicalNode topologicalNode1, TopologicalNode topologicalNode2) {
		if (!topologicalNode1.hasLocation() || !topologicalNode2.hasLocation()) {
			throw new BndNetworkException("Location expected for topological nodes " + topologicalNode1.getId() + ", " + topologicalNode2.getId() + ".");
		}
		return listComparator.compare(topologicalNode1.getLocation(), topologicalNode2.getLocation());
	}
}