package com.bnd.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ListKeyTreeMap<K,V> implements Map<Iterable<K>,V> {

	private final ListKeyNode<K, V> root;

	public ListKeyTreeMap() {
		this(new MapListKeyInnerNode<K, V>());
	}

	public ListKeyTreeMap(ListKeyNode<K, V> root) {
		this.root = root;
	}

	@Override
	public V get(final Object key) {
		final Iterator<K> listIterator = ((Iterable<K>) key).iterator();
		return root.get(listIterator);
	}

	@Override
	public boolean containsKey(final Object key) {
		return get(key) != null;
	}

	@Override
	public V put(Iterable<K> key, V value) {
		root.put(key.iterator(), value);
		return value;
	}

	@Override
	public void putAll(Map<? extends Iterable<K>, ? extends V> m) {
		for (Entry<? extends Iterable<K>, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Supports only get, containsKey, and put operations.");
	}

	@Override
	public Set<Entry<Iterable<K>, V>> entrySet() {
		throw new UnsupportedOperationException("Supports only get, containsKey, and put operations.");
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException("Supports only get, containsKey, and put operations.");
	}

	@Override
	public Set<Iterable<K>> keySet() {
		throw new UnsupportedOperationException("Supports only get, containsKey, and put operations.");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Supports only get, containsKey, and put operations.");
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException("Supports only get, containsKey, and put operations.");
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("Supports only get, containsKey, and put operations.");
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException("Supports only get, containsKey, and put operations.");
	}
}