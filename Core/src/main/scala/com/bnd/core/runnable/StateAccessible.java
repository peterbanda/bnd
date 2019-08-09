package com.bnd.core.runnable;

import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012
 */
public interface StateAccessible<T> {

	List<T> getStates();

	void setStates(List<T> states);
}