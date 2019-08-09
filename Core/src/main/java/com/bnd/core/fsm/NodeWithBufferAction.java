package com.bnd.core.fsm;

import com.bnd.core.fsm.action.BufferStateAction;

class NodeWithBufferAction<S, T> extends Node<S, T> {

	private BufferStateAction<T> enterStateAction;

	protected NodeWithBufferAction(S state) {
		super(state);
	}

	protected void setEnterStateAction(BufferStateAction<T> enterStateAction) {
		this.enterStateAction = enterStateAction;
	}

	protected BufferStateAction<T> getEnterStateAction() {
		return enterStateAction;
	}
}