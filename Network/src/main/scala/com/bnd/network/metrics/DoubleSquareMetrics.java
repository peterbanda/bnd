package com.bnd.network.metrics;

import java.util.Iterator;
import java.util.List;

import com.bnd.core.BndRuntimeException;
import com.bnd.function.enumerator.ListEnumeratorFactory;

/**
 * @author © Peter Banda
 * @since 2012   
 */
class DoubleSquareMetrics extends DoubleMetrics {

	protected DoubleSquareMetrics(
		ListEnumeratorFactory enumeratorFactory
	) {
		super(enumeratorFactory);
	}

	@Override
	public double calcDistance(
		final List<Double> firstPoint,
		final List<Double> secondPoint
	) {
		if (firstPoint.size() != secondPoint.size()) {
			throw new BndRuntimeException("The number of coordinates of the first and second point is not same.");
		}
		Iterator<Double> secondCoordinateIterator = secondPoint.iterator();
		double sum = 0;
		for (Double firstPointCoordinate : firstPoint) {
			final double leg = firstPointCoordinate - secondCoordinateIterator.next();
			sum += leg * leg;
		}
		return sum;
	}
}
