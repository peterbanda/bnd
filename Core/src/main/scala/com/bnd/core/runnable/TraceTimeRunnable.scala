package com.bnd.core.runnable

import java.{lang => jl, util => ju}

import com.bnd.core.domain.{ComponentHistory, ComponentRunTrace}
import com.bnd.core.runnable.SeqIndexAccessible.Implicits._

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.Publisher

/**
	* @author © Peter Banda
	* @since 2013
	*/
private class TraceTimeRunnable[T : Manifest, C, S[X] : SeqIndexAccessible](
		timeRunnable : TimeRunnable with mutable.Publisher[StateEvent[T, S]]
	) extends DummyTraceTimeRunnable[T, C](timeRunnable) {

    val updatedStateCollector = new ComponentStateCollector[T, C, S]
    val alteredStateCollector = new AlteredStateCollector[T, C, S]

    timeRunnable.subscribe(updatedStateCollector)
//    timeRunnable.subscribe(alteredStateCollector)

    override def getRunTrace = {
        // run for 0 time to get final state
    	runFor(0 : BigDecimal)
    	val runTrace = new ComponentRunTrace[T, C]
    	runTrace.runTime(currentTime.doubleValue)
    	if (!updatedStateCollector.collected.isEmpty) {
    		runTrace.timeSteps(updatedStateCollector.collected.map(_._1.doubleValue : jl.Double))
    		val components = updatedStateCollector.components
    		val histories = updatedStateCollector.collected.view.map(_._2.toSeq).transpose
    		runTrace.componentHistories((components, histories).zipped.map{createComponentHistory(_, _)}.toList)
    	}
    	if (!alteredStateCollector.collected.isEmpty) {
    	    // TODO
    	}
    	runTrace
    }

    def createComponentHistory(component : C, history : ju.List[T]) = new ComponentHistory(component, history)
}

class DummyTraceTimeRunnable[T, C](timeRunnable : TimeRunnable) extends TimeRunnable with RunTraceHolder[T, C] {

    override def runFor(timeDiff : BigDecimal) = timeRunnable.runFor(timeDiff)

    override def runUntil(finalTime : BigDecimal) = timeRunnable.runUntil(finalTime)

    override def currentTime = timeRunnable.currentTime

    override def nextTimeStepSize = timeRunnable.nextTimeStepSize

    override def getRunTrace = {
        // run for 0 time to get final state
    	runFor(0 : BigDecimal)
    	val runTrace = new ComponentRunTrace[T, C]
    	runTrace.runTime(currentTime.doubleValue)
    	runTrace
    }
}

object TraceTimeRunnable{
    def apply[T : Manifest, C, S[X] : SeqIndexAccessible](
    	timeRunnable : TimeRunnable with Publisher[StateEvent[T, S]]
    ) : TimeRunnable with RunTraceHolder[T, C] = new TraceTimeRunnable(timeRunnable)

    def applyDummy[T, C](
    	timeRunnable : TimeRunnable
    ) : TimeRunnable with RunTraceHolder[T, C] = new DummyTraceTimeRunnable(timeRunnable)
}