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
import com.bnd.math.business.dynamics.VectorSpace
import com.bnd.network.domain.NetworkAction
import com.bnd.core.dynamics.StateAlternationType
import com.bnd.math.domain.rand.RandomDistribution
import com.bnd.math.business.rand.RandomDistributionProvider
import com.bnd.core.util.RandomUtil
import com.bnd.core.runnable.StateAccessible
import com.bnd.core.runnable.FullStateAccessible
import java.util.Collections

import com.bnd.network.domain.TopologicalNodeLocationComparator
import com.bnd.core.dynamics.StateAlternationType
import com.bnd.core.reflection.GenericReflectionProvider
import com.bnd.core.runnable.TimeRunnable
import com.bnd.core.util.RandomUtil

trait NetworkDerridaAnalyser[T] {
    def run(
        network : Network[T],
        simulationConfig : NetworkSimulationConfig,
        runTime : Double,
        repetitions : Int
    ) : Iterable[(Double,Double)]
}

object NetworkDerridaAnalyser {

    def apply[T](
    	networkRunnableFactory : NetworkRunnableFactory[T],
    	randomDistributionProvider : RandomDistributionProvider[T],
    	distanceFun : (Seq[T], Seq[T]) => Double,
    	flip : T => T
    ) : NetworkDerridaAnalyser[T] = new NetworkDerridaAnalyserImpl[T](networkRunnableFactory, randomDistributionProvider, distanceFun, flip)
}

private class NetworkDerridaAnalyserImpl[T](
    networkRunnableFactory : NetworkRunnableFactory[T],
    randomDistributionProvider : RandomDistributionProvider[T],
    distanceFun : (Seq[T], Seq[T]) => Double,
    flip : T => T) extends NetworkDerridaAnalyser[T] {

    override def run(
        network : Network[T],
        simulationConfig : NetworkSimulationConfig,
        runTime : Double,
        repetitions : Int
    ) = {
        val networkRunnable1 = networkRunnableFactory.createNonInteractive(network, simulationConfig)
        val networkRunnable2 = networkRunnableFactory.createNonInteractive(network, simulationConfig)
        val components1 = networkRunnable1.componentIndexMap.map(_._1)
        val components2 = networkRunnable2.componentIndexMap.map(_._1)
        val networkSize = components1.size

        val orderedComponents1 = new ju.ArrayList[TopologicalNode](components1)
		if (components1.head.hasLocation)
			Collections.sort(orderedComponents1, new TopologicalNodeLocationComparator)

		val orderedComponents2 = new ju.ArrayList[TopologicalNode](components2)
		if (components2.head.hasLocation)
			Collections.sort(orderedComponents2, new TopologicalNodeLocationComparator)

        def runNetwork(
            networkRunnable : TimeRunnable with FullStateAccessible[T, TopologicalNode],
            orderedComponents : ju.List[TopologicalNode],
            initState : ju.List[T]
        ) = {
		    (orderedComponents, initState).zipped.foreach{
		        case (component,state) => networkRunnable.setState(component, state)
		    }
	    	networkRunnable.runFor(runTime)
	    	orderedComponents.map(networkRunnable.getState(_))
        }

        val distances = for (perturbPos <- 0 to networkSize) yield
	    	for (i <- 1 to repetitions) yield {
	    		val initState1 = randomDistributionProvider.nextList(networkSize)
	    		val initState2 : ju.List[T] = new ju.ArrayList[T](initState1)

	    		val positions = RandomUtil.nextElementsWithoutRepetitions(networkSize, perturbPos)
	    		positions.foreach(pos => initState2.set(pos, flip(initState2.get(pos))))

	    		val initDistance = distanceFun(initState1, initState2)

	    		val finalState1 = runNetwork(networkRunnable1, orderedComponents1, initState1)
	    		val finalState2 = runNetwork(networkRunnable2, orderedComponents2, initState2)

	    		val finalDistance = distanceFun(finalState1, finalState2)
	    		(initDistance, finalDistance)
	    	}
        distances.flatten
    }

    private def createActionSeries(state : ju.List[T]) = {
    	val actionSeries = new NetworkActionSeries[T]
    	val initAction = new NetworkAction[T]
	    initAction.setStartTime(0)
	    initAction.setTimeLength(0)
	    initAction.setAlternationType(StateAlternationType.Replacement)
	    initAction.setStates(state)
	    actionSeries.addAction(initAction)
	    actionSeries
    }
}