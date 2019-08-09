package com.bnd.network.business.integrator

import java.util.Map

import com.bnd.network.BndNetworkException
import com.bnd.network.domain.StatesWeightsIntegratorType
import StatesWeightsIntegratorDef.StatesWeightsIntegrator

/**
 * @author © Peter Banda
 * @since 2012  
 */
private[integrator] class StatesWeightsIntegratorFactoryImpl[T](
	statesWeightsIntegratorMap : Map[StatesWeightsIntegratorType, StatesWeightsIntegrator[T]]
) extends StatesWeightsIntegratorFactory[T] {

	override def apply(integratorType : StatesWeightsIntegratorType) : StatesWeightsIntegrator[T] = {
		val statesWeightsIntegrator = statesWeightsIntegratorMap.get(integratorType)
		if (statesWeightsIntegrator == null)
			throw new BndNetworkException("States-weights integrator of type '" + statesWeightsIntegrator + "' not recognized.")

		statesWeightsIntegrator
	}
}