package com.bnd.core.dynamics;

import java.util.List;

public interface SingleRunFinalProcessor<T> {

	void process(List<List<T>> allStates);
}