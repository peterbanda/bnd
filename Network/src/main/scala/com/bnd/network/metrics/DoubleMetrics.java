package com.bnd.network.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.bnd.core.BndRuntimeException;
import com.bnd.core.metrics.Metrics;
import com.bnd.function.enumerator.ListEnumerator;
import com.bnd.function.enumerator.ListEnumeratorFactory;

/**
 * @author © Peter Banda
 * @since 2013
 */
abstract class DoubleMetrics implements Metrics<Double> {

	private final ListEnumeratorFactory enumeratorFactory;
	private final double atomicDiff;

	protected DoubleMetrics(ListEnumeratorFactory enumeratorFactory) {
		this(enumeratorFactory, 1d);
	}

	protected DoubleMetrics(ListEnumeratorFactory enumeratorFactory, double atomicDiff) {
		this.enumeratorFactory = enumeratorFactory;
		this.atomicDiff = atomicDiff;
	}

	@Override
	public double calcDistanceOnTorus(
		final List<Double> firstPoint,
		final List<Double> secondPoint,
		final List<Double> boundaries
	) {
		checkPointDimensions(firstPoint, secondPoint);
		if (!checkIfWithinBoundaries(firstPoint, boundaries)) {
			throw new BndRuntimeException("The point " + firstPoint + " is not within boundaries " + boundaries);
		}
		if (!checkIfWithinBoundaries(secondPoint, boundaries)) {
			throw new BndRuntimeException("The point " + secondPoint + " is not within boundaries " + boundaries);
		}
		final Collection<List<Double>> firstCongruentPoints = getCongruentTorusPoints(firstPoint, boundaries);
		final Collection<List<Double>> secondCongruentPoints = getCongruentTorusPoints(secondPoint, boundaries);
		double minDistance = Double.MAX_VALUE;
		for (List<Double> firstCongruentPoint : firstCongruentPoints) {
			for (List<Double> secondCongruentPoint : secondCongruentPoints) {
				minDistance = Math.min(minDistance, calcDistance(firstCongruentPoint, secondCongruentPoint));				
			}
		}
		return minDistance;
	}

	protected Collection<List<Double>> getSurroundingPoints(
		final List<Double> startingPoint,
		final List<Double> boundaries,
		boolean torusFlag
	) {
		if (boundaries != null) {
			checkPointDimensions(startingPoint, boundaries);
		}
		Collection<List<Double>> surroundingPoints = new ArrayList<List<Double>>();
		for (double diff = -atomicDiff; diff <= atomicDiff; diff += 2 * atomicDiff) {
			int coordinateIndex = 0;
			for (final Double coordinate : startingPoint) {
				// create a new point by adjusting coordinate
				List<Double> newPoint = new ArrayList<Double>(startingPoint);
				newPoint.set(coordinateIndex, coordinate + diff);

				// check if within boundaries and fix if possible (torus flag set)
				if (torusFlag) {
					fixTorusBoundaries(newPoint, boundaries);
				}
				if (checkIfWithinBoundaries(newPoint, boundaries)) {
					surroundingPoints.add(newPoint);
				}
				coordinateIndex++;
			}
		}
		return surroundingPoints;
	}

	@Override
	public Collection<List<Double>> getSurroundingPoints(
		final List<Double> startingPoint,
		final List<Double> boundaries,
		boolean torusFlag,
		Double radius
	) {
		List<List<Double>> surroundingPoints = new ArrayList<List<Double>>();
		List<List<Double>> pointsToVisit = new ArrayList<List<Double>>();
		pointsToVisit.add(startingPoint);
		while (!pointsToVisit.isEmpty()) {
			List<Double> pointToVisit = pointsToVisit.remove(0);
			final Collection<List<Double>> newPoints = getSurroundingPoints(pointToVisit, boundaries, torusFlag);
			for (List<Double> newPoint : newPoints) {
				double distance = 0;
				if (torusFlag) {
					distance = calcDistanceOnTorus(startingPoint, newPoint, boundaries);
				} else {
					distance = calcDistance(startingPoint, newPoint);
				}
				if (!surroundingPoints.contains(newPoint) && !newPoint.equals(startingPoint) &&  distance <= radius) {
					surroundingPoints.add(newPoint);
					pointsToVisit.add(newPoint);
				}
			}
		}
		return surroundingPoints;
	}

	@Override
	public List<Double> getShiftedPoint(
		final List<Double> point,
		final List<Double> vectorShift,
		final List<Double> boundaries,
		final boolean torusFlag
	) {
		checkPointDimensions(point, vectorShift);
		List<Double> shiftedPoint = new ArrayList<Double>();
		Iterator<Double> coordinateShiftIterator = vectorShift.iterator();
		for (Double coordinate : point) {
			shiftedPoint.add(coordinate + coordinateShiftIterator.next());
		}
		if (torusFlag) {
			fixTorusBoundaries(shiftedPoint, boundaries);
		} else if (!checkIfWithinBoundaries(shiftedPoint, boundaries)) {
			return null;
		}
		return shiftedPoint;
	}

	private void fixTorusBoundaries(
		List<Double> point,
		final List<Double> boundaries
	) {
		if (boundaries == null || boundaries.isEmpty()) {
			return;
		}
		checkPointDimensions(point, boundaries);
		Iterator<Double> boundaryIterator = boundaries.iterator();
		int coordinateIndex = 0;
		for (Double coordinate : point) {
			Double boundary = boundaryIterator.next();
			Double newCoordinate = coordinate % boundary;
			if (newCoordinate < 0d) {
				newCoordinate = boundary + newCoordinate;
			}
			point.set(coordinateIndex, newCoordinate);
			coordinateIndex++;
		}
	}

	private Collection<List<Double>> getCongruentTorusPoints(
		List<Double> point,
		final List<Double> boundaries
	) {
		if (boundaries == null || boundaries.isEmpty()) {
			throw new BndRuntimeException("Boundaries expected for torus.");
		}
		checkPointDimensions(point, boundaries);
		Collection<List<Double>> congruentPoints = new ArrayList<List<Double>>();
		// each point is congruent with itself
		congruentPoints.add(point);

		final ListEnumerator<Integer> combinationEnumerator = enumeratorFactory.createInstance(false, 0, point.size() - 1);
		for (List<Integer> coordinateIndeces : combinationEnumerator.enumerate(1, point.size())) {
			List<Double> congruentPoint = new ArrayList<Double>(point);
			for (int coordinateIndex : coordinateIndeces) {
				final double coordinate = point.get(coordinateIndex);
				final double boundary = boundaries.get(coordinateIndex);

				congruentPoint.set(coordinateIndex, coordinate + boundary);
			}
			congruentPoints.add(congruentPoint);
		}
		return congruentPoints;
	}

	@Override
	public boolean checkIfWithinBoundaries(
		final List<Double> point,
		final List<Double> boundaries
	) {
		if (boundaries == null || boundaries.isEmpty()) {
			return true;
		}
		checkPointDimensions(point, boundaries);
		Iterator<Double> boundaryIterator = boundaries.iterator();
		for (Double coordinate : point) {
			if (coordinate < 0d || coordinate >= boundaryIterator.next()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public double calcVectorNorm(List<Double> vector) {
		List<Double> zeroPoint = new ArrayList<Double>();
		for (int i = 0; i < vector.size(); i++) {
			zeroPoint.add(0d);
		}
		return calcDistance(vector, zeroPoint);
	}

	/**
	 * @throws BndRuntimeException
	 */ 
	private void checkPointDimensions(
		final List<Double> firstPoint,
		final List<Double> secondPoint
	) {
		if (firstPoint == null || secondPoint == null) {
			return;
		}
		if (firstPoint.size() != secondPoint.size()) {
			throw new BndRuntimeException("The number of coordinates of the point " + firstPoint.toString() + " and the point " + secondPoint.toString() + " is not same.");
		}
	}
}