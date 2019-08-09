package com.bnd.network.business

import java.util.Collections
import java.{lang => jl, util => ju}

import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.domain.MultiStateUpdateType
import com.bnd.core.runnable.SeqIndexAccessible._
import com.bnd.core.runnable.{ComponentStateCollector, ComposedStateProducer, FixedPointDetector, StrictFixedPointDetector}
import com.bnd.core.util.{ConversionUtil, RandomUtil}
import com.bnd.function.domain.{AbstractFunction, TransitionTable}
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import com.bnd.function.{BndFunctionException, domain => fd}
import com.bnd.network.business.ScalaNetworkTSMTest._
import com.bnd.network.business.integrator.StatesWeightsIntegratorDef.StatesWeightsIntegrator
import com.bnd.network.domain.{Network, NetworkFunction, NetworkSimulationConfig, SpatialTopology, TopologicalNode, TopologicalNodeLocationComparator, Topology}
import com.bnd.core.domain.MultiStateUpdateType
import com.bnd.core.metrics.MetricsType
import com.bnd.core.runnable.TimeStateManager
import com.bnd.core.util.{ConversionUtil, RandomUtil}
import junit.framework.TestCase._
import org.junit.{BeforeClass, FixMethodOrder, Test}
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConversions._
import scala.util.Random

/**
 * @author © Peter Banda
 * @since 2013  
 */
object ScalaNetworkTSMTest {

  val repetitions = 1000
  val runTime = 2000
  val nodesNum = 100
  val fixedPointPeriodicity = 20

  var topologies: Iterable[Topology] = _
  var functions: Iterable[fd.AbstractFunction[Boolean, Boolean]] = _
  var initialStates: Iterable[Seq[Boolean]] = _
  val booleanFixedPointDetector = new StrictFixedPointDetector[Boolean](fixedPointPeriodicity)

  @BeforeClass
  def initialize {
    topologies = for (_ <- 1 to repetitions) yield createSpatialTopologyTestData(nodesNum)

    functions = for (topology <- topologies) yield createBoolFunction(Random.nextInt(255), 3)

    initialStates = for (topology <- topologies) yield createInitialStates(nodesNum)
  }

  def createBoolFunction(ruleNum: Int, neighborsNum: Int): AbstractFunction[Boolean, Boolean] = {
    val javaOutputs = ConversionUtil.convertDecimalToBooleanList(ruleNum, jl.Double.valueOf(math.pow(2, neighborsNum)).intValue)
    ju.Collections reverse javaOutputs
    val outputsx: Seq[jl.Boolean] = javaOutputs
    val outputsz: Seq[Boolean] = outputsx
    val outputs: ju.Collection[Boolean] = outputsz

    createScalaBoolTransitionTable(outputs)
  }

  def createScalaBoolTransitionTable(tableOutputs: Iterable[Boolean]): TransitionTable[Boolean, Boolean] = {
    if (tableOutputs == null || tableOutputs.isEmpty) {
      throw new BndFunctionException("Transition table can not be empty!")
    }
    val numberOfRows = tableOutputs.size
    val arity = (Math.log(numberOfRows) / Math.log(2)).toInt
    var transitionTable = new TransitionTable[Boolean, Boolean] {
      setArity(arity)
      setInputClazz(classOf[Boolean])
      setOutputClazz(classOf[Boolean])
      setRangeFrom(false: Boolean)
      setRangeTo(true: Boolean)
    }
    for (tableOutput <- tableOutputs) transitionTable.addOutput(tableOutput)
    transitionTable
  }

  def createSpatialTopologyTestData(nodesNum: Int) = new SpatialTopology {
    setMetricsType(MetricsType.Manhattan)
    setTorusFlag(true)
    setItsOwnNeighor(true)
    addSize(nodesNum)
    setRadius(1)
  }

  def createInitialStates(nodeNum: Int): ju.List[Boolean] = {
    var configuration = new ju.ArrayList[Boolean]
    val booleans: Array[Boolean] = Array(true, false)
    for (i <- 1 to nodeNum) {
      configuration.add(if (RandomUtil.nextBoolean()) booleans(0) else booleans(1))
    }
    configuration
  }
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ScalaNetworkTSMTest extends ScalaNetworkTest {

  @Autowired
  val topologyFactory: TopologyFactory = null

  @Autowired
  val scalaBooleanNetworkFactory: NetworkBOFactory[Boolean] = null

  @Autowired
  val functionEvaluatorFactory: FunctionEvaluatorFactory = null

  @Autowired
  val metaNetworkRunnableFactory: MetaNetworkRunnableFactory = null

  @Test
  def test1() {
    val booleanNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[Boolean]
    val booleanNetworkRunnable = booleanNetworkRunnableFactory.createNonInteractive(
      createNetwork(topologies.head, functions.head), new NetworkSimulationConfig)
    booleanNetworkRunnable.setStates(initialStates.head)
    booleanNetworkRunnable.runFor(runTime)
  }

  @Test
  def test1X = runNetworkTSM(MultiStateUpdateType.Sync, false, false)(topologies.head, functions.head, initialStates.head)

  @Test
  def test2BooleanNetworkTSM = (topologies, functions, initialStates).zipped.foreach(runNetworkTSM(MultiStateUpdateType.Sync, false, false))

  @Test
  def test3BooleanNetworkTSMUniformStepSize = (topologies, functions, initialStates).zipped.foreach(runNetworkTSM(MultiStateUpdateType.Sync, true, false))

  @Test
  def test4BooleanNetworkTSMSingleOuput = (topologies, functions, initialStates).zipped.foreach(runNetworkTSM(MultiStateUpdateType.Sync, false, true))

  @Test
  def test5BooleanNetworkTSMSingleOuputUniformStepSize = (topologies, functions, initialStates).zipped.foreach(runNetworkTSM(MultiStateUpdateType.Sync, true, true))

  @Test
  def test6BooleanNetworkOld = (topologies, functions, initialStates).zipped.foreach(runBooleanNetworkOld)

  @Test
  def testXXCorrectness() {
    def assertSameTimeAndState(
      timeStates1: Iterable[(BigDecimal, Seq[Boolean])],
      timeStates2: Iterable[(BigDecimal, Seq[Boolean])]
    ) = (timeStates1, timeStates2).zipped.foreach { case ((time1, state1), (time2, state2)) => {
      assertEquals(time1, time2)
      assertEquals("States at time " + time1 + " not equal", state1, state2)
    }
    }

    (topologies, functions, initialStates).zipped.foreach((topology, function, initialState) => {
      val states1 = getNetworkTSMStates(MultiStateUpdateType.Sync, false, false)(topology, function, initialState)
      val states2 = getNetworkTSMStates(MultiStateUpdateType.Sync, true, false)(topology, function, initialState)
      val states3 = getNetworkTSMStates(MultiStateUpdateType.Sync, false, true)(topology, function, initialState)
      val states4 = getNetworkTSMStates(MultiStateUpdateType.Sync, true, true)(topology, function, initialState)
      val statesOld = getBooleanNetworkOldStates(topology, function, initialState)
      assertSameTimeAndState(states1, statesOld)
      assertSameTimeAndState(states2, statesOld)
      assertSameTimeAndState(states3, statesOld)
      assertSameTimeAndState(states4, statesOld)
    })
  }

  def runNetworkTSM[T: Manifest](
    multiStateUpdateType: MultiStateUpdateType,
    uniformStepSizeFlag: Boolean,
    singleOutputOptimization: Boolean,
    statesWeightsIntegrator: Option[StatesWeightsIntegrator[T]] = None,
    fixedPointDetector: Option[FixedPointDetector[T]] = None)(
    topology: Topology,
    function: fd.Function[T, T],
    initialStates: Seq[T]
  ) {
    val runnable = createNetworkNew(multiStateUpdateType, uniformStepSizeFlag, singleOutputOptimization, statesWeightsIntegrator, fixedPointDetector, topology, function, initialStates)
    runnable.runFor(runTime)
  }

  def runBooleanNetworkOld(
    topology: Topology,
    function: fd.AbstractFunction[Boolean, Boolean],
    initialStates: Seq[Boolean]
  ) {
    val networkBO = createNetworkOld(topology, function, initialStates)
    networkBO.runFor(runTime)
  }

  def getNetworkTSMStates[T: Manifest](
    multiStateUpdateType: MultiStateUpdateType,
    uniformStepSizeFlag: Boolean,
    singleOutputOptimization: Boolean,
    statesWeightsIntegrator: Option[StatesWeightsIntegrator[T]] = None,
    fixedPointDetector: Option[FixedPointDetector[T]] = None)(
    topology: Topology,
    function: fd.Function[T, T],
    initialStates: Seq[T]
  ) = {
    val runnable = createNetworkNew(multiStateUpdateType, uniformStepSizeFlag, singleOutputOptimization, statesWeightsIntegrator, fixedPointDetector, topology, function, initialStates)
    val collector = new ComponentStateCollector[T, TopologicalNode, ju.List]
    runnable.subscribe(collector)
    runnable.runFor(runTime)

    val orderedComponents = new ju.ArrayList[TopologicalNode](collector.components)
    Collections.sort(orderedComponents, new TopologicalNodeLocationComparator)
    val orderedComponentIndexMap = orderedComponents.zipWithIndex.toMap
    collector.collected.map { case (time, states) => {
      val sortedComponentStates = (collector.components zip states).toList.sortBy { case (node, _) => orderedComponentIndexMap.get(node).get}
      (time, sortedComponentStates.map(_._2))
    }
    }
  }

  def getBooleanNetworkOldStates(
    topology: Topology,
    function: fd.AbstractFunction[Boolean, Boolean],
    initialStates: Seq[Boolean]
  ) = {
    val networkBO = createNetworkOld(topology, function, initialStates)
    for (time <- 0 until runTime) yield {
      val state = networkBO.getNodeStatesInLocationOrder
      networkBO.runFor(1)
      (time: BigDecimal, state: Seq[Boolean])
    }
  }

  def createNetworkOld(
    topology: Topology,
    function: fd.AbstractFunction[Boolean, Boolean],
    initialStates: Seq[Boolean]
  ) = {
    val networkFunction = new NetworkFunction[Boolean] {
      setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
      setFunction(function)
    }

    val network = new Network[Boolean] {
      setTopology(topology)
      setFunction(networkFunction)
    }

    val networkBO = scalaBooleanNetworkFactory.createNetworkBO(network)
    (networkBO.getNodesInLocationOrder, initialStates).zipped.foreach((node, state) => node.setState(state))
    networkBO
  }

  def createNetwork[T](
    topology: Topology,
    function: fd.AbstractFunction[T, T]
  ) = {
    val networkFunction = new NetworkFunction[T] {
      setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
      setFunction(function)
    }

    new Network[T] {
      setTopology(topology)
      setFunction(networkFunction)
    }
  }

  def createNetworkNew[T: Manifest](
    multiStateUpdateType: MultiStateUpdateType,
    uniformStepSizeFlag: Boolean,
    singleOutputOptimization: Boolean,
    statesWeightsIntegrator: Option[StatesWeightsIntegrator[T]],
    fixedPointDetector: Option[FixedPointDetector[T]],
    topology: Topology,
    function: fd.Function[T, T],
    initialStates: Seq[T]
  ) = {
    val newTopology = topologyFactory.apply(topology)
    val functionEvaluator = functionEvaluatorFactory.createInstance(function)
    val nodes = newTopology.getNonBiasNodes
    Collections.sort(nodes, new TopologicalNodeLocationComparator)
    val stateProducers =
      if (statesWeightsIntegrator.isDefined)
        nodes.map(NodeStateProducer(_, statesWeightsIntegrator.get))
      else
        nodes.map(NodeStateProducer(_, functionEvaluator))
    val producer = if (singleOutputOptimization)
      ComposedStateProducer.singleOutputInstance(stateProducers, multiStateUpdateType, uniformStepSizeFlag)
    else
      ComposedStateProducer.multiOutputInstance(stateProducers, multiStateUpdateType, uniformStepSizeFlag)

    val runnable = TimeStateManager(producer, fixedPointDetector, Some(initialStates(0)))
    (nodes, initialStates).zipped.foreach((component, state) => runnable.setState(component, state))
    runnable
  }
}