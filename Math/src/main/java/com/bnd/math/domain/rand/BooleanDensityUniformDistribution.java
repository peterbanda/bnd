package com.bnd.math.domain.rand;

public class BooleanDensityUniformDistribution extends RandomDistribution<Boolean> {

	// Needed for Hibernate only : TODO
	protected void setType(RandomDistributionType type) {
		
	}

	@Override
	public RandomDistributionType getType() {
		return RandomDistributionType.BooleanDensityUniform;
	}

	@Override
	public Class<Boolean> getValueType() {
		return Boolean.class;
	}
}