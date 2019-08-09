package com.bnd.core.metrics;

import java.util.Collection;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012   
 */
public interface Metrics<T> {

	double calcDistance(
		List<T> firstPoint,
		List<T> secondPoint);

	double calcVectorNorm(List<T> vector);
	
	double calcDistanceOnTorus(
		List<T> firstPoint,
		List<T> secondPoint,
		List<T> boundaries);

	Collection<List<T>> getSurroundingPoints(
		List<T> startingPoint,
		List<T> boundaries,
		boolean torusFlag,
		T radius);

	boolean checkIfWithinBoundaries(
		List<T> point,
		List<T> boundaries);

	List<T> getShiftedPoint(
		List<T> point,
		List<T> vectorShift,
		List<T> boundaries,
		boolean torusFlag);
}