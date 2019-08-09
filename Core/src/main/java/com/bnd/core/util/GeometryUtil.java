package com.bnd.core.util;

import java.util.ArrayList;
import java.util.Collection;

import com.bnd.core.domain.Point;

/**
 * @author © Peter Banda
 * @since 2010
 */
@Deprecated
// use Metrics instead
public class GeometryUtil {

	@Deprecated
	public static <N extends Number> double getTorusManhattanDistance(
            Point<N> point1, Point<N> point2, Point<N> sizes) {
		double minDistance = 0;
		for (int i = 0; i < point1.getNumberOfCoordinates(); i++) {
			double axisDistance = Math.abs(point1.getCoordinate(i).doubleValue() - point2.getCoordinate(i).doubleValue());
			axisDistance = Math.min(axisDistance, (sizes.getCoordinate(i).doubleValue() - axisDistance));
			minDistance += axisDistance;
		}		
		return minDistance;
	}

	@Deprecated
	public static <N extends Number> Point<N> getTorusManhattanPointLocation(
		Point<N> referalPoint,
		Point<N> point,
		Point<N> sizes
	) {
		Point<N> newPoint = null;
		newPoint = (Point<N>) point.clone();
		for (int i = 0; i < referalPoint.getNumberOfCoordinates(); i++) {
			double rCoordinate = referalPoint.getCoordinate(i).doubleValue();
			double pCoordinate = point.getCoordinate(i).doubleValue();
			N size = sizes.getCoordinate(i);
			double axisDistance = Math.abs(rCoordinate - pCoordinate);
			if (axisDistance > (size.doubleValue() - axisDistance)) {
				if (rCoordinate < pCoordinate) {
					newPoint.decCoordinate(i, size);
				} else {
					newPoint.incCoordinate(i, size);
				}
			}
		}
		return newPoint;
	}

	@Deprecated
	public static double getManhattanDistance(Point<?> point1, Point<?> point2) {
		if (point1.getCoordinates().length != point2.getCoordinates().length) {
			throw new RuntimeException("The number of coordinates of first and second point is not the same.");
		}
		double distance = 0;
		int i = 0;
		for (Number coordinate : point1.getCoordinates()) {
			distance += Math.abs(coordinate.doubleValue() - point2.getCoordinate(i).doubleValue());
			i++;
		}
		return distance;
	}

	@Deprecated
	public static double getSqEuclideanDistance(Point<?> point1, Point<?> point2) {
		if (point1.getCoordinates().length != point2.getCoordinates().length) {
			throw new RuntimeException("The number of coordinates of first and second point is not the same.");
		}
		double distance = 0;
		int i = 0;
		for (Number coordinate : point1.getCoordinates()) {
			double leg = coordinate.doubleValue() - point2.getCoordinates()[i].doubleValue();
			distance += leg * leg;
			i++;
		}
		return distance;
	}

	@Deprecated
	public static double getEuclideanDistance(Point<?> point1, Point<?> point2) {
		return Math.sqrt(getSqEuclideanDistance(point1, point2));
	}

	@Deprecated
	public static Collection<Point<Integer>> getSurroundingPoints(Point<Integer> location) {
		Collection<Point<Integer>> surroundingPoints = new ArrayList<Point<Integer>>();
		int coordinateIndex = 0;
		for (Integer coordinate : location.getCoordinates()) {
			for (int diff = -1; diff < 2; diff += 2) {
				Integer newCoordinate = coordinate.intValue() + diff;
				Point<Integer> surroundingPoint = (Point<Integer>) location.clone();
				surroundingPoint.setCoordinate(coordinateIndex, newCoordinate);
				surroundingPoints.add(surroundingPoint);
			}
			coordinateIndex++;
		}
		return surroundingPoints;
	}

	// assume location is within the given bounds (dimension sizes)
	// all points are located within (0,0,...,0) -> (dimensionSizes[0],dimensionSizes[1],...,dimensionSizes[n-1])
	@Deprecated
	public static Collection<Point<Integer>> getSurroundingPoints(
		Point<Integer> location,
		Point<Integer> dimensionSizes,
		boolean torusFlag
	) {
		Collection<Point<Integer>> surroundingPoints = new ArrayList<Point<Integer>>();
		int coordinateIndex = 0;
		for (Integer coordinate : location.getCoordinates()) {
			for (int diff = -1; diff < 2; diff += 2) {
				Point<Integer> surroundingPoint = null;
				Integer newCoordinate = coordinate.intValue() + diff;
				if (torusFlag && diff == 1
						&& newCoordinate.equals(dimensionSizes.getCoordinate(coordinateIndex))) {
					newCoordinate = new Integer(0);
				} else if (torusFlag && diff == -1
						&& newCoordinate.equals(new Integer(-1))) {
					newCoordinate = dimensionSizes.getCoordinate(coordinateIndex) - 1;
				} else if (!torusFlag) {
					newCoordinate = null;
				}
				if (newCoordinate != null) {
					surroundingPoint = (Point<Integer>) location.clone();
					surroundingPoint.setCoordinate(coordinateIndex, newCoordinate);
					surroundingPoints.add(surroundingPoint);
				}
			}
			coordinateIndex++;
		}
		return surroundingPoints;
	}

	@Deprecated
	public static Point<Integer> getZeroPoint(int numberOfDimensions) {
		Integer[] coordinates = new Integer[numberOfDimensions];
		for (int dimension = 0; dimension < numberOfDimensions; dimension++) {
			coordinates[dimension] = new Integer(0);
		}
		return new Point<Integer>(coordinates);
	}

	@Deprecated
	public static Point<Integer> get2dLocation(int x, int y) {
		return new Point<Integer>(new Integer[] {x, y});
	}

	@Deprecated
	public static Point<Integer> getRectangleSizes(int area) {
		int sizex = new Double(Math.sqrt(area)).intValue();
		while (area % sizex != 0) {
			sizex--;
		}
		if (sizex < area / sizex) {
			sizex = area / sizex;
		}
		return get2dLocation(sizex, area / sizex);
	}
}