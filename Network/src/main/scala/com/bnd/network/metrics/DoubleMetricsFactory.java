package com.bnd.network.metrics;

import com.bnd.core.BndRuntimeException;
import com.bnd.core.metrics.Metrics;
import com.bnd.core.metrics.MetricsFactory;
import com.bnd.core.metrics.MetricsType;
import com.bnd.function.enumerator.ListEnumeratorFactory;

import java.io.Serializable;

/**
 * @author © Peter Banda
 * @since 2012   
 */
public class DoubleMetricsFactory implements MetricsFactory<Double>, Serializable {

	private final ListEnumeratorFactory listEnumeratorFactory;

	public DoubleMetricsFactory(ListEnumeratorFactory enumeratorFactory) {
		this.listEnumeratorFactory = enumeratorFactory;
	}

	@Override
	public Metrics<Double> createInstance(MetricsType metricsType) {
		if (metricsType == null) {
			return null;
		}
		// TODO: what about caching square metrics?
		final Metrics<Double> squareMetrics = new DoubleSquareMetrics(listEnumeratorFactory);

		Metrics<Double> metrics = null;
		switch (metricsType) {
			case Manhattan:
				metrics = new DoubleManhattanMetrics(listEnumeratorFactory);
				break;
			case Euclidean:
				metrics = new DoubleEuclideanMetrics(listEnumeratorFactory, squareMetrics);
				break;
			case Max:
				metrics = new DoubleMaxMetrics(listEnumeratorFactory);
				break;
			case MeanSquare:
				metrics = new DoubleMeanSquareMetrics(listEnumeratorFactory, squareMetrics);
				break;
			case RootMeanSquare:
				metrics = new DoubleRootMeanSquareMetrics(listEnumeratorFactory, squareMetrics);
				break;
			case SumOfSquares:
				metrics = new DoubleSumOfSquaresMetrics(listEnumeratorFactory, squareMetrics);
				break;
			default:
				throw new BndRuntimeException("Metrics of type '" + metricsType + "' not recognized.");
		}
		return metrics;
	}
}
