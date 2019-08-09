package com.bnd.math.domain.rand;

import com.bnd.core.domain.TechnicalDomainObject;

/** 
 * @author © Peter Banda
 * @since 2011
 */
public abstract class RandomDistribution<T> extends TechnicalDomainObject {

	public abstract RandomDistributionType getType();

	public abstract Class<T> getValueType();

	public static RandomDistribution<Double> createNormalDistribution(
		Double mean,
		Double std
	) {
		return new ShapeLocationDistribution<Double>(RandomDistributionType.Normal, mean, std);
	}

	public static RandomDistribution<Double> createLogNormalDistribution(
		Double location,
		Double shape
	) {
		return new ShapeLocationDistribution<Double>(RandomDistributionType.LogNormal, location, shape);
	}

	public static RandomDistribution<Double> createPositiveNormalDistribution(
		Double location,
		Double shape
	) {
		return new ShapeLocationDistribution<Double>(RandomDistributionType.PositiveNormal, location, shape);
	}

	public static <T extends Number> RandomDistribution<T> createNormalDistribution(
		Class<T> valueType,
		Double mean,
		Double std
	) {
		return new ShapeLocationDistribution<T>(RandomDistributionType.Normal, mean, std, valueType);
	}

	public static <T extends Number> RandomDistribution<T> createLogNormalDistribution(
		Class<T> valueType,
		Double location,
		Double shape
	) {
		return new ShapeLocationDistribution<T>(RandomDistributionType.LogNormal, location, shape, valueType);
	}

	public static <T extends Number> RandomDistribution<T> createPositiveNormalDistribution(
		Class<T> valueType,
		Double location,
		Double shape
	) {
		return new ShapeLocationDistribution<T>(RandomDistributionType.PositiveNormal, location, shape, valueType);
	}
}