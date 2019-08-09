package com.bnd.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import com.bnd.core.reflection.ReflectionUtil;

/**
 * <code>ConversionUtil</code> is a utility class securely converting String value to 
 * the appropriate Integer, Double or Byte representation.
 * 
 * @author © Peter Banda
 * @since 2012
 */
public class ConversionUtil {

	private static final String DELIMETER = ",";

	public static <T> T convert(Class<T> clazz, String value) {
		return convert(clazz, value, "");
	}

	public static <T> T convert(Class<T> clazz, String value, String variableName) {
		T result;
		if (clazz == null) {
			throw new BndConversionException("Class type of variable " + variableName + " not specified.");
		}
		if (clazz == String.class) {
			result = (T) value;
		} else if (clazz == Integer.class) {
			result = (T) convertToInteger(value, variableName);
		} else if (clazz == Byte.class) {
			result = (T) convertToByte(value, variableName);
		} else if (clazz == Double.class) {
			result = (T) convertToDouble(value, variableName);
		} else if (clazz == Float.class) {
			result = (T) convertToFloat(value, variableName);
		} else if (clazz == Long.class) {
			result = (T) convertToLong(value, variableName);
		} else if (clazz == Boolean.class) {
			result = (T) convertToBoolean(value, variableName);
		} else if (clazz == List.class || clazz == Collection.class) {
			result = (T) convertToList(Long.class, value, variableName);
		} else {
			throw new BndConversionException(clazz.toString() + " not supported as a target conversionl type.");
		}
		return result;
	}

	public static <T> List<T> convertToList(Class<T> elementClazz, String value, String variableName) {
		List<T> result = new ArrayList<T>();
		StringTokenizer st = new StringTokenizer(value, DELIMETER);
		while (st.hasMoreElements()) {
			result.add(convert(elementClazz, st.nextToken().trim(), variableName));
		}
		return result;
	}

	public static <T> T[] convertArray(Class<T> elementClazz, String[] values, String variableName) {
		T[] convertedArray = ReflectionUtil.createNewArray(elementClazz, values.length);
		for (int i = 0; i < values.length; i++) {
			convertedArray[i] = convert(elementClazz, values[i].trim(), variableName);
		}
		return convertedArray;
	}

	public static <T> Collection<T> convertCollection(Class<T> elementClazz, Collection<String> values, String variableName) {
		Collection<T> convertedCollection = new ArrayList<T>();
		for (String value : values) {
			convertedCollection.add(convert(elementClazz, value.trim(), variableName));
		}
		return convertedCollection;
	}

	public static Byte convertToByte(String s, String variableName) {
		Byte result;
		try {
			result = Byte.parseByte(s);
		} catch (NumberFormatException e) {
			throw new BndConversionException("Expression '" + s + "' is not valid " + variableName + ". Byte expected.", e);
		}
		return result;
	}

	public static Integer convertToInteger(String s, String variableName) {
		Integer result;
		try {
			result = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new BndConversionException("Expression '" + s + "' is not valid " + variableName + ". Integer expected.", e);
		}
		return result;
	}

	public static Double convertToDouble(String s, String variableName) {
		Double result;
		try {
			result = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw new BndConversionException("Expression '" + s + "' is not valid " + variableName + ". Double expected.", e);
		}
		return result;
	}

	public static Float convertToFloat(String s, String variableName) {
		Float result;
		try {
			result = Float.parseFloat(s);
		} catch (NumberFormatException e) {
			throw new BndConversionException("Expression '" + s + "' is not valid " + variableName + ". Float expected.", e);
		}
		return result;
	}

	public static Long convertToLong(String s, String variableName) {
		Long result;
		try {
			result = Long.parseLong(s);
		} catch (NumberFormatException e) {
			throw new BndConversionException("Expression '" + s + "' is not valid " + variableName + ". Long expected.", e);
		}
		return result;
	}

	public static Boolean convertToBoolean(String s, String variableName) {
		Boolean result;
		try {
			result = Boolean.parseBoolean(s);
		} catch (NumberFormatException e) {
			throw new BndConversionException("Expression '" + s + "' is not valid " + variableName + ". Boolean expected.", e);
		}
		return result;
	}

	public static Double[] convertToDouble(Boolean[] booleanArray) {
		Double[] doubleArray = new Double[booleanArray.length];
		for (int i = 0; i < booleanArray.length; i++) {
			doubleArray[i] = booleanArray[i] ? new Double(1) : new Double(0);
		}
		return doubleArray;
 	}

	public static List<Double[]> convertToDouble(Collection<Boolean[]> booleanArrayCollection) {
		List<Double[]> doubleArrayCollection = new ArrayList<Double[]>();
		for (Boolean[] booleanArray : booleanArrayCollection) {
			doubleArrayCollection.add(convertToDouble(booleanArray));
		}
		return doubleArrayCollection;
 	}

	public static Byte[] convertToByte(Boolean[] booleanArray) {
		Byte[] byteArray = new Byte[booleanArray.length];
		for (int i = 0; i < booleanArray.length; i++) {
			byteArray[i] = booleanArray[i] ? new Byte((byte) 1) : new Byte((byte) 0);
		}
		return byteArray;
 	}

	public static Collection<Byte[]> convertToByte(Collection<Boolean[]> booleanArrayCollection) {
		Collection<Byte[]> byteArrayCollection = new ArrayList<Byte[]>();
		for (Boolean[] booleanArray : booleanArrayCollection) {
			byteArrayCollection.add(convertToByte(booleanArray));
		}
		return byteArrayCollection;
 	}

	public static <TYPE extends Number> List<TYPE> convert(List<? extends Number> values, Class<TYPE> inTypeClass) {
		List<TYPE> results = new ArrayList<TYPE>();
		for (Number num : values) {
			results.add(convert(num, inTypeClass));
		}
		return results;
	}

	/**
	 * Nasty conversion of given value to the one of the Number instances
	 * 
	 * @param value
	 * @param inTypeClass
	 * @return
	 */
	public static <TYPE extends Number> TYPE convert(Number value, Class<TYPE> inTypeClass) {
		if (value == null) {
			return null;
		}
		TYPE result = null;
		if (inTypeClass == Long.class) {
			result = (TYPE) Long.valueOf(value.longValue());
		} else if (inTypeClass == Integer.class) {
			result = (TYPE) Integer.valueOf(value.intValue());
		} else if (inTypeClass == Double.class) {
			result = (TYPE) Double.valueOf(value.doubleValue());
		} else if (inTypeClass == Float.class) {
			result = (TYPE) Float.valueOf(value.floatValue());
		} else if (inTypeClass == Byte.class) {
			result = (TYPE) Byte.valueOf(value.byteValue());
		}
		return result;
	}

	/**
	 * Nasty conversion of given value to the one of the Number instances
	 * 
	 * @param value
	 * @param inTypeClass
	 * @return
	 */
	public static <TYPE extends Number> TYPE convertSimpleInt(int value, Class<TYPE> inTypeClass) {
		TYPE result = null;
		if (inTypeClass == Long.class) {
			result = (TYPE) Long.valueOf(value);
		} else if (inTypeClass == Integer.class) {
			result = (TYPE) Integer.valueOf(value);
		} else if (inTypeClass == Double.class) {
			result = (TYPE) Double.valueOf(value);
		} else if (inTypeClass == Float.class) {
			result = (TYPE) Float.valueOf(value);
		} else if (inTypeClass == Byte.class) {
			result = (TYPE) Byte.valueOf((byte) value);
		}
		return result;
	}

	/**
	 * Nasty conversion from double to the instance of one of the underlying instances
	 * 
	 * @param value
	 * @param inTypeClass
	 * @return
	 */
	public static <TYPE extends Number> TYPE convertAndRoundIfNeeded(double value, Class<TYPE> inTypeClass) {
		TYPE result = null;
		if (inTypeClass == Long.class) {
			result = (TYPE) Long.valueOf((long) Math.round(value));
		} else if (inTypeClass == Integer.class) {
			result = (TYPE) Integer.valueOf((int) Math.round(value));
		} else if (inTypeClass == Double.class) {
			result = (TYPE) Double.valueOf(value);
		} else if (inTypeClass == Float.class) {
			result = (TYPE) Float.valueOf((float) value);
		} else if (inTypeClass == Byte.class) {
			result = (TYPE) Byte.valueOf((byte) Math.round(value));
		}
		return result;
	}

	public static List<Boolean> convertDecimalToBooleanList(int decimal, int size) {
		List<Boolean> booleanList = new ArrayList<Boolean>();
		String binaryString = Integer.toBinaryString(decimal);
		int diff = size - binaryString.length();
		if (binaryString.length() < size) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < diff; i++) {
				sb.append('0');
			}
			sb.append(binaryString);
			binaryString = sb.toString();
		}
		char[] cArray = binaryString.toCharArray();
		for (char c : cArray) {
			booleanList.add(new Boolean(c == '1'));
		}
		return booleanList;
	}

	public static int[] toSimpleType(final Integer[] array) {
		final int arraySize = array.length;
		int[] ints = new int[arraySize];
		for (int i = 0; i < arraySize; i++) {
			ints[i] = array[i];
		}
		return ints;
	}

	public static double[] toSimpleType(final Double[] array) {
		final int arraySize = array.length;
		double[] doubles = new double[arraySize];
		for (int i = 0; i < arraySize; i++) {
			doubles[i] = array[i];
		}
		return doubles;
	}

	public static Double[] toComplexType(final double[] array) {
		final int arraySize = array.length;
		Double[] doubles = new Double[arraySize];
		for (int i = 0; i < arraySize; i++) {
			doubles[i] = array[i];
		}
		return doubles;
	}

	public static double[][] toSimpleType(final Double[][] matrix) {
		final int matrixSide = matrix.length;
		double[][] doubles = new double[matrixSide][matrixSide];
		for (int i = 0; i < matrixSide; i++) {
			for (int j = 0; j < matrixSide; j++) {
				doubles[i][j] = matrix[i][j];
			}
		}
		return doubles;
	}

	public static String[] toStrings(final Object[] array) {
		final int arraySize = array.length;
		String[] strings = new String[arraySize];
		for (int i = 0; i < arraySize; i++) {
			strings[i] = array[i].toString();
		}
		return strings;
	}
}