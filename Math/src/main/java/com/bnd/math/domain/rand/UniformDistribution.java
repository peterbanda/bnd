package com.bnd.math.domain.rand;

/** 
 * @author © Peter Banda
 * @since 2011
 */
public class UniformDistribution<T> extends RandomDistribution<T> {

	private T from;
	private T to;

	public UniformDistribution() {
		// no-op
	}

	public UniformDistribution(T from, T to) {
		this.from = from;
		this.to = to;
	}

	public T getFrom() {
		return from;
	}

	public void setFrom(T from) {
		this.from = from;
	}

	public T getTo() {
		return to;
	}

	public void setTo(T to) {
		this.to = to;
	}

	@Override
	public RandomDistributionType getType() {
		return RandomDistributionType.Uniform;
	}

	@Override
	public Class<T> getValueType() {
		return (Class<T>) from.getClass();
	}
}