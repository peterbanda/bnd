package com.bnd.network.business

import java.{lang => jl, util => ju}

import scala.collection.JavaConversions._
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.util.ConversionUtil
import com.bnd.core.util.RandomUtil
import com.bnd.function.business.FunctionFactory
import com.bnd.function.domain.TransitionTable
import com.bnd.network.domain.Network
import com.bnd.network.domain.NetworkFunction
import com.bnd.network.domain.SpatialTopology
import junit.framework.TestCase._
import com.bnd.function.BndFunctionException
import com.bnd.core.domain.MultiStateUpdateType
import com.bnd.core.domain.MultiStateUpdateType
import com.bnd.core.metrics.MetricsType
import com.bnd.core.util.{ConversionUtil, RandomUtil}

/**
 * @author © Peter Banda
 * @since 2013  
 */
class ScalaNetworkBOFactoryTest extends ScalaNetworkTest {

	// Constants
	val SHOW_OUTPUTS = false
	val CELLS = new jl.Integer(1000)
	val REPETITIONS = 20
	val STEPS = 4000

	val functionFactory = new FunctionFactory()

	@Autowired
	val scalaBooleanNetworkFactory : NetworkBOFactory[Boolean] = null

	@Test
	def testCreateBooleanNetworkBO3() {
		val networkBO = scalaBooleanNetworkFactory.createNetworkBO(createBooleanNetworkTestData())
		assertNotNull(networkBO)
		assertNotEmpty(networkBO.getNodes())

		var msSum : Long = 0
		for (j <- 1 to REPETITIONS) {
			val startTime = new ju.Date()
			networkBO.setNodeStates(createBooleanNetworkConfigurationTestData(networkBO.getNodes().size()))
			for (i <- 1 to STEPS) {
				if (SHOW_OUTPUTS) {
					println(networkBO.getNodeStatesInLocationOrder())
				}
				networkBO.updateState()
			}
			if (SHOW_OUTPUTS) {
				println(networkBO.getNodeStatesInLocationOrder())
				println("")
			}
			assertNotEmpty(networkBO.getNodeStates())
			val endTime = new ju.Date()
			msSum = (msSum + (endTime.getTime() - startTime.getTime()))
		}
		println("Average Time (ms): " + msSum / REPETITIONS)
	}

	def createTopologyTestData3() : SpatialTopology = {
		new SpatialTopology {
			setMetricsType(MetricsType.Manhattan)
			setTorusFlag(true)
			setItsOwnNeighor(true)
			addSize(CELLS)
			setRadius(1)
		}
	}

	def createBooleanNetworkTestData() : Network[Boolean] = {
		new Network[Boolean] {
			setTopology(createTopologyTestData3())
			setFunction(createBooleanNetworkFunctionTestData3())
		}
	}

	def createBooleanNetworkConfigurationTestData(nodeNum : Int) : java.util.List[Boolean] = {
		var configuration = new ju.ArrayList[Boolean]
		val booleans : Array[Boolean] = Array(true, false)
		for (i <- 1 to nodeNum) {
			configuration.add(if (RandomUtil.nextBoolean()) booleans(0) else booleans(1))
		}
		configuration
	}

	def createBooleanNetworkFunctionTestData3() : NetworkFunction[Boolean] = {
	    val javaOutputs = ConversionUtil.convertDecimalToBooleanList(110, 8)
	    ju.Collections reverse javaOutputs
	    val outputsx : Seq[jl.Boolean] = javaOutputs
	    val outputsz : Seq[Boolean] = outputsx
	    val outputs : ju.Collection[Boolean] = outputsz

		var function = new NetworkFunction[Boolean] {
			setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
			setFunction(createScalaBoolTransitionTable(outputs))
		}
		function
	}

	def createScalaBoolTransitionTable(tableOutputs : Iterable[Boolean]) : TransitionTable[Boolean, Boolean] = {
		if (tableOutputs == null || tableOutputs.isEmpty()) {
			throw new BndFunctionException("Transition table can not be empty!")
		}
		val numberOfRows = tableOutputs.size()
		val arity = (Math.log(numberOfRows) / Math.log(2)).toInt
		var transitionTable = new TransitionTable[Boolean, Boolean] {
			setArity(arity)
			setInputClazz(classOf[Boolean])
			setOutputClazz(classOf[Boolean])
			setRangeFrom(false : Boolean)
			setRangeTo(true : Boolean)
		}
		for (tableOutput <- tableOutputs) transitionTable.addOutput(tableOutput)
		transitionTable
	}
}