package com.bnd.network.business;

import java.util.Collection;

import com.bnd.core.domain.MultiStateUpdateType;
import com.bnd.network.BndNetworkException;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public abstract class MultiStateUpdater implements StateUpdatable {

	final private Collection<? extends StateUpdatable> singleStateUpdatables;

	public MultiStateUpdater(Collection<? extends StateUpdatable> singleStateUpdatables) {
		this.singleStateUpdatables = singleStateUpdatables;
	}

	protected Collection<? extends StateUpdatable> getSingleStateUpdatables() {
		return singleStateUpdatables;
	}

	@Override
	public void rollbackToPreviousState() {
		for (StateUpdatable stateHolder : getSingleStateUpdatables()) {
			stateHolder.rollbackToPreviousState();
		}				
	}

	public static <T> MultiStateUpdater createInstance(
		MultiStateUpdateType nodeStateUpdaterType,
		Collection<? extends StateUpdatable> singleStateUpdatables
	) {
		switch (nodeStateUpdaterType) {
			case Sync:
				return new SynchStateUpdater(singleStateUpdatables);
			case AsyncFixedOrder:
				return new AsynchStateUpdater(singleStateUpdatables, false);
			case AsyncRandom:
				return new AsynchStateUpdater(singleStateUpdatables, true);
			default:
				throw new BndNetworkException("The node state type '" + nodeStateUpdaterType + "' not recognized.");
		}
	}
}