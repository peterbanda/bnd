package com.bnd.network.metrics;

import java.util.*;

import junit.framework.TestCase;

import org.junit.Test;

import com.bnd.core.metrics.Metrics;
import com.bnd.core.metrics.MetricsFactory;
import com.bnd.core.metrics.MetricsType;
import com.bnd.core.util.RandomUtil;
import com.bnd.function.enumerator.ListEnumeratorFactoryImpl;

public class MetricsTest extends TestCase {

	private static final int REPETITIONS = 20;
	MetricsFactory<Double> doubleMetricsFactory = new DoubleMetricsFactory(new ListEnumeratorFactoryImpl());
	MetricsFactory<Integer> integerMetricsFactory = new DoubleConvertibleMetricsFactory<Integer>(doubleMetricsFactory, Integer.class);

	@Test
	public void testDistanceOnTorus() {
		Metrics<Double> manhattanMetrics = doubleMetricsFactory.createInstance(MetricsType.Manhattan);
		List<Double> firstPoint = Arrays.asList(new Double[] {0d, 9d});
		List<Double> secondPoint = Arrays.asList(new Double[] {8d, 0d});
		List<Double> boundaries = Arrays.asList(new Double[] {9d, 10d});
		final double distance = manhattanMetrics.calcDistanceOnTorus(firstPoint, secondPoint, boundaries);
		assertEquals(2d, distance);
	}

	@Test
	public void testDoubleManhattanGetSurroundingPoints() {
		testDoubleGetSurroundingPoints(MetricsType.Manhattan);
	}

	@Test
	public void testDoubleMaxGetSurroundingPoints() {
		testDoubleGetSurroundingPoints(MetricsType.Max);
	}

	@Test
	public void testIntegerManhattanGetSurroundingPoints() {
		testIntegerGetSurroundingPoints(MetricsType.Manhattan);
	}

	@Test
	public void testIntegerMaxGetSurroundingPoints() {
		testIntegerGetSurroundingPoints(MetricsType.Max);
	}

	private void testDoubleGetSurroundingPoints(MetricsType metricsType) {
		Metrics<Double> metrics = doubleMetricsFactory.createInstance(metricsType);
		List<Double> boundariesx = Arrays.asList(new Double[] {9d, 10d, 11d});
		for (int dimensions = 1; dimensions < 4; dimensions++) {
			List<Double> boundaries = new ArrayList<Double>();
			for (int i = 0; i < dimensions; i++) {
				boundaries.add(boundariesx.get(i));
			}
			for (int radius = 1; radius < 5; radius ++) {
				for (int repetition = 0; repetition < REPETITIONS; repetition++) {
					List<Double> point = new ArrayList<Double>();
					for (int i = 0; i < boundaries.size(); i++) {
						point.add(new Double(RandomUtil.nextInt(boundariesx.get(i).intValue())));
					}
					Collection<List<Double>> surroundingPoints = metrics.getSurroundingPoints(point, boundaries, true, (double) radius);
					checkDoubleBoundaries(surroundingPoints, boundaries);
					checkSurroundingPointsNumber(metricsType, point, boundaries, radius, surroundingPoints.size());
				}
			}
		}
	}

	private void testIntegerGetSurroundingPoints(MetricsType metricsType) {
		Metrics<Integer> metrics = integerMetricsFactory.createInstance(metricsType);
		List<Integer> boundariesx = Arrays.asList(new Integer[] {9, 10, 11});
		for (int dimensions = 1; dimensions < 4; dimensions++) {
			List<Integer> boundaries = new ArrayList<Integer>();
			for (int i = 0; i < dimensions; i++) {
				boundaries.add(boundariesx.get(i));
			}
			for (int radius = 1; radius < 5; radius ++) {
				for (int repetition = 0; repetition < REPETITIONS; repetition++) {
					List<Integer> point = new ArrayList<Integer>();
					for (int i = 0; i < boundaries.size(); i++) {
						point.add(new Integer(RandomUtil.nextInt(boundariesx.get(i).intValue())));
					}
					Collection<List<Integer>> surroundingPoints = metrics.getSurroundingPoints(point, boundaries, true, radius);
					checkIntegerBoundaries(surroundingPoints, boundaries);
					checkSurroundingPointsNumber(metricsType, point, boundaries, radius, surroundingPoints.size());
				}
			}
		}
	}

	private void checkDoubleBoundaries(
		Collection<List<Double>> points,
		List<Double> boundaries
	) {
		for (List<Double> point : points) {
			Iterator<Double> boundaryIterator = boundaries.iterator();
			for (Double coordinate : point) {
				assertTrue(coordinate >= 0);
				assertTrue(coordinate < boundaryIterator.next());
			}
		}
	}

	private void checkIntegerBoundaries(
		Collection<List<Integer>> points,
		List<Integer> boundaries
	) {
		for (List<Integer> point : points) {
			Iterator<Integer> boundaryIterator = boundaries.iterator();
			for (Integer coordinate : point) {
				assertTrue(coordinate >= 0);
				assertTrue(coordinate < boundaryIterator.next());
			}
		}
	}

	private void checkSurroundingPointsNumber(
		final MetricsType metricsType,
		final List<?> point,
		final List<?> boundaries,
		final double radius,
		final int surroundingPointsNumber
	) {
		final String message = "The number of surrounding points for the point " + point + ", boundaries " + boundaries + ", and radius " + radius;
		switch (metricsType) { 
			case Manhattan:
				checkManhattanSurroundingPointsNumber(point.size(), radius, surroundingPointsNumber, message);
				break;
			case Max:
				checkMaxSurroundingPointsNumber(point.size(), radius, surroundingPointsNumber, message);
				break;
			default:
		}
	}

	private void checkManhattanSurroundingPointsNumber(
		final double dimensionsNumber,
		final double radius,
		final double surroundingPointsNumber,
		final String message
	) {
		if (dimensionsNumber == 1) {
			assertEquals(message, 2 * radius, surroundingPointsNumber);
		} else if (dimensionsNumber == 2) {
			assertEquals(message, 2 * radius * (radius + 1), surroundingPointsNumber);
		} else if (dimensionsNumber == 3) {
			assertEquals(message, 2 * radius * (radius * (2 * radius + 3) + 4) / 3, surroundingPointsNumber);
		}
	}

	private void checkMaxSurroundingPointsNumber(
		final double dimensionsNumber,
		final double radius,
		final double surroundingPointsNumber,
		final String message
	) {
		double edgeLength = 2 * radius + 1;
		if (dimensionsNumber == 1) {
			assertEquals(message, 2 * radius, surroundingPointsNumber);
		} else if (dimensionsNumber == 2) {
			assertEquals(message, edgeLength * edgeLength - 1, surroundingPointsNumber);
		} else if (dimensionsNumber == 3) {
			assertEquals(message, edgeLength * edgeLength * edgeLength - 1, surroundingPointsNumber);
		}
	}
}