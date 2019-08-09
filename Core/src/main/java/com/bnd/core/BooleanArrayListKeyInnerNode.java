package com.bnd.core;

public final class BooleanArrayListKeyInnerNode<V> extends ArrayListKeyInnerNode<Boolean,V> {

	public BooleanArrayListKeyInnerNode() {
		super(2);
	}

	@Override
	protected int getArrayIndex(Boolean key) {
		return key ? 1 : 0;
	}

	@Override
	protected ListKeyNode<Boolean, V> newInstance() {
		return new BooleanArrayListKeyInnerNode<V>();
	}
}