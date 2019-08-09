package com.bnd.network.metrics;

import java.io.Serializable;
import java.util.Collection;

import com.bnd.core.converter.Converter;
import com.bnd.core.converter.ConverterFactory;
import com.bnd.core.metrics.Metrics;
import com.bnd.core.metrics.MetricsFactory;
import com.bnd.core.metrics.MetricsType;

/**
 * @author © Peter Banda
 * @since 2012   
 */
public class DoubleConvertibleMetricsFactory<T> implements MetricsFactory<T>, Serializable {

//	private final Log log = LogFactory.getLog(getClass());

	private final Converter<T,Double> doubleConverter;
	private final Converter<Collection<T>,Collection<Double>> doubleCollectionConverter;
	private final MetricsFactory<Double> doubleMetricsFactory;

	private final ConverterFactory converterFactory = ConverterFactory.getInstance();

	public DoubleConvertibleMetricsFactory(
		final MetricsFactory<Double> doubleMetricsFactory,
		final Converter<T,Double> doubleConverter
	) {
		this.doubleMetricsFactory = doubleMetricsFactory;
		this.doubleConverter = doubleConverter;
		this.doubleCollectionConverter = converterFactory.createCollectionConverter(doubleConverter);
	}

	public DoubleConvertibleMetricsFactory(
		final MetricsFactory<Double> doubleMetricsFactory,
		final Class<T> clazz
	) {
		this.doubleMetricsFactory = doubleMetricsFactory;
		if (clazz != Double.class) {
//			log.info("No double converter provided, but expected for type '" + clazz + "'. Trying to generate one using the standard ConverterFactory.");
			this.doubleConverter = converterFactory.createConverter(clazz, Double.class);
			this.doubleCollectionConverter = converterFactory.createCollectionConverter(doubleConverter);
		} else {
//			log.info("No double converter provided, but ok since the underlaying type is Double. Next time use DoubleDistanceCalculatorFactory directly.");
			this.doubleConverter = null;
			this.doubleCollectionConverter = null;
		}
	}

	@Override
	public Metrics<T> createInstance(final MetricsType metricsType) {
		if (metricsType == null) {
			return null;
		}
		final Metrics<Double> doubleIntegrator = doubleMetricsFactory.createInstance(metricsType);
		if (doubleCollectionConverter == null) {
			// no double converter provided => assuming the type is double
			return (Metrics<T>) doubleIntegrator;
		}
		// if not of type double we have to wrap it into adapter
		return new DoubleConvertibleMetrics(doubleIntegrator, doubleConverter, doubleCollectionConverter);
	}
}