package com.bnd.core;

abstract class ArrayListKeyInnerNode<K,V> extends AbstractListKeyInnerNode<K, V> {

	private final ListKeyNode<K,V>[] childNodes;

	protected ArrayListKeyInnerNode(int branchingFactor) {
		this.childNodes = new ListKeyNode[branchingFactor];
	}

	protected abstract int getArrayIndex(K key);

	@Override
	protected ListKeyNode<K, V> getChildNode(final K key) {
		return childNodes[getArrayIndex(key)];
	}

	@Override
	protected void addChildNode(final K key, final ListKeyNode<K, V> childNode) {
		childNodes[getArrayIndex(key)] = childNode;	
	}
}