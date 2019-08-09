package com.bnd.core.domain;

public class KeyHolderExtractor<KEY, O extends KeyHolder<KEY>> implements KeyExtractor<KEY, O> {

	@Override
	public KEY getKey(O object) {
		return object.getKey();
	}

	@Override
	public void setKey(O object, KEY key) {
		object.setKey(key);
	}
}