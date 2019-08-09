package com.bnd.function.business;

import java.{lang => jl, util => ju}

import com.bnd.core.runnable.TimeRunnable

import scala.collection.mutable.ListBuffer
import com.bnd.core.runnable.StateAccessible
import com.bnd.function.domain.ODESolverType
import com.bnd.core.runnable.FullStateAccessible
import com.bnd.core.dynamics.StateAlternationType

abstract class StateAlternatorContainer[T, C](
    private val timeRunnable : TimeRunnable with FullStateAccessible[T, C],
    private val influxODESolverType : ODESolverType,
	private val influxTimeStepLength : jl.Double,
	private val influxScale : jl.Double) {

	protected var stateAlternators = ListBuffer[StateAlternator[T, C]]()

	def addStateAlternator(
		alterType : StateAlternationType,
		stateAlternations : ju.List[T],
		components : ju.List[C],
		applicableTimeLength : jl.Double 
	)

	def alterState : jl.Boolean = {
	    stateAlternators = stateAlternators.filterNot(_.isDone(timeRunnable.currentTime.doubleValue))
	    stateAlternators.foreach(_.alterState)
	    !stateAlternators.isEmpty
	}
}

class NumberStateAlternatorContainer[T <: Number, C](
    private val timeRunnable : TimeRunnable with FullStateAccessible[T, C],
    private val influxODESolverType : ODESolverType,
	private val influxTimeStepLength : jl.Double,
	private val influxScale : jl.Double)(implicit m: Manifest[T]) extends StateAlternatorContainer[T, C](timeRunnable, influxODESolverType, influxTimeStepLength, influxScale) {

	override def addStateAlternator(
		alterType : StateAlternationType,
		stateAlternations : ju.List[T],
		components : ju.List[C],
		applicableTimeLength : jl.Double 
	) {
		val stateAlternator = new NumberStateAlternator[T, C](
			alterType, timeRunnable, stateAlternations, components, timeRunnable.currentTime.doubleValue, applicableTimeLength,
			influxODESolverType, influxTimeStepLength, influxScale)
		stateAlternators += stateAlternator
	}
}

class DoubleStateAlternatorContainer[C](
    private val timeRunnable : TimeRunnable with FullStateAccessible[jl.Double, C],
    private val influxODESolverType : ODESolverType,
	private val influxTimeStepLength : jl.Double,
	private val influxScale : jl.Double) extends NumberStateAlternatorContainer[jl.Double, C](timeRunnable, influxODESolverType, influxTimeStepLength, influxScale)