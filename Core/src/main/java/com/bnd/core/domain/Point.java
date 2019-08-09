package com.bnd.core.domain;

import java.util.Arrays;

import com.bnd.core.BndRuntimeException;
import com.bnd.core.util.ConversionUtil;

/**
 * @author © Peter Banda
 * @since 2010
 */
public class Point<N extends Number> implements Cloneable {

	private N[] coordinates;

	public Point(N[] coordinates) {
		if (coordinates == null) {
			throw new BndRuntimeException("Each point must have some coordinates!");
		}
		this.coordinates = coordinates;
	}

	public N[] getCoordinates() {
		return coordinates;
	}

	public int getNumberOfCoordinates() {
		return coordinates.length;
	}

	public N getCoordinate(int index) {
		checkElement(index, coordinates);
		return coordinates[index];
	}

	public void setCoordinate(int index, N value) {
		checkElement(index, coordinates);
		coordinates[index] = value;
	}

	public void incCoordinate(int index, N value) {
		setCoordinate(index, ConversionUtil.convert(
				coordinates[index].doubleValue() + value.doubleValue(), (Class<N>) coordinates[index].getClass()));
	}

	public void decCoordinate(int index, N value) {
		setCoordinate(index, ConversionUtil.convert(
				coordinates[index].doubleValue() - value.doubleValue(), (Class<N>) coordinates[index].getClass()));
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (N coordinate : coordinates) {
			sb.append(coordinate);
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() -1);
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(coordinates);
	}

	@Override
	public boolean equals(Object object) {
		if ((object == null)
		 || !(object instanceof Point<?>)) {
			return false;
		}
		Point<N> point2 = (Point<N>) object;
		return this == point2
			|| Arrays.equals(coordinates, point2.coordinates);
	}

	@Override
	public Point<N> clone() {
		return new Point<N>(coordinates.clone());
	}

	/**
	 * Checks if there is not-NULL element at given index in array.
	 * If the element does not exist, <code>RuntimeException</code> is thrown
	 * 
	 * @param indexOfElement
	 * @param array
	 */
	private static void checkElement(int indexOfElement, Object[] array) {
		if (array == null || array.length <= indexOfElement || array[indexOfElement] == null) {
			throw new BndRuntimeException("Element with index " + indexOfElement + " does not exist in array " + array + "!");
		}
	}
}