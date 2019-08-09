package com.bnd.core.runnable

import java.{lang => jl}

import com.bnd.function.business.ConstantFunctionEvaluator
import com.bnd.function.business.ode.ODESolverFunFactory
import com.bnd.function.domain.ODESolverType

import scala.collection.mutable.{Map => MMap}
import scala.collection.Map
import com.bnd.core.DoubleConvertible.Implicits.addAsDouble
import com.bnd.core.dynamics.ODESolver

object StateAlternationRepeatFirstInflux {

    def apply[C, H](
    	odeSolverType : ODESolverType,
		initTimeStep : Double,
		tolerance : Option[Double])(
    	applyStartTime : BigDecimal,
    	timeLength : BigDecimal,
    	items : Iterable[StateAlternationItem[jl.Double, C, H]],
    	cacheWrites : Iterable[StateAlternationWrite[jl.Double, C, H]]
    ) = {
        val newInflux = ODESolverFunFactory.applyInflux(odeSolverType, initTimeStep, tolerance)_
        var influces = MMap[C, (Option[Double] => jl.Double)]()
        new StateAlternation[jl.Double, C, H](
            applyStartTime, timeLength,
	        items.map{
	            item => new StateAlternationItem(item.component,
	            		{ (environment : Map[C, jl.Double], cache : Map[H, jl.Double], timeStep : Option[Double]) => {
	            			val influx = influces.getOrElseUpdate(item.component, newInflux(item.fun(environment, cache, timeStep)))
	            			addAsDouble(environment.get(item.component).get, influx(timeStep))
	            		}})
            },
            cacheWrites)
    }
}