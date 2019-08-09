package com.bnd.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class ParseUtil {

	public static <T> T parse(String s, Class<T> clazz) {
		T result = null;
		if (clazz == Long.class) {
			result = (T) new Long(Long.parseLong(s));
		} else if (clazz == Integer.class) {
			result = (T) new Integer(Integer.parseInt(s));
		} else if (clazz == Double.class) {
			result = (T) new Double(Double.parseDouble(s));
		} else if (clazz == Float.class) {
			result = (T) new Float(Float.parseFloat(s));
		} else if (clazz == Byte.class) {
			result = (T) new Byte(Byte.parseByte(s));
		} else if (clazz == Boolean.class) {
			result = (T) new Boolean(Boolean.parseBoolean(s));
		}
		return result;
	}

	public static <T> T parse(String s, Class<T> clazz, String variableName) {
		T result;
		if (s == null) {
			throw new BndParseException(variableName + " not specified!");
		}
		try {
			result = parse(s, clazz);
		} catch (NumberFormatException e) {
			throw new BndParseException("Expression '" + s + "' is not valid " + variableName + ". " + clazz.getName() + " expected.", e);
		}
		return result;
	}

	public static int parseInt(String s, String variableName) {
		int result;
		if (s == null) {
			throw new BndParseException(variableName + " not specified!");
		}
		try {
			result = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new BndParseException("Expression '" + s + "' is not valid " + variableName + ". Integer expected.", e);
		}
		return result;
	}

	public static double parseDouble(String s, String variableName) {
		double result;
		if (s == null) {
			throw new BndParseException(variableName + " not specified!");
		}
		try {
			result = Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw new BndParseException("Expression '" + s + "' is not valid " + variableName + ". Double expected.", e);
		}
		return result;
	}

	public static <T> Collection<T> parseArray(String s, Class<T> clazz, String variableName, String delimeter) {
		Collection<T> results = new ArrayList<T>();
		if (s == null) {
			throw new BndParseException(variableName + " not specified!");
		}
		StringTokenizer st = new StringTokenizer(s, delimeter);
		while (st.hasMoreTokens()) {
			results.add(parse(st.nextToken().trim(), clazz, variableName));
		}
		return results;
	}

	public static Integer[] parseIntegerArray(String s, String variableName, String delimeter) {
		List<Integer> result = new ArrayList<Integer>();
		if (s == null) {
			throw new BndParseException(variableName + " not specified!");
		}
		try {
			StringTokenizer st = new StringTokenizer(s, delimeter);
			while (st.hasMoreElements()) {
				result.add(Integer.parseInt((String) st.nextElement()));
			}
		} catch (NumberFormatException e) {
			throw new BndParseException(s + " is not valid element of " + variableName + ". Integer expected.", e);
		}
		return result.toArray(new Integer[0]);
	}

	/**
	 * Retrieves a list of Strings from given comma-separated String list.
	 * 
	 * @param stringList The comma-separated String to handle
	 * @return The list of String extracted from given comma-separated String.
	 */
	public static List<String> parseCommaList(String stringList) {
		List<String> lvListOfStrings = new ArrayList<String>();
		if (stringList != null) {
			StringTokenizer lStringTokenizer = new StringTokenizer(stringList, ",");
			while (lStringTokenizer.hasMoreElements()) {
				String lNextElement = (String) lStringTokenizer.nextElement();
				if (lNextElement != null && !(lNextElement.length() == 0)) {
					lvListOfStrings.add(lNextElement.trim());
				}
			}
		}
		return lvListOfStrings;
	}
}