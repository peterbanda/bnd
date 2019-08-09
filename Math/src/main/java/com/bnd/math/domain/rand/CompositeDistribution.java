package com.bnd.math.domain.rand;

/** 
 * @author © Peter Banda
 * @since 2013
 */
public class CompositeDistribution<T> extends RandomDistribution<T> {

	static public enum CompositeFunction {
		PLUS, TIMES
	}

	private RandomDistribution<T> distribution1;
	private RandomDistribution<T> distribution2;
	private CompositeFunction function;

	public CompositeDistribution() {
		// no-op
	}

	public CompositeDistribution(
		RandomDistribution<T> distribution1,
		RandomDistribution<T> distribution2,
		CompositeFunction function
	) {
		this.distribution1 = distribution1;
		this.distribution2 = distribution2;
		this.function = function;
	}

	public RandomDistribution<T> getDistribution1() {
		return distribution1;
	}

	public void setDistribution1(RandomDistribution<T> distribution1) {
		this.distribution1 = distribution1;
	}

	public RandomDistribution<T> getDistribution2() {
		return distribution2;
	}

	public void setDistribution2(RandomDistribution<T> distribution2) {
		this.distribution2 = distribution2;
	}

	public CompositeFunction getFunction() {
		return function;
	}

	public void setFunction(CompositeFunction function) {
		this.function = function;
	}

	@Override
	public RandomDistributionType getType() {
		return RandomDistributionType.Composite;
	}

	@Override
	public Class<T> getValueType() {
		return distribution1.getValueType();
	}
}