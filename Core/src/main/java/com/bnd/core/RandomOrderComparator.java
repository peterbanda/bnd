package com.bnd.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RandomOrderComparator<T> extends FixedOrderComparator<T> {

	public RandomOrderComparator(Collection<T> objects) {
		super(randomizeObjectOrder(objects));
	}

	private static <T> Collection<T> randomizeObjectOrder(Collection<T> objects) {
		List<T> objectsInRandomOrder = new ArrayList<T>(objects);
		Collections.shuffle(objectsInRandomOrder);
		return objectsInRandomOrder;
	}
}

