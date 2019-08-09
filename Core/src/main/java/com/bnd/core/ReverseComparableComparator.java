package com.bnd.core;

import java.util.Comparator;

import com.bnd.core.util.ObjectUtil;

public class ReverseComparableComparator<T extends Comparable<T>> implements Comparator<T> {

	public ReverseComparableComparator() {
		// nothing to do
	}

	@Override
	public int compare(T object1, T object2) {
		return -ObjectUtil.compareObjects(object1, object2);
	}
}
