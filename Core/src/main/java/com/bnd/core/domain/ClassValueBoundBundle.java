package com.bnd.core.domain;

import java.util.*;

public class ClassValueBoundBundle {

	private Map<Class<?>, ValueBound<?>> classBoundMap = new HashMap<Class<?>, ValueBound<?>>();

	public <T> void register(Class<T> clazz, ValueBound<T> bound) {
		classBoundMap.put(clazz, bound);
	}

	public <T> void register(Class<T> clazz) {
		classBoundMap.put(clazz, null);
	}

	private Class<?> getBestRegisterdMatchingClass(Class<?> clazz) {
		Class<?> bestMatchingClazz = clazz;
		while (bestMatchingClazz != null && !classBoundMap.containsKey(bestMatchingClazz)) {
			bestMatchingClazz = bestMatchingClazz.getSuperclass();
		}
		return bestMatchingClazz;
	}

	public boolean contains(Class<?> clazz) {
		final Class<?> matchingClazz = getBestRegisterdMatchingClass(clazz);
		return matchingClazz != null;
	}

	public <T> ValueBound<T> getBound(Class<T> clazz) {
		final Class<?> matchingClazz = getBestRegisterdMatchingClass(clazz);
		return (ValueBound<T>) classBoundMap.get(matchingClazz);
	}

	public static ClassValueBoundBundle getDefaultInstance() {
		ClassValueBoundBundle defaultClassValueBoundBundle = new ClassValueBoundBundle();
		defaultClassValueBoundBundle.register(Integer.class,
				new ValueBound<Integer>(0, 10));
		defaultClassValueBoundBundle.register(Byte.class,
				new ValueBound<Byte>((byte) 0, (byte) 5));
		defaultClassValueBoundBundle.register(Long.class,
				new ValueBound<Long>(0l, 10000l));
		defaultClassValueBoundBundle.register(Float.class,
				new ValueBound<Float>(0f, 2f));
		defaultClassValueBoundBundle.register(Double.class,
				new ValueBound<Double>(0d, 4d));
		defaultClassValueBoundBundle.register(Boolean.class);
		defaultClassValueBoundBundle.register(String.class,
				new ValueBound<String>(5));
		defaultClassValueBoundBundle.register(Enum.class);
		Date now = new Date();
		Calendar tomorrowCal = GregorianCalendar.getInstance();
		tomorrowCal.setTime(now);
		tomorrowCal.add(Calendar.DAY_OF_MONTH, 1);
		defaultClassValueBoundBundle.register(Date.class,
				new ValueBound<Date>(now, tomorrowCal.getTime()));
		return defaultClassValueBoundBundle;
	}
}