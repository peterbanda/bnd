package com.bnd.core.runnable;

import com.bnd.core.domain.ComponentRunTrace;

public interface RunTraceHolder<T, C> {

	ComponentRunTrace<T, C> getRunTrace();
}