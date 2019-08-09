package com.bnd.network.business.integrator

import java.{lang => jl}
import java.util.{Collection, HashMap}
import StatesWeightsIntegratorDef.StatesWeightsIntegrator
import com.bnd.core.NumericConversions
import com.bnd.network.domain.StatesWeightsIntegratorType
import scala.collection.JavaConversions.collectionAsScalaIterable

object MetaStatesWeightsIntegratorFactory {

	def createBooleanInstance : StatesWeightsIntegratorFactory[Boolean] = {
		val map = new HashMap[StatesWeightsIntegratorType, StatesWeightsIntegrator[Boolean]]
		map.put(StatesWeightsIntegratorType.LinearSum, sumIntegrator[Boolean](_&&_,_||_))
		map.put(StatesWeightsIntegratorType.QuadSum, sumIntegrator[Boolean](_&&_,_||_))
		new StatesWeightsIntegratorFactoryImpl[Boolean](map)
	}

	def createJavaDoubleInstance : StatesWeightsIntegratorFactory[jl.Double] = {
		val map = new HashMap[StatesWeightsIntegratorType, StatesWeightsIntegrator[jl.Double]]
		map.put(StatesWeightsIntegratorType.LinearSum, sumIntegrator[jl.Double](
			{(a : jl.Double, b : jl.Double) => if (a == null || b == null)
        0d
        else
        a * b : jl.Double},
			{(a : jl.Double, b : jl.Double) => a + b : jl.Double}))

		map.put(StatesWeightsIntegratorType.QuadSum, sumIntegrator[jl.Double](
			quad({(a : jl.Double, b : jl.Double) => a * b : jl.Double}),
		{(a : jl.Double, b : jl.Double) => a + b : jl.Double}))

		new StatesWeightsIntegratorFactoryImpl[jl.Double](map)
	}

	def createAnyValInstance[T <: AnyVal](clazz : Class[T]) : StatesWeightsIntegratorFactory[T] = {
		implicit val numeric = NumericConversions.liftAnyValToNumericFromClass[T](clazz)
		createNumericInstance
	}

	def createNumericInstance[T](implicit num : Numeric[T]) : StatesWeightsIntegratorFactory[T] = {
		val map = new HashMap[StatesWeightsIntegratorType, StatesWeightsIntegrator[T]]
		map.put(StatesWeightsIntegratorType.LinearSum, sumIntegrator[T](num.times,num.plus))
		map.put(StatesWeightsIntegratorType.QuadSum, sumIntegrator[T](quad(num.times),num.plus))
		new StatesWeightsIntegratorFactoryImpl[T](map)
	}

	private def quad[T](times: (T, T) => T)(a : T, b : T) = {val c = times(a,b); times(c,c)}

	private def sumIntegrator[T](
		times: (T, T) => T,
		plus : (T, T) => T)(
		states: Collection[T],
		weights: Collection[T]
	) = (states, weights).zipped.map(times).reduceLeft(plus)
}