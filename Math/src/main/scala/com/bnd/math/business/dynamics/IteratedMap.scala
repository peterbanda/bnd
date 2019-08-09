package com.bnd.math.business.dynamics

import scala.collection.JavaConversions._
import scala.math._
import com.bnd.core.runnable.StateAccessible
import java.{lang => jl}
import java.{util => ju}

import com.bnd.core.runnable.FullStateAccessible
import com.bnd.core.runnable.TimeRunnable

class IteratedMap[T](val partialFunctions : Seq[Seq[T] => T]) extends TimeRunnable with FullStateAccessible[T, Int] {

    private var states : Seq[T] = Seq.empty[T]
    private var time = 0

    private def runOneStep = {
    	states = partialFunctions map (f => f(states))
    	time += 1
    }

    override def runFor(timeDiff : BigDecimal) = runUntil(timeDiff + time)

    override def runUntil(finalTime : BigDecimal) = {
	    while (time < finalTime.intValue) runOneStep
	}

    override def nextTimeStepSize = 1 

	override def getStates : ju.List[T] = states

	override def setStates(states : ju.List[T]) : Unit = this.states = states

	override def setState(component : Int, state : T) : Unit = states.set(component, state)

	override def getState(component : Int) = states.get(component)

	override def componentStates = states.zipWithIndex.map(_.swap)

	override def currentTime = time
}

object IteratedMap {
    def createLogisticMap(a : Double) = new IteratedMap[Double](
		Seq[Seq[Double] => Double](
			states => a * states(0) * (1 - states(0))
		)
	)

    def createTrigonometric2DMap(a : Double, b : Double) = new IteratedMap[Double](
		Seq[Seq[Double] => Double](
			states => cos(states(0) + a * states(1)),
			states => sin(b * states(0) + states(1))
		)
	)

	def createLorenzSystem(a : Double, b : Double, c : Double, timeDiff : Double) = new IteratedMap[Double](
		Seq[Seq[Double] => Double](
			states => states(0) + timeDiff * (b * (states(1) - states(0))),
			states => states(1) + timeDiff * (states(0) * (a - states(2)) - states(1)),
			states => states(2) + timeDiff * (states(0) * states(1) - c * states(2))
		)
	)
}