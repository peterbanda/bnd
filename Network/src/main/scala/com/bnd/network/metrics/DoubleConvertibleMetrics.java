package com.bnd.network.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bnd.core.converter.Converter;
import com.bnd.core.metrics.Metrics;

/**
 * @author © Peter Banda
 * @since 2012   
 */
public class DoubleConvertibleMetrics<T> implements Metrics<T> {

	private final Metrics<Double> doubleMetrics;
	private final Converter<T, Double> doubleConverter;
	private final Converter<List<T>, List<Double>> doubleCollectionConverter;  // TODO: do we need both converters?

	DoubleConvertibleMetrics(
		Metrics<Double> doubleMetrics,
		Converter<T, Double> doubleConverter,
		Converter<List<T>,List<Double>> doubleCollectionConverter
	) {
		this.doubleMetrics = doubleMetrics;
		this.doubleConverter = doubleConverter;
		this.doubleCollectionConverter = doubleCollectionConverter;
	}

	@Override
	public double calcDistance(
		List<T> firstPoint,
		List<T> secondPoint
	) {
		return doubleMetrics.calcDistance(
			doubleCollectionConverter.convert(firstPoint),
			doubleCollectionConverter.convert(secondPoint));
	}

	@Override
	public double calcVectorNorm(List<T> vector) {
		return doubleMetrics.calcVectorNorm(
				doubleCollectionConverter.convert(vector));
	}

	@Override
	public double calcDistanceOnTorus(
		List<T> firstPoint,
		List<T> secondPoint,
		List<T> boundaries
	) {
		return doubleMetrics.calcDistanceOnTorus(
				doubleCollectionConverter.convert(firstPoint),
				doubleCollectionConverter.convert(secondPoint),
				doubleCollectionConverter.convert(boundaries));
	}

	@Override
	public boolean checkIfWithinBoundaries(
		List<T> point,
		List<T> boundaries
	) {
		return doubleMetrics.checkIfWithinBoundaries(
				doubleCollectionConverter.convert(point),
				doubleCollectionConverter.convert(boundaries));
	}

	@Override
	public Collection<List<T>> getSurroundingPoints(
		List<T> coordinates,
		List<T> maxCoordinates,
		boolean torusFlag,
		T radius
	) {
		final Collection<List<Double>> doubleSurroundingPoints = doubleMetrics.getSurroundingPoints(
				doubleCollectionConverter.convert(coordinates),
				doubleCollectionConverter.convert(maxCoordinates),
				torusFlag,
				doubleConverter.convert(radius));

		Collection<List<T>> surroundingPoints = new ArrayList<List<T>>();
		for (List<Double> doubleSurroundingPoint : doubleSurroundingPoints) {
			surroundingPoints.add(doubleCollectionConverter.reconvert(doubleSurroundingPoint));
		}
		return surroundingPoints;
	}

	@Override
	public List<T> getShiftedPoint(
		List<T> point,
		List<T> vectorShift,
		List<T> boundaries,
		boolean torusFlag
	) {
		return doubleCollectionConverter.reconvert(
				doubleMetrics.getShiftedPoint(
					doubleCollectionConverter.convert(point),
					doubleCollectionConverter.convert(vectorShift),
					doubleCollectionConverter.convert(boundaries),
					torusFlag));
	}
}