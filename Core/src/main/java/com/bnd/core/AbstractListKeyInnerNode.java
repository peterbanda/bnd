package com.bnd.core;

import java.util.Iterator;

import com.bnd.core.reflection.ReflectionUtil;

public abstract class AbstractListKeyInnerNode<K,V> implements ListKeyNode<K,V> {

	protected abstract ListKeyNode<K,V> getChildNode(K key);

	protected abstract void addChildNode(K key, ListKeyNode<K,V> childNode);

	@Override
	public V get(final Iterator<K> keyIterator) {
		return getChildNode(keyIterator.next()).get(keyIterator);
	}

	@Override
	public void put(final Iterator<K> keyIterator, final V value) {
		final K childKey = keyIterator.next();
		ListKeyNode<K, V> childNode = getChildNode(childKey);
		if (childNode == null) {
			if (keyIterator.hasNext()) {
				childNode = newInstance();
			} else {
				childNode = new ListKeyLeaf<K,V>();				
			}
			addChildNode(childKey, childNode);
		}
		childNode.put(keyIterator, value);
	}

	protected ListKeyNode<K,V> newInstance() {
		return ReflectionUtil.createNewInstance(getClass());
	}
}