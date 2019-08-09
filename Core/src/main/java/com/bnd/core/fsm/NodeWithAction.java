package com.bnd.core.fsm;

import com.bnd.core.fsm.action.StateAction;

class NodeWithAction<S, T> extends Node<S, T> {

	private StateAction enterStateAction;

	protected NodeWithAction(S state) {
		super(state);
	}

	protected void setEnterStateAction(StateAction enterStateAction) {
		this.enterStateAction = enterStateAction;
	}

	protected StateAction getEnterStateAction() {
		return enterStateAction;
	}
}