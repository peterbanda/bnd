package com.bnd.function.business;

import java.{lang => jl, util => ju}

import com.bnd.core.runnable.StateAccessible
import com.bnd.core.runnable.FullStateAccessible
import com.bnd.core.dynamics.StateAlternationType

import scala.collection.JavaConversions._

abstract class StateAlternator[T, C](
	protected val alternationType : StateAlternationType,
	protected val stateAccesible : FullStateAccessible[T, C],
	protected val stateAlternations : ju.List[T],
	protected val components : ju.List[C],
	protected val startTime : jl.Double,
	protected val applicableTimeLength : jl.Double) {

	def alterState : Unit = 
		alternationType match {
			case StateAlternationType.Replacement => (components, stateAlternations).zipped.foreach(stateAccesible.setState)
			case StateAlternationType.Addition => handleAddition
			case StateAlternationType.Influx => handleInflux
		}

	protected def handleAddition : Unit = addToStates(stateAlternations)

	protected def handleInflux : Unit

	protected def addToStates(stateDiffs : ju.List[T]) : Unit

	def isDone(currentTime : jl.Double) = currentTime > startTime && currentTime >= startTime + applicableTimeLength
}