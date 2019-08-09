package com.bnd.network.business

import scala.collection.JavaConversions._
import com.bnd.core.CollectionElementsConversions._
import java.{lang => jl, util => ju}

import com.bnd.function.{domain => fd}
import com.bnd.network.domain.NetworkSimulationConfig
import com.bnd.core.DoubleConvertible.Implicits.addAsDouble
import com.bnd.network.domain.Network
import com.bnd.math.domain.evo.ArrayChromosome
import com.bnd.function.domain.TransitionTable
import com.bnd.core.CollectionElementsConversions._
import com.bnd.network.domain.NetworkActionSeries

import scala.collection.mutable.Subscriber
import com.bnd.core.runnable.StateEvent

import scala.collection.mutable.Publisher
import com.bnd.core.runnable.StateUpdatedEvent
import com.bnd.network.domain.TopologicalNode
import com.bnd.network.domain.NetworkFunction
import java.util.Date

import com.bnd.function.evaluator.FunctionEvaluator
import com.bnd.function.business.ScalaFunctionEvaluatorConversions.functionEvaluatorToScalaDoubleMapFunction
import com.bnd.function.business.ScalaFunctionEvaluatorConversions.functionEvaluatorToScalaMapFunction
import com.bnd.core.DoubleConvertible
import com.bnd.network.domain.NetworkEvaluationVariable
import com.bnd.network.domain.NetworkEvaluation
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import com.bnd.network.BndNetworkException
import com.bnd.core.reflection.GenericReflectionProvider

trait NetworkEvaluator[T] extends Subscriber[StateEvent[T, ju.List], Publisher[StateEvent[T, ju.List]]] {
    def result : Double
}

private final class InputLastTwoOutputsNetworkEvaluator[T](runTime : BigDecimal, evalFun : (Seq[T], Seq[T], Seq[T]) => Double) extends NetworkEvaluator[T] {

  private var firstState : Seq[T] = null
  private var lastButOneState : Seq[T] = null
  private var lastState : Seq[T] = null

	def notify(pub: Publisher[StateEvent[T, ju.List]], event: StateEvent[T, ju.List]) =
    event match {
	    case StateUpdatedEvent(time : BigDecimal, components_ : Iterable[TopologicalNode], state : ju.List[T]) => {
	      if (firstState == null) firstState = new ju.ArrayList(state) // clone()

	    	if (time.equals(runTime - 1))
	    	  lastButOneState = new ju.ArrayList(state) // clone()
	    	else if (time.equals(runTime))
	    	  lastState = new ju.ArrayList(state) // clone()
	    	else if (time > runTime)
	    	  throw new RuntimeException("Somehow actual time " + time + " is greater than given run time " + runTime)

	    }
	    case _ =>
		}

  override def result = evalFun(firstState, lastButOneState, lastState)
}

private final class InputLastNetworkEvaluator[T](runTime : BigDecimal, evalFun : (Seq[T], Seq[T]) => Double) extends NetworkEvaluator[T] {

  private var firstState : Seq[T] = null
  private var lastState : Seq[T] = null

	def notify(pub: Publisher[StateEvent[T, ju.List]], event: StateEvent[T, ju.List]) =
    event match {
	    case StateUpdatedEvent(time : BigDecimal, components_ : Iterable[TopologicalNode], state : ju.List[T]) => {
	      if (firstState == null) firstState = new ju.ArrayList(state) // clone()

	    	if (time.equals(runTime))
	    	  lastState = new ju.ArrayList(state) // clone()
	    	else if (time > runTime)
	    	  throw new RuntimeException("Somehow actual time " + time + " is greater than given run time " + runTime)
	    }
	    case _ =>
		}

    override def result = evalFun(firstState, lastState)
}

object NetworkEvaluator {

    private def customFunctionEvaluation[T : Manifest : DoubleConvertible](
        funEvaluatorFactory : FunctionEvaluatorFactory,
        networkEvaluation : NetworkEvaluation)(
        firstState : Seq[T],
        lastButOneState : Seq[T],
        lastState : Seq[T]
    ) = {
	    // TODO: this conversion is very ineffective
        val firstStateD : Seq[jl.Double] = firstState : Seq[Double]
        val lastStateD : Seq[jl.Double] = if (lastState != null) lastState : Seq[Double] else firstStateD
        val lastButOneStateD : Seq[jl.Double] = if (lastButOneState != null) lastButOneState : Seq[Double] else lastStateD

        val environment = Map("firstState" -> (firstStateD : Array[jl.Double]), "lastButOneState" -> (lastButOneStateD : Array[jl.Double]), "lastState" -> (lastStateD : Array[jl.Double]))
        val componentIndexMap = Map("firstState" -> 0, "lastButOneState" -> 1, "lastState" -> 2)
        val variableFunIndexMap : Map[NetworkEvaluationVariable, Int] = networkEvaluation.getVariables.map(
	            variable => (variable, variable.getVariableIndex : Int) ).toMap

	    if (networkEvaluation.getEvaluationItems.size > networkEvaluation.getVariables.size)
	        throw new BndNetworkException(networkEvaluation.getEvaluationItems.size + " vs " + networkEvaluation.getVariables.size)

        val varEnvironment = networkEvaluation.getEvaluationItems.map(item => {
        	val fun = functionEvaluatorToScalaMapFunction[Array[jl.Double], jl.Double, String](
                funEvaluatorFactory.createInstance[Array[jl.Double], jl.Double](item.getEvalFunction),
                componentIndexMap)
            (item.getVariable, fun(environment))
        }).toMap

        // main evaluation function
        val fun = functionEvaluatorToScalaDoubleMapFunction[Any, jl.Double, String, NetworkEvaluationVariable](
                funEvaluatorFactory.createInstance(networkEvaluation.getEvalFunction.asInstanceOf[fd.Function[Any, jl.Double]]),
                componentIndexMap,
                variableFunIndexMap)

        fun(environment, varEnvironment)
    }

    def apply[T : Manifest : DoubleConvertible](
         funEvaluatorFactory : FunctionEvaluatorFactory,
         networkEvaluation : NetworkEvaluation,
         runTime : Int) : NetworkEvaluator[T] = 
        new InputLastTwoOutputsNetworkEvaluator(runTime, customFunctionEvaluation[T](funEvaluatorFactory, networkEvaluation))

    def apply[T](
         evalFun : (Seq[T], Seq[T]) => Double,
         runTime : Int) : NetworkEvaluator[T] = 
        new InputLastNetworkEvaluator(runTime, evalFun)
}