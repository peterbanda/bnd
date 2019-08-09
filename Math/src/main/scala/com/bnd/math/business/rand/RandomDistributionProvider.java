package com.bnd.math.business.rand;

import java.util.List;

public interface RandomDistributionProvider<T> {

	T next();

	List<T> nextList(int size);

	Double mean();

	Double variance();

	Class<T> getValueType();
}