package com.bnd.math.business.dynamics

import java.{lang => jl, util => ju}

import scala.collection.JavaConversions._
import com.bnd.core.runnable.StateAccessible

import scala.math.Numeric._
import scala.math.Integral.Implicits._
import scala.math._
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.runnable.TimeRunnable

class SingleRunProcessorExec[T,O](val processor : SingleRunProcessor[T,O]) {

    def run(
        runnable : TimeRunnable with StateAccessible[T],
        initialState : Seq[T],
        timeStepLength : Double,
        iterations : Int) : O = {

        def nextStates() = {
        	runnable.runFor(timeStepLength)
        	runnable.getStates()
        }

        // state initial state
        runnable.setStates(initialState)

        val statesHistory = for (i <- 1 to iterations) yield nextStates() : Iterable[T]

        if (processor.isTransposedStatesHistoryExpected) {
        	processor.process(statesHistory.transpose, timeStepLength)
        } else
        	processor.process(statesHistory, timeStepLength)
    }
}

class JavaDoubleSingleRunProcessorExec[O](val processor : DoubleSingleRunProcessor[O]) {

    val proxied = new SingleRunProcessorExec[Double,O](processor)

    def run(
        runnable : TimeRunnable with StateAccessible[jl.Double],
        initialState : ju.List[jl.Double],
        timeStepLength : jl.Double,
        iterations : jl.Integer
    ) : O = {
		val scalaDoubleRunnable = TimeRunnableDoubleAdapter(runnable)
		val seq = initialState : Seq[jl.Double]
		proxied.run(scalaDoubleRunnable, seq, timeStepLength, iterations)
    }
}

class JavaStationaryPointCountProcessor(val detectionPrecision : jl.Double) extends DoubleSingleRunProcessor[jl.Iterable[jl.Integer]] {

    val proxied = new StationaryPointCountProcessor[Double](detectionPrecision)

    override def process(statesHistory : Iterable[Iterable[Double]], timeStepLength : Double) : jl.Iterable[jl.Integer] = {
        val a = proxied.process(statesHistory, timeStepLength) : Iterable[jl.Integer]
        scalaIterableToJavaIterable(a)
    }

    override def isTransposedStatesHistoryExpected = proxied.isTransposedStatesHistoryExpected
}

class JavaTurningPointCountProcessor(
    val detectionPrecision : jl.Double,
    val extremeDiff : jl.Double,
    val upperBound : jl.Double) extends DoubleSingleRunProcessor[jl.Iterable[jl.Integer]] {

    val proxied = new TurningPointCountProcessor[Double](detectionPrecision, extremeDiff, upperBound)

    override def process(statesHistory : Iterable[Iterable[Double]], timeStepLength : Double) : jl.Iterable[jl.Integer] = {
        val a = proxied.process(statesHistory, timeStepLength) : Iterable[jl.Integer]
        scalaIterableToJavaIterable(a)
    }

    override def isTransposedStatesHistoryExpected = proxied.isTransposedStatesHistoryExpected
}

class JavaNonlinearityErrorProcessor(val upperBound : jl.Double) extends DoubleSingleRunProcessor[jl.Iterable[jl.Double]] {

    val proxied = new NonlinearityLastProcessor[Double](true, upperBound)

    override def process(statesHistory : Iterable[Iterable[Double]], timeStepLength : Double) : jl.Iterable[jl.Double] = {
        val a = proxied.process(statesHistory, timeStepLength) : Iterable[jl.Double]
        scalaIterableToJavaIterable(a)
    }

    override def isTransposedStatesHistoryExpected = proxied.isTransposedStatesHistoryExpected
}

trait DoubleSingleRunProcessor[O] extends SingleRunProcessor[Double, O]