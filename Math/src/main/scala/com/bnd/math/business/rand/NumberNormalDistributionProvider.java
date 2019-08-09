package com.bnd.math.business.rand;

import com.bnd.core.util.RandomUtil;
import com.bnd.math.domain.rand.ShapeLocationDistribution;

class NumberNormalDistributionProvider<T extends Number> extends AbstractRandomDistributionProvider<T> {

	private final ShapeLocationDistribution<T> distribution;

	protected NumberNormalDistributionProvider(ShapeLocationDistribution<T> distribution) {
		super(distribution);
		this.distribution = distribution;
	}

	@Override
	public T next() {
		return RandomUtil.nextNormal(clazz, distribution.getLocation(), distribution.getShape());
	}

  private Double standardDeviation() {
    return distribution.getShape();
  }

	@Override
	public Double mean() {
		return distribution.getLocation();
	}

	@Override
	public Double variance() {
		return standardDeviation() * standardDeviation();
	}
}