package com.bnd.network.domain;

import java.util.ArrayList;
import java.util.Collection;

import com.bnd.math.domain.learning.TrainingSet;

/**
 * @author © Peter Banda
 * @since 2012
 */
@Deprecated
public class NetworkTrainingSet<T> extends TrainingSet<T> {

	private Collection<Network<T>> networks = new ArrayList<Network<T>>();

	public Collection<Network<T>> getNetworks() {
		return networks;
	}

	public void setNetworks(Collection<Network<T>> networks) {
		this.networks = networks;
	}

	public void addNetwork(Network<T> network) {
		networks.add(network);
	}

	public void removeNetwork(Network<T> network) {
		networks.remove(network);
	}
}