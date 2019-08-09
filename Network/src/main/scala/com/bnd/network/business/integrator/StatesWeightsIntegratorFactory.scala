package com.bnd.network.business.integrator

import java.{util => ju}
import StatesWeightsIntegratorDef.StatesWeightsIntegrator
import com.bnd.network.domain.StatesWeightsIntegratorType

/**
 * @author © Peter Banda
 * @since 2015
 */
trait StatesWeightsIntegratorFactory[T] {

	def apply(integratorType : StatesWeightsIntegratorType) : StatesWeightsIntegrator[T]
}

object StatesWeightsIntegratorDef {
	type StatesWeightsIntegrator[T] = (ju.List[T], ju.List[T]) => T
}