package com.bnd.math.business.dynamics

import java.{util => ju}

import scala.collection.JavaConversions._
import scala.math.Numeric._
import scala.math.Integral.Implicits._
import scala.math._
import com.bnd.core.runnable.StateAccessible
import com.bnd.core.runnable.TimeRunnable

abstract class SingleRunProcessorSuite[T,O](val processors : Iterable[SingleRunProcessor[T,_]]) {

    def run(
        runnable : TimeRunnable with StateAccessible[T],
        initialState : Seq[T],
        timeStepLength : Double,
        iterations : Int) : O = {

        def nextStates = {
        	runnable.runFor(timeStepLength)
        	runnable.getStates()
        }

        // state initial state
        runnable.setStates(initialState)

        val statesHistory = for (i <- 1 to iterations) yield nextStates : Iterable[T]
        val statesHistoryTransposed = statesHistory.transpose
        createResult(for (processor <- processors) yield {
            if (processor.isTransposedStatesHistoryExpected)
                processor.process(statesHistoryTransposed, timeStepLength)
        	else
        		processor.process(statesHistory, timeStepLength)
        })
    }

    protected def createResult(processorResults : Iterable[_]) : O
}