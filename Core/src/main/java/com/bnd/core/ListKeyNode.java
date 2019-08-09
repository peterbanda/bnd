package com.bnd.core;

import java.util.Iterator;

public interface ListKeyNode<K,V> {

	V get(Iterator<K> keyIterator);

	void put(Iterator<K> keyIterator, V value);
}