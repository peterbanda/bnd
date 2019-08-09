package com.bnd.core;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.bnd.core.util.ObjectUtil;

public class FixedOrderComparator<T> implements Comparator<T> {

	final private Map<T, Integer> objectOrderMap;

	public FixedOrderComparator(Collection<T> objectsInOrder) {
		objectOrderMap = new HashMap<T, Integer>();
		int order = 1;
		for (T objectInOrder : objectsInOrder) {
			objectOrderMap.put(objectInOrder, order);
			order++;
		}
	}

	@Override
	public int compare(T o1, T o2) {
		return ObjectUtil.compareObjects(objectOrderMap.get(o1), objectOrderMap.get(o2));
	}		
}

