package com.bnd.network.metrics;

import java.util.List;

import com.bnd.core.metrics.Metrics;
import com.bnd.function.enumerator.ListEnumeratorFactory;

/**
 * @author © Peter Banda
 * @since 2012   
 */
class DoubleEuclideanMetrics extends DoubleMetrics {

	private final Metrics<Double> squareMetrics;

	protected DoubleEuclideanMetrics(
		ListEnumeratorFactory enumeratorFactory,
		Metrics<Double> squareMetrics
	) {
		super(enumeratorFactory);
		this.squareMetrics = squareMetrics;
	}

	@Override
	public double calcDistance(
		final List<Double> firstPoint,
		final List<Double> secondPoint
	) {
		final double squareDistance = squareMetrics.calcDistance(firstPoint, secondPoint);
		return Math.sqrt(squareDistance);
	}
}