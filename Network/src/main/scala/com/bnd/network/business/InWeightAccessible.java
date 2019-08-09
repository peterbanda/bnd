package com.bnd.network.business;

import java.util.Iterator;

import com.bnd.network.domain.TopologicalNode;

public interface InWeightAccessible<T> {

	void setWeights(Iterator<T> weightIterator);

	void setMutableWeights(Iterator<T> weightIterator);

	void setImmutableWeights(Iterator<T> weightIterator);

	int getWeightsNum();

	void setWeight(TopologicalNode start, T value);

	T getWeight(TopologicalNode start);
}