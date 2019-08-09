package com.bnd.math.business.rand;

import java.util.ArrayList;
import java.util.List;

import com.bnd.math.domain.rand.RandomDistribution;

abstract class AbstractRandomDistributionProvider<T> implements RandomDistributionProvider<T> {

	protected final Class<T> clazz;

	protected AbstractRandomDistributionProvider(RandomDistribution<T> distribution) {
		this(distribution.getValueType());
	}

	protected AbstractRandomDistributionProvider(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Class<T> getValueType() {
		return clazz;
	}

	@Override
	public List<T> nextList(int size) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < size; i++) {
			list.add(next());
		}
		return list;
	}
}