package com.bnd.core.fsm.action;

public interface TransitionAction<T> {

	public void run(T transitionLabel);
}
