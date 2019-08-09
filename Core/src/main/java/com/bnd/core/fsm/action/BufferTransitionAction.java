package com.bnd.core.fsm.action;

import java.util.List;

public interface BufferTransitionAction<T> {

	public void run(List<T> buffer, T transitionLabel);
}
