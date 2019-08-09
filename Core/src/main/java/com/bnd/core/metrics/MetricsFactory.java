package com.bnd.core.metrics;

/**
 * @author © Peter Banda
 * @since 2012   
 */
public interface MetricsFactory<T> {

	Metrics<T> createInstance(MetricsType metricsType);
}