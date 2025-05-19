package com.bnd.network.business

import com.bnd.core.dynamics.StateAlternationType
import com.bnd.core.runnable.{StateAlternation, StateAssignmentAlternation}

import java.util.ArrayList
import java.util.Collections
import scala.jdk.CollectionConverters._
import scala.collection.Map
import com.bnd.function.business.ScalaFunctionEvaluatorConversions._
import com.bnd.function.evaluator.FunctionEvaluatorFactory

import java.{lang => jl}
import java.{util => ju}
import com.bnd.network.domain.NetworkActionSeries
import com.bnd.network.domain.NetworkSimulationConfig
import com.bnd.network.domain.NetworkAction
import com.bnd.network.domain.TopologicalNode
import com.bnd.math.business.rand.RandomDistributionProviderFactory
import com.bnd.network.BndNetworkException
import com.bnd.network.domain.TopologicalNodeLocationComparator

class NetworkScriptFactory(private val funEvaluatorFactory : FunctionEvaluatorFactory) {

	def apply[T](
	    simConfig : NetworkSimulationConfig)(
	    actionSeries : NetworkActionSeries[T],
	    components : Iterable[TopologicalNode]
	) : Stream[StateAlternation[T, TopologicalNode, Nothing]]= {
//		implicit val scalaActionOrdering: Ordering[NetworkAction[_]] = new Ordering[NetworkAction[_]] {
//			def compare(x: NetworkAction[_], y: NetworkAction[_]): Int = x.compareTo(y)
//		}

		val actionSorted = actionSeries.getActions.asScala.toSeq.sorted

		implicit val scalaOrdering: Ordering[TopologicalNode] = (x: TopologicalNode, y: TopologicalNode) => new TopologicalNodeLocationComparator().compare(x, y)

		val orderedComponentsAux = components.toSeq
		val orderedComponents = if (components.head.hasLocation) orderedComponentsAux.sorted else orderedComponentsAux

		val initPart = toAlternations(simConfig, orderedComponents)(actionSorted).toStream
		val periodicPart = if (actionSeries.isPeriodic) {
		    val periodicAlternators = repeat(
		            toAlternationFactoryFuns(simConfig, orderedComponents)(actionSorted.drop(actionSeries.getRepeatFromElementSafe)),
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
	        case StateAlternationType.Replacement =>
						{
						import scala.jdk.CollectionConverters._
						val pairs = components.zip(newStates.asScala).toSeq
						new StateAssignmentAlternation(startTime, action.getTimeLength : Double, pairs)
					}

//			case StateAlternationType.Replacement => newReplacement[T, TopologicalNode, Nothing]
//			case StateAlternationType.Addition => newDoubleAddition[T, TopologicalNode, Nothing]
//			case StateAlternationType.Influx => StateAlternationRepeatFirstInflux[TopologicalNode, Nothing](
//			        simConfig.getOdeSolverType,
//			        simConfig.getTimeStep,
//			        if (simConfig.getTolerance != null) Some(simConfig.getTolerance) else None)_
		}
	}
}