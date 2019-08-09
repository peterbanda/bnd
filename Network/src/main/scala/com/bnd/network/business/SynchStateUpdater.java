package com.bnd.network.business;

import java.util.Collection;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class SynchStateUpdater extends MultiStateUpdater {

	public SynchStateUpdater(Collection<? extends StateUpdatable> stateHolders) {
		super(stateHolders);
	}

	@Override
	public void updateState() {
		updateStateInCache();
		updateStateFromCache();
	}

	@Override
	public void updateStateInCache() {
		for (StateUpdatable stateHolder : getSingleStateUpdatables()) {
			stateHolder.updateStateInCache();
		}				
	}

	@Override
	public void updateStateFromCache() {
		for (StateUpdatable stateHolder : getSingleStateUpdatables()) {
			stateHolder.updateStateFromCache();
		}
	}
}