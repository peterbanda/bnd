package com.bnd.network.business;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public interface StateUpdatable {

	public void updateState();

	void updateStateInCache();

	void updateStateFromCache();

	void rollbackToPreviousState();
}