package com.bnd.math.business.rand;

import com.bnd.core.util.RandomUtil;
import com.bnd.math.domain.rand.ShapeLocationDistribution;

class NumberLogNormalDistributionProvider<T extends Number> extends AbstractRandomDistributionProvider<T> {

	private final ShapeLocationDistribution<T> distribution;

	protected NumberLogNormalDistributionProvider(ShapeLocationDistribution<T> distribution) {
		super(distribution);
		this.distribution = distribution;
	}

	@Override
	public T next() {
		return RandomUtil.nextLogNormal(clazz, distribution.getLocation(), distribution.getShape());
	}

	@Override
	public Double mean() {
		double location = distribution.getLocation().doubleValue();
		double shape = distribution.getShape().doubleValue();
		return Math.exp(location + (shape / 2));
	}

	@Override
	public Double variance() {
		double location = distribution.getLocation().doubleValue();
		double shape = distribution.getShape().doubleValue();
		return Math.exp(2 * (location + shape)) - Math.exp(2 * location + shape);
	}
}