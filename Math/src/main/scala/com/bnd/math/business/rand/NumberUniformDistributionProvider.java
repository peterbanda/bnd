package com.bnd.math.business.rand;

import com.bnd.core.util.RandomUtil;
import com.bnd.math.domain.rand.UniformDistribution;

class NumberUniformDistributionProvider<T extends Number> extends AbstractRandomDistributionProvider<T> {

	private final UniformDistribution<T> distribution;

	protected NumberUniformDistributionProvider(UniformDistribution<T> distribution) {
		super(distribution);
		this.distribution = distribution;
	}

	@Override
	public T next() {
		return RandomUtil.next(clazz, distribution.getFrom(), distribution.getTo());
	}

	@Override
	public Double mean() {
		return (distribution.getFrom().doubleValue() + distribution.getTo().doubleValue()) / 2;
	}

	@Override
	public Double variance() {
		double diff = distribution.getTo().doubleValue() - distribution.getFrom().doubleValue();
		return diff * diff / 12;
	}
}