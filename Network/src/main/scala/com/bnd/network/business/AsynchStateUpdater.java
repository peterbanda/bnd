package com.bnd.network.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class AsynchStateUpdater extends MultiStateUpdater {

	private final boolean randomOrder;

	public AsynchStateUpdater(Collection<? extends StateUpdatable> stateHolders, boolean randomOrder) {
		super(stateHolders);
		this.randomOrder = randomOrder;
	}

	protected Collection<? extends StateUpdatable> getSingleStateUpdatablesInOrder() {
		if (!randomOrder) {
			return getSingleStateUpdatables();
		}
		List<StateUpdatable> stateUpdatablesInRandomOrder = new ArrayList<StateUpdatable>();
		stateUpdatablesInRandomOrder.addAll(getSingleStateUpdatables());
		Collections.shuffle(stateUpdatablesInRandomOrder);
		return stateUpdatablesInRandomOrder;
	}

	@Override
	public void updateState() {
		for (StateUpdatable stateUpdatable : getSingleStateUpdatablesInOrder()) {
			stateUpdatable.updateState();
		}
	}

	@Override
	public void updateStateInCache() {
		for (StateUpdatable stateUpdatable : getSingleStateUpdatablesInOrder()) {
			stateUpdatable.updateStateInCache();
			stateUpdatable.updateStateFromCache();
		}
		// roll-back to previous state hence emulating nothing has changed
		rollbackToPreviousState();
	}

	@Override
	public void updateStateFromCache() {
		for (StateUpdatable stateUpdatable : getSingleStateUpdatablesInOrder()) {
			stateUpdatable.updateStateFromCache();
		}
	}
}