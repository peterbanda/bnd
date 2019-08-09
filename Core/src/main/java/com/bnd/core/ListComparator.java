package com.bnd.core;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.bnd.core.util.ObjectUtil;

public class ListComparator<T extends Comparable<T>> implements Comparator<List<T>> {

	@Override
	public int compare(List<T> list1, List<T> list2) {
		Iterator<T> list2ElementsIterator = list2.iterator();
		int result = 0;
		for (T list1Element : list1) {
			result = ObjectUtil.compareObjects(list1Element, list2ElementsIterator.next());
			if (result != 0) {
				break;
			}
		}
		return result;
	}
}