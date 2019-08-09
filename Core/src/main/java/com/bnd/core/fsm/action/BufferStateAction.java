package com.bnd.core.fsm.action;

import java.util.List;

public interface BufferStateAction<T> {

	public void run(List<T> buffer);
}