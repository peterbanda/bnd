package com.bnd.network.business;

import com.bnd.network.domain.Topology;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public interface TopologyFactory {

	public Topology apply(Topology originalTopology);
}