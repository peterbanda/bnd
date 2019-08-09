package com.bnd.core;

import java.util.Map.Entry;

import com.bnd.core.util.ObjectUtil;

public class Pair<K, V> implements Entry<K, V> {

	private K first;
	private V second;

	public Pair(K first) {
		this.first = first;
	}

	public Pair(K first, V second) {
		this(first);
		this.second = second;
	}

	@Override
	public K getKey() {
		return first;
	}

	@Override
	public V getValue() {
		return second;
	}

	@Override
	public V setValue(V value) {
		this.second = value;
		return this.second;
	}

	public K getFirst() {
		return first;
	}

	public V getSecond() {
		return second;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object instanceof Pair)) {
			return false;
		}
		Pair pair = (Pair) object;
		return this == pair
				|| (ObjectUtil.areObjectsEqual(first, pair.first) && ObjectUtil.areObjectsEqual(second, pair.second));
	}

	@Override
	public int hashCode() {
		return ObjectUtil.getHashCode(new Object[] {first, second});
	}
}