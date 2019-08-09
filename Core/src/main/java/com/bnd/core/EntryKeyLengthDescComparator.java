package com.bnd.core;

import java.util.Comparator;
import java.util.Map.Entry;

import com.bnd.core.util.ObjectUtil;

public class EntryKeyLengthDescComparator<T> implements Comparator<Entry<String, T>> {

	@Override
	public int compare(Entry<String, T> entry1, Entry<String, T> entry2) {
		return ObjectUtil.compareObjects(entry2.getKey().length(), entry1.getKey().length());
	}
}