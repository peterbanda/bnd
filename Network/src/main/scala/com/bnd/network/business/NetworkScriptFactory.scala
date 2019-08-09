package com.bnd.network.business

import java.util.ArrayList
import java.util.Collections

import scala.collection.JavaConversions._
import scala.collection.Map
import com.bnd.function.business.ScalaFunctionEvaluatorConversions._
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import java.{lang => jl}
import java.{util => ju}

import com.bnd.core.runnable.StateAlternation
import com.bnd.core.runnable.StateAssignmentAlternation
import com.bnd.network.domain.NetworkActionSeries
import com.bnd.network.domain.NetworkSimulationConfig
import com.bnd.network.domain.NetworkAction
import com.bnd.network.domain.TopologicalNode
import com.bnd.math.business.rand.RandomDistributionProviderFactory
import com.bnd.network.BndNetworkException
import com.bnd.network.domain.TopologicalNodeLocationComparator
import com.bnd.core.dynamics.StateAlternationType

class NetworkScriptFactory(private val funEvaluatorFactory : FunctionEvaluatorFactory) {

	def apply[T](
	    simConfig : NetworkSimulationConfig)(
	    actionSeries : NetworkActionSeries[T],
	    components : Iterable[TopologicalNode]
	) : Stream[StateAlternation[T, TopologicalNode, Nothing]]= {
		val actions = new ArrayList[NetworkAction[T]](actionSeries.getActions)
		Collections.sort(actions)

		// TODO: optimize this
		val orderedComponents = new ju.ArrayList[TopologicalNode](components)
		if (components.head.hasLocation)
			Collections.sort(orderedComponents, new TopologicalNodeLocationComparator)

		val initPart = toAlternations(simConfig, orderedComponents)(actions).toStream
		val periodicPart = if (actionSeries.isPeriodic) {
		    val periodicAlternators = repeat(
		            toAlternationFactoryFuns(simConfig, orderedComponents)(actions.drop(actionSeries.getRepeatFromElementSafe)),
		            actionSeries.getPeriodicity().toDouble)
		    if (actionSeries.hasRepetitions)
		        periodicAlternators.takeWhile(_.applyStartTime < actionSeries.getRepetitions() * actionSeries.getPeriodicity())
		     else 
		        periodicAlternators
		} else Stream[StateAlternation[T, TopologicalNode, Nothing]]()
		initPart.toStream #::: periodicPart
	}

	private def repeat[T, C](
	    factoryFuns : Iterable[Double => StateAlternation[T, C, Nothing]],
	    periodicity : Double
	)  = {
		def repeatAux(timeShift : Double) : Stream[StateAlternation[T, C, Nothing]] =
		    factoryFuns.map(_(timeShift)).toStream #::: repeatAux(timeShift + periodicity)
		repeatAux(periodicity)
	}

	private def toAlternations[T](
		simConfig : NetworkSimulationConfig,
		components : Iterable[TopologicalNode])(
		actions : Iterable[NetworkAction[T]]
	) = actions.map(action => toAlternation(simConfig, components)(action)(0d))

	private def toAlternationFactoryFuns[T](
		simConfig : NetworkSimulationConfig,
		components : Iterable[TopologicalNode])(
		actions : Iterable[NetworkAction[T]]
	) = actions.map(action => toAlternation(simConfig, components)(action)(_))

	private def toAlternation[T](
	    simConfig : NetworkSimulationConfig,
	    components : Iterable[TopologicalNode])(
		action : NetworkAction[T])(
		timeShift : Double
	) : StateAlternation[T, TopologicalNode, Nothing] = {
	    val startTime = action.getStartTime.doubleValue + timeShift
	    val rdp = RandomDistributionProviderFactory.apply(action.getStateDistribution)

	    val newStates =
	        if (action.getStates != null)
	            action.getStates
	        else if (action.getStateDistribution != null) {
	            val rdp = RandomDistributionProviderFactory.apply(action.getStateDistribution)
	            rdp.nextList(components.size)
	        } else throw new BndNetworkException("States or random distribution expected for network script at time '" + startTime + ".")
  
		action.getAlternationType match {
	        case StateAlternationType.Replacement => new StateAssignmentAlternation(startTime, action.getTimeLength : Double, components zip newStates)
//			case StateAlternationType.Replacement => newReplacement[T, TopologicalNode, Nothing]
//			case StateAlternationType.Addition => newDoubleAddition[T, TopologicalNode, Nothing]
//			case StateAlternationType.Influx => StateAlternationRepeatFirstInflux[TopologicalNode, Nothing](
//			        simConfig.getOdeSolverType,
//			        simConfig.getTimeStep,
//			        if (simConfig.getTolerance != null) Some(simConfig.getTolerance) else None)_
		}
	}
}