package com.bnd.core.fsm.action;

import java.util.List;

public class AddToBufferTransitionAction<T> implements BufferTransitionAction<T> {

	@Override
	public void run(List<T> buffer, T transitionLabel) {
		buffer.add(transitionLabel);
	}
}