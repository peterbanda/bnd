package com.bnd.math.domain.rand;

/** 
 * @author © Peter Banda
 * @since 2015
 */
public class UniformDiscreteDistribution<T> extends RandomDistribution<T> {

	private T[] values;

	public UniformDiscreteDistribution() {
		// no-op
	}

	public UniformDiscreteDistribution(T[] values) {
		this.values = values;
	}

	public T[] getValues() {
		return values;
	}

	public void setValues(T[] values) {
		this.values = values;
	}

	@Override
	public Class<T> getValueType() {
		return (Class<T>) values[0].getClass();
	}

	@Override
	public RandomDistributionType getType() {
		return RandomDistributionType.UniformDiscrete;
	}
}