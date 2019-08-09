package com.bnd.core;

import java.util.HashMap;
import java.util.Map;

public final class MapListKeyInnerNode<K,V> extends AbstractListKeyInnerNode<K, V> {

	private Map<K, ListKeyNode<K,V>> childNodes = new HashMap<K, ListKeyNode<K,V>>();

	@Override
	protected ListKeyNode<K, V> getChildNode(K key) {
		return childNodes.get(key);
	}

	@Override
	protected void addChildNode(K key, ListKeyNode<K, V> childNode) {
		childNodes.put(key, childNode);
	}

	@Override
	protected ListKeyNode<K, V> newInstance() {
		return new MapListKeyInnerNode<K, V>();
	}
}