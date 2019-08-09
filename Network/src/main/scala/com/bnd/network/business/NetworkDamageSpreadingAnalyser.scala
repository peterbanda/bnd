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
import com.bnd.core.reflection.GenericReflectionProvider
import com.bnd.core.runnable.TimeRunnable
import com.bnd.core.util.RandomUtil

import scala.collection.mutable.ListBuffer

trait NetworkDamageSpreadingAnalyser[T] {
    def run(
        network : Network[T],
        simulationConfig : NetworkSimulationConfig,
        maxRunTime : Double,
        stepSize : Double,
        repetitions : Int
    ) : Iterable[(Double,Double)]
}

object NetworkDamageSpreadingAnalyser {

    def apply[T](
    	networkRunnableFactory : NetworkRunnableFactory[T],
    	randomDistributionProvider : RandomDistributionProvider[T],
    	distanceFun : (Seq[T], Seq[T]) => Double,
    	flip : T => T
    ) : NetworkDamageSpreadingAnalyser[T] = new NetworkDamageSpreadingAnalyserImpl[T](networkRunnableFactory, randomDistributionProvider, distanceFun, flip)
}

private class NetworkDamageSpreadingAnalyserImpl[T](
    networkRunnableFactory : NetworkRunnableFactory[T],
    randomDistributionProvider : RandomDistributionProvider[T],
    distanceFun : (Seq[T], Seq[T]) => Double,
    flip : T => T) extends NetworkDamageSpreadingAnalyser[T] {

    override def run(
        network : Network[T],
        simulationConfig : NetworkSimulationConfig,
        maxRunTime : Double,
        stepSize : Double,
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

        def setState(
            networkRunnable : FullStateAccessible[T, TopologicalNode],
            orderedComponents : ju.List[TopologicalNode],
            initState : ju.List[T]
        ) = (orderedComponents, initState).zipped.foreach{
		        case (component,state) => networkRunnable.setState(component, state)
		    }

        def runNetwork(
            networkRunnable : TimeRunnable with FullStateAccessible[T, TopologicalNode],
            orderedComponents : ju.List[TopologicalNode]
        ) = {
	    	networkRunnable.runFor(stepSize)
	    	orderedComponents.map(networkRunnable.getState(_))
        }

	    val distances = for (i <- 1 to repetitions) yield {
	    	val initState1 = randomDistributionProvider.nextList(networkSize)
	    	val initState2 : ju.List[T] = new ju.ArrayList[T](initState1)
	    	val perturbPos = RandomUtil.nextInt(networkSize)
	    	initState2.set(perturbPos, flip(initState2.get(perturbPos)))

	    	setState(networkRunnable1, orderedComponents1, initState1)
	    	setState(networkRunnable2, orderedComponents2, initState2)

	    	var time = 0 : Double
	    	val buffer = new ListBuffer[(Double,Double)]
	    	while (time < maxRunTime) {
	    	    val state1 = runNetwork(networkRunnable1, orderedComponents1)
	    		val state2 = runNetwork(networkRunnable2, orderedComponents2)
	    		time = time + stepSize
	    		val distance = distanceFun(state1, state2)
	    		buffer += {(time, distance)}
	    	}
	    	buffer
	    }
        distances.flatten
    }
}