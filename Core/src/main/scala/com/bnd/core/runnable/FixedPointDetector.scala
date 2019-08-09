package com.bnd.core.runnable

import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits._

trait FixedPointDetector[T] {
	def isFixedPoint(states : Iterable[T] , newStates : Iterable[T], timeStep : Double) : Boolean

	def waitTime : Double
}

class DistanceFixedPointDetector[T : DoubleConvertible](
		private val detectionPrecision : Double,
    	private val periodicWaitTime : Double
    ) extends FixedPointDetector[T] {

	override def isFixedPoint(states : Iterable[T] , newStates : Iterable[T], timeStep : Double) = 
			(newStates, states).zipped.forall((state, prevState) => math.abs(state - prevState) / timeStep < detectionPrecision)

	override def waitTime = periodicWaitTime
}

class StrictFixedPointDetector[T](private val periodicWaitTime : Double) extends FixedPointDetector[T] {

	override def isFixedPoint(states : Iterable[T] , newStates : Iterable[T], timeStep : Double) = (newStates, states).zipped.forall(_.equals(_))

	override def waitTime = periodicWaitTime
}