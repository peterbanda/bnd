package com.bnd.core;

import java.util.Comparator;

final public class PairComparator<L,P> implements Comparator<Pair<L,P>> {

	private final Comparator<L> leftComparator;
	private final Comparator<P> rightComparator;

	public PairComparator(Comparator<L> leftComparator, Comparator<P> rightComparator) {
		this.leftComparator = leftComparator;
		this.rightComparator = rightComparator;
	}

	@Override
	public int compare(Pair<L,P> o1, Pair<L,P> o2) {
		int result = leftComparator.compare(o1.getFirst(), o2.getFirst());
		if (result == 0) {
			result = rightComparator.compare(o1.getSecond(), o2.getSecond());
		}
		return result;
	}
}