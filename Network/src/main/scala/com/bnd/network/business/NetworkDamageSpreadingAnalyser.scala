package com.bnd.network.business

import com.bnd.core.runnable.{FullStateAccessible, TimeRunnable}
import com.bnd.core.util.RandomUtil

import scala.jdk.CollectionConverters._
import java.{lang => jl, util => ju}
import com.bnd.function.{domain => fd}
import com.bnd.network.domain.NetworkSimulationConfig
import com.bnd.network.domain.Network
import com.bnd.network.domain.TopologicalNode
import com.bnd.math.business.rand.RandomDistributionProvider

import com.bnd.network.domain.TopologicalNodeLocationComparator

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
        val components1 = networkRunnable1.componentIndexMap.keys
        val components2 = networkRunnable2.componentIndexMap.keys
        val networkSize = components1.size

				implicit val scalaOrdering: Ordering[TopologicalNode] = (x: TopologicalNode, y: TopologicalNode) => new TopologicalNodeLocationComparator().compare(x, y)

				val orderedComponents1 = components1.toSeq.sorted
				val orderedComponents2Aux = components2.toSeq
				val orderedComponents2 = if (components2.head.hasLocation) orderedComponents2Aux.sorted else orderedComponents2Aux

      	def setState(
         		networkRunnable : FullStateAccessible[T, TopologicalNode],
         		orderedComponents : Seq[TopologicalNode],
         		initState : Seq[T]
      	) = (orderedComponents, initState).zipped.foreach {
		  			case (component,state) => networkRunnable.setState(component, state)
		    }

        def runNetwork(
        		networkRunnable : TimeRunnable with FullStateAccessible[T, TopologicalNode],
          	orderedComponents : Seq[TopologicalNode]
        ) = {
	    		networkRunnable.runFor(stepSize)
	    		orderedComponents.map(networkRunnable.getState(_))
        }

	    	val distances = for (i <- 1 to repetitions) yield {
	    			val initState1 = randomDistributionProvider.nextList(networkSize)
	    			val initState2 : ju.List[T] = new ju.ArrayList[T](initState1)
	    			val perturbPos = RandomUtil.nextInt(networkSize)
	    			initState2.set(perturbPos, flip(initState2.get(perturbPos)))

	    			setState(networkRunnable1, orderedComponents1, initState1.asScala.toSeq)
	    			setState(networkRunnable2, orderedComponents2, initState2.asScala.toSeq)

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