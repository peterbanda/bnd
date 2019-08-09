package com.bnd.core;

import java.util.Comparator;

public class ReverseComparator<T> implements Comparator<T> {

	private Comparator<T> originalComparator;

	public ReverseComparator(Comparator<T> originalComparator) {
		this.originalComparator = originalComparator;
	}
	
	@Override
	public int compare(T object1, T object2) {
		return -originalComparator.compare(object1, object2);
	}
}
