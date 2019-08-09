package com.bnd.core.util;

import java.util.*;

import com.bnd.core.BndRuntimeException;
import com.bnd.core.domain.ValueBound;
import com.bnd.core.reflection.ReflectionUtil;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class RandomUtil {

	private static final Random random = new Random();
	// high quality random
//	private static final Random secureRandom = new SecureRandom();

	/**
	 * Generates random boolean - true or false.
	 * 
	 * @return Random boolean
	 */
	public static boolean nextBoolean() {
		return random.nextBoolean();
	}

	/**
	 * Generates random float from the interval <0,1).
	 * 
	 * @return Random float
	 */
	public static double nextFloat() {
		return random.nextFloat();
	}

	/**
	 * Generates random double from the interval <0,1).
	 * 
	 * @return Random double
	 */
	public static double nextDouble() {
		return random.nextDouble();
	}

	/**
	 * Generates random double from the interval <0,255).
	 * 
	 * @return Random byte
	 */
	public static byte nextByte() {
		return (byte) random.nextInt(Byte.MAX_VALUE);
	}

	/**
	 * Generates random double from a given interval.
	 * Note: Left side of the interval (<code>from</code>) is included, but right side (<code>to</code>) is excluded.
	 * 
	 * @param from
	 * @param to
	 * @return Random double
	 */
	public static double nextDouble(double from, double to) {
		return from + (to - from) * nextDouble();
	}

	/**
	 * Generates random double from a given interval.
	 * 
	 * @param to
	 * @return Random double
	 */
	public static double nextDouble(double to) {
		return to * nextDouble();
	}

	/**
	 * Generates random float from a given interval.
	 * Note: Left side of the interval (<code>from</code>) is included, but right side (<code>to</code>) is excluded.
	 * 
	 * @param from
	 * @param to
	 * @return Random float
	 */
	public static double nextFloat(float from, float to) {
		return from + (to - from) * nextFloat();
	}

	/**
	 * Generates random float from a given interval.
	 * 
	 * @param to
	 * @return Random float
	 */
	public static double nextFloat(float to) {
		return to * nextFloat();
	}

	/**
	 * Generates random integer from a given interval.
	 * Note: Left side of the interval (<code>from</code>) is included, but right side (<code>to</code>) is excluded.
	 * 
	 * @param from
	 * @param to
	 * @return Random integer
	 */
	public static int nextInt(int from, int to) {
		return from + random.nextInt(to - from);
	}

	/**
	 * Generates random integer from a given interval.
	 * 
	 * @param to
	 * @return Random integer
	 */
	public static int nextInt(int to) {
		return random.nextInt(to);
	}

	/**
	 * Generates random integer from a given interval except a not allowed number.
	 * 
	 * @param to
	 * @param notAllowedNum
	 * @return Random integer
	 */
	public static int nextIntExcept(int to, int notAllowedNum) {
		return (nextInt(1, to) + notAllowedNum) % to;
	}

	/**
	 * Generates random long from the given interval.
	 * Note: Left side of the interval (<code>from</code>) is included, but right side (<code>to</code>) is excluded.
	 * 
	 * @param from
	 * @param to
	 * @return Random long
	 */
	public static long nextLong(long from, long to) {
		// TODO
		return (long) nextDouble((double) from, (double) to);
	}

	/**
	 * Generates random byte from the given interval.
	 * Note: Left side of the interval (<code>from</code>) is included, but right side (<code>to</code>) is excluded.
	 * 
	 * @param from
	 * @param to
	 * @return Random byte
	 */
	public static byte nextByte(byte from, byte to) {
		return (byte) nextInt(from, to);
	}

	/**
	 * Generates random String of given length.
	 * 
	 * @param length
	 * @return Random String
	 */
	public static String nextString(int length) {
		byte[] bytes = new byte[length];
		random.nextBytes(bytes);
		return new String(bytes);
	}

	/**
	 * Generates random String of given length.
	 * 
	 * @param length
	 * @return Random String
	 */
	public static String nextReadableString(int length) {
		byte[] bytes = new byte[length];
		// not optimal, but good enough
		for (int i = 0; i < length; ) {
			byte b = nextByte();
			char ch = (char) b;
			if (Character.isLetterOrDigit(ch)) {
				bytes[i] = b;
				i++;
			}
		}
		return new String(bytes);
	}

	/**
	 * Generates random Date from a given interval.
	 * Note: Left side of the interval (<code>from</code>) is included, but right side (<code>to</code>) is excluded.
	 * 
	 * @param from
	 * @param to
	 * @return Random Date
	 */
	public static Date nextDate(Date from, Date to) {
		final long ms = nextLong(from.getTime(), to.getTime());
		return new Date(ms);
	}

	/**
	 * Generic random generation for number types.
	 * 
	 * @param numberClazz
	 * @param from
	 * @param to
	 * @return
	 */
	public static <T extends Number> T next(Class<T> numberClazz, T from, T to) {
		if (ObjectUtil.areObjectsEqual(from, to)) {
			return from;
		}
		T result = null;
		if (numberClazz == Long.class) {
			result = (T) new Long(nextLong((Long) from, (Long) to));
		} else if (numberClazz == Integer.class) {
			result = (T) new Integer(nextInt((Integer) from, (Integer) to));
		} else if (numberClazz == Byte.class) {
			result = (T) new Byte(nextByte((Byte) from, (Byte) to));
		} else if (numberClazz == Double.class) {
			result = (T) new Double(nextDouble((Double) from, (Double) to));
		} else if (numberClazz == Float.class) {
			result = (T) new Float(nextDouble((Float) from, (Float) to));
		}
		return result;
	}

	public static List<Boolean> nextBooleanList(int size) {
		List<Boolean> booleanList = new ArrayList<Boolean>();
		for (int i = 0; i < size; i++) {
			booleanList.add(new Boolean(nextBoolean()));
		}
		return booleanList;
	}

	public static Collection<Integer> nextPermutation(Integer numberOfObjects) {
		List<Integer> indeces = new ArrayList<Integer>();
		for (int i = 0; i < numberOfObjects; i++) {
			indeces.add(i);
		}
		Collections.shuffle(indeces);
		return indeces;
	}

	public static <T> Collection<T> nextPermutation(Collection<T> objects) {
		List<T> shuffledObjects = new ArrayList<T>(objects);
		Collections.shuffle(shuffledObjects);
		return shuffledObjects;
	}

	public static Collection<Integer> nextElementsWithoutRepetitions(
		Integer numberOfObjects,
		Integer numberOfObjectsToSelect
	) {
		Collection<Integer> indeces = new ArrayList<Integer>();
		for (int i = 0; i < numberOfObjects; i++) {
			indeces.add(i);
		}
		return nextElementsWithoutRepetitions(indeces, numberOfObjectsToSelect);
	}

	public static Collection<Integer> nextElementsWithoutRepetitionsExcept(
		Integer indexToOmit,
		Integer numberOfObjects,
		Integer numberOfObjectsToSelect
	) {
		Collection<Integer> indeces = new ArrayList<Integer>();
		for (int i = 0; i < numberOfObjects; i++) {
			if (!ObjectUtil.areObjectsEqual(i, indexToOmit)) {
				indeces.add(i);
			}
		}
		return nextElementsWithoutRepetitions(indeces, numberOfObjectsToSelect);
	}

	public static <T> Collection<T> nextElementsWithoutRepetitions(Collection<T> objects, int numberOfObjectsToSelect) {
		if (objects.size() < numberOfObjectsToSelect) {
			throw new BndRuntimeException("Cannot select '" + numberOfObjectsToSelect + "' from '" + objects.size() + "' elements.");
		}
		List<T> selectedObjects = new ArrayList<T>(objects);
		Collections.shuffle(selectedObjects);
		final int objectsToRemoveNumber = objects.size() - numberOfObjectsToSelect;
		for (int i = 0; i < objectsToRemoveNumber; i++) {
			selectedObjects.remove(0);
		}
		return selectedObjects;
	}

	public static <T> Collection<T> nextElementsWithRepetitions(Collection<T> objects, int numberOfObjectsToSelect) {
		T firstObject = ObjectUtil.getFirst(objects);
		T[] emptyArray = ReflectionUtil.createNewArray(firstObject, 0);
		return nextElementsWithRepetitions(objects.toArray(emptyArray), numberOfObjectsToSelect);
	}

	public static <T> Collection<T> nextElementsWithoutRepetitions(T[] objects, int numberOfObjectsToSelect) {
		return nextElementsWithoutRepetitions(Arrays.asList(objects), numberOfObjectsToSelect);
	}

	public static <T> Collection<T> nextElementsWithRepetitions(T[] objects, int numberOfObjectsToSelect) {
		final int objectsNumber = objects.length;
		Collection<T> selectedObjects = new ArrayList<T>(numberOfObjectsToSelect);
		for (int i = 0; i < numberOfObjectsToSelect; i++) {
			selectedObjects.add(objects[random.nextInt(objectsNumber)]);
		}
		return selectedObjects;
	}

	// TODO: Refactor this
	public static <E> E next(Class<E> elementClazz, ValueBound<E> elementValueBound) {
		E randomElementValue = null;
		if (Number.class.isAssignableFrom(elementClazz)) {
			// Number
			if (elementValueBound == null) {
				throw new BndRuntimeException("No element value bound defined, but expected for '" + elementClazz.getName() + "'.");
			}
			ValueBound<Number> numberElementValueBound = (ValueBound<Number>) elementValueBound;
			randomElementValue = (E) next((Class<Number>) elementClazz, numberElementValueBound.getFrom(), numberElementValueBound.getTo());
		} else if (Enum.class.isAssignableFrom(elementClazz)) {
			// Enum
			E[] selectionElements;
			if (elementValueBound != null && elementValueBound.hasEnumeratedValues()) {
				selectionElements = elementValueBound.getEnumeratedOrderedValues();
			} else {
				selectionElements = elementClazz.getEnumConstants();
			}
			randomElementValue = nextElement(selectionElements);
		} else if (elementClazz == Boolean.class) {
			// Boolean
			randomElementValue = (E) new Boolean(nextBoolean());
		} if (elementClazz == String.class) {
			// String
			if (elementValueBound == null) {
				throw new BndRuntimeException("No element value bound defined, but expected for '" + elementClazz.getName() + "'.");
			}
			randomElementValue = (E) nextReadableString(elementValueBound.getLength());
		} if (elementClazz == Date.class) {
			// Date
			if (elementValueBound == null) {
				throw new BndRuntimeException("No element value bound defined, but expected for '" + elementClazz.getName() + "'.");
			}
			ValueBound<Date> dateElementValueBound = (ValueBound<Date>) elementValueBound;
			randomElementValue = (E) nextDate(dateElementValueBound.getFrom(), dateElementValueBound.getTo());			
		}
		return randomElementValue;
	}

	public static <T> T nextElement(T[] objects) {
		return objects[RandomUtil.nextInt(objects.length)];
	}

    public static <T> T nextElement(Collection<T> objects) {
        T firstObject = ObjectUtil.getFirst(objects);
        T[] emptyArray = ReflectionUtil.createNewArray(firstObject, 0);
        return nextElement(objects.toArray(emptyArray));
    }

	public static <T extends Number> T perturbate(T value, Double perturbationStrength, T lowerLimit, T upperLimit) {
		Class<T> numClass = (Class<T>) value.getClass();
		double shift = RandomUtil.nextDouble(0, perturbationStrength);
		if (RandomUtil.nextBoolean()) {
			shift = -shift;
		}
		double newValue = value.doubleValue() * (1 + shift);
		if (lowerLimit != null && newValue < lowerLimit.doubleValue()) {
			return lowerLimit;
		}
		if (upperLimit != null && newValue > upperLimit.doubleValue()) {
			return upperLimit;
		}
		return probabilityConvert(newValue, numClass);
	}

	public static double nextNormal(double mean, double std) {
		return mean + random.nextGaussian() * std;
	}

	public static double nextLogNormal(double location, double shape) {
		return Math.exp(nextNormal(location, shape));
	}

	public static double nextUniform(double from, double to) {
		return nextDouble(from, to);
	}

	/**
	 * Generic random Gaussian generation for number types.
	 * 
	 * @param numberClazz
	 * @param mean
	 * @param std
	 * @return
	 */
	public static <T extends Number> T nextNormal(Class<T> numberClazz, Double mean, Double std) {
		double value = nextNormal(mean, std);
		return probabilityConvert(value, numberClazz);
	}

	/**
	 * Generic random log Gaussian generation for number types.
	 * 
	 * @param numberClazz
	 * @param from
	 * @param to
	 * @return
	 */
	public static <T extends Number> T nextLogNormal(Class<T> numberClazz, Double location, Double shape) {
		double value = nextLogNormal(location, shape);
		return probabilityConvert(value, numberClazz);
	}

	/**
	 * Nasty conversion of given value to the one of the Number instances
	 * 
	 * @param value
	 * @param clazz
	 * @return
	 */
	private static <TYPE extends Number> TYPE probabilityConvert(Number value, Class<TYPE> clazz) {
		if (value == null) {
			return null;
		}

		TYPE result = null;

		if (clazz == Long.class || clazz == Integer.class || clazz == Byte.class) {
			double doubleVal = value.doubleValue();
			double lowerVal  = Math.floor(doubleVal);
			double upperVal  = Math.ceil(doubleVal);

			if (nextDouble() > doubleVal - lowerVal) {
				result = ConversionUtil.convert(lowerVal, clazz);
			} else {
				result = ConversionUtil.convert(upperVal, clazz);
			}
		} else {
			result = ConversionUtil.convert(value, clazz);
		}

		return result;
	}
}