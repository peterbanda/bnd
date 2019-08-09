package com.bnd.core.dynamics;

import java.util.List;

public interface SingleRunIncrementalProcessor<T> {

	void process(List<T> currentStates);
}