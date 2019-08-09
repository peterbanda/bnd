package com.bnd.core;

import java.util.Iterator;

public final class ListKeyLeaf<K,V> implements ListKeyNode<K, V> {

	private V value;

	@Override
	public V get(final Iterator<K> keyIterator) {
		if (!keyIterator.hasNext()) {
			return value;
		}
		return null;
	}

	@Override
	public void put(final Iterator<K> keyIterator, final V value) {
		if (!keyIterator.hasNext()) {
			this.value = value;
		}
	}
}