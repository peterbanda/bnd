package com.bnd.math.domain.rand;

/** 
 * @author © Peter Banda
 * @since 2013
 */
public class DiscreteDistribution<T> extends RandomDistribution<T> {

	private double[] probabilities;
	private T[] values;

	public DiscreteDistribution() {
		// no-op
	}

	public DiscreteDistribution(double[] probabilities, T[] values) {
		this.probabilities = probabilities;
		this.values = values;
	}

	public double[] getProbabilities() {
		return probabilities;
	}

	public void setProbabilities(double[] probabilities) {
		this.probabilities = probabilities;
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
		return RandomDistributionType.Discrete;
	}
}