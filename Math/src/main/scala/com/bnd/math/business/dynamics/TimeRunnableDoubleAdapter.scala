package com.bnd.math.business.dynamics

import java.{lang => jl, util => ju}

import scala.collection.JavaConversions._
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.Implicits._
import com.bnd.core.runnable.StateAccessible
import com.bnd.core.runnable.TimeRunnable

private class TimeRunnableDoubleAdapter[T : DoubleConvertible] (
    val timeRunnable : TimeRunnable with StateAccessible[T]) extends TimeRunnable with StateAccessible[Double] {
    
    val converter = implicitly[DoubleConvertible[T]]

    implicit def fromDouble(a : Double) = converter.fromDouble(a) 
    
    override def runFor(timeDiff : BigDecimal) = timeRunnable.runFor(timeDiff)

    override def runUntil(finalTime : BigDecimal) = timeRunnable.runUntil(finalTime)

    override def nextTimeStepSize = timeRunnable.nextTimeStepSize

	override def getStates = timeRunnable.getStates.map(a => a : Double)

	override def setStates(states : ju.List[Double]) = timeRunnable.setStates(states.map(a => a : T))

	override def currentTime = timeRunnable.currentTime
}

object TimeRunnableDoubleAdapter {
    def apply[T : DoubleConvertible](
         timeRunnable : TimeRunnable with StateAccessible[T]
    ) : TimeRunnable with StateAccessible[Double] = new TimeRunnableDoubleAdapter(timeRunnable)
}