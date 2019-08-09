package com.bnd.function.business.ode

import java.{lang => jl}

import ODESolverFactory._
import com.bnd.function.evaluator.FunctionEvaluator
import com.bnd.function.domain.ODESolverType
import com.bnd.function.business.ScalaFunctionEvaluatorConversions._
import com.bnd.core.dynamics.ODESolver

object ODESolverFunFactory {

    private def odeSolverFun(odeSolver : ODESolver)(input : Array[jl.Double])(timeStep : Option[Double]) =
        if (timeStep.isDefined) 
        	odeSolver.getApproxDiffs(input, timeStep.get)
        else
            odeSolver.getApproxDiffs(input)

    def apply(
		odeSolverType : ODESolverType,
		initTimeStep : Double,
		tolerance : Option[Double])(
		diffEquations : Array[jl.Double] => Array[jl.Double]
	) : Array[jl.Double] => Option[Double] => Array[jl.Double] =
	    applyEvaluator(odeSolverType, initTimeStep, tolerance)(scalaArrayFunctionToFunctionEvaluator(diffEquations, 0))

    def applyEvaluator(
		odeSolverType : ODESolverType,
		initTimeStep : Double,
		tolerance : Option[Double])(
		diffEquationsEvaluator : FunctionEvaluator[jl.Double, Array[jl.Double]]
	) : Array[jl.Double] => Option[Double] => Array[jl.Double] = {
        val toleranceDouble = if (tolerance.isDefined) tolerance.get : jl.Double else null.asInstanceOf[jl.Double] 
        val odeSolver = createInstance(diffEquationsEvaluator, odeSolverType, initTimeStep, toleranceDouble)
        odeSolverFun(odeSolver)_
	}

	def applyInflux(
		odeSolverType : ODESolverType,
		initTimeStep : Double,
		tolerance : Option[Double])(
	    influxRate : Double)(
	    timeStep : Option[Double]
	) = ODESolverFunFactory(odeSolverType, initTimeStep, tolerance)(
	        {_ : Array[jl.Double] => Array(influxRate)}
	    )(Array[jl.Double]())(timeStep)(0)
}