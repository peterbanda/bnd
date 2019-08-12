package com.bnd.network.business.integrator;

import java.util.Collection;

/**
 * @author © Peter Banda
 * @since 2012  
 */
@Deprecated
public interface JavaStatesWeightsIntegrator<T> {

	Collection<T> integrate(Collection<T> states, Collection<T> weights);

	int getOutputArity(int inputArity);
}