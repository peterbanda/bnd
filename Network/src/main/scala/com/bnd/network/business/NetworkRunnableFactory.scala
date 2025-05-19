package com.bnd.network.business

import com.bnd.core.ClassUtil
import com.bnd.core.ClassUtil.{toManifest, toType}
import com.bnd.core.runnable.{ComposedStateProducer, DistanceFixedPointDetector, FixedPointDetector, FullStateAccessible, RunTraceHolder, SingleStateProducer, StateAlternation, StateAssignmentAlternation, StateEvent, StrictFixedPointDetector, TimeRunnable, TimeStateManager, TraceTimeRunnable}

import java.io.Serializable
import java.util.Collections
import java.{lang => jl, util => ju}
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import com.bnd.network.BndNetworkException
import com.bnd.network.business.function.ActivationFunctionFactory
import com.bnd.network.business.integrator.StatesWeightsIntegratorDef.StatesWeightsIntegrator
import com.bnd.network.business.integrator.StatesWeightsIntegratorFactory
import com.bnd.network.business.NetworkRunnableFactoryUtil.NetworkRunnable
import com.bnd.network.domain._

import scala.jdk.CollectionConverters._
import scala.collection.mutable.{Publisher => ScalaPublisher}

/**
 * @author © Peter Banda
 * @since 2012  
 */

trait NetworkRunnableFactory[T] extends Serializable {

  def createNonInteractive(
    network: Network[T],
    config: NetworkSimulationConfig
  ): NetworkRunnable[T]

  def createInteractive(
    network: Network[T],
    config: NetworkSimulationConfig,
    actionSeries: NetworkActionSeries[T]
  ): NetworkRunnable[T]

  def createNonInteractiveWeightAccessible(
    network: Network[T],
    config: NetworkSimulationConfig
  ): (NetworkRunnable[T], WeightAccessible[T])

  def createInteractiveLayeredWeightAccessible(
    network: Network[T],
    config: NetworkSimulationConfig,
    initialDelay : BigDecimal,
    singleIterationLength : BigDecimal,
    inputStream: Stream[Seq[T]]
  ): (NetworkRunnable[T], WeightAccessible[T])

  def createInteractiveWithTrace(
    network: Network[T],
    config: NetworkSimulationConfig,
    actionSeries: NetworkActionSeries[T]
  ): TimeRunnable with RunTraceHolder[T, TopologicalNode]
}

trait MetaNetworkRunnableFactory {

  def createInstanceFromClass[T](clazz: Class[T]): NetworkRunnableFactory[T]

  def createInstance[T: Manifest] = {
    val clazz = ClassUtil.extract[T]
    createInstanceFromClass(clazz)
  }
}

private class NetworkRunnableFactoryImpl[T: Manifest](
  functionEvaluatorFactory: FunctionEvaluatorFactory,
  topologyFactory: TopologyFactory,
  networkWeightSetter: UntypedNetworkWeightBuilder[T],
  integratorFactory: StatesWeightsIntegratorFactory[T],
  activationFunctionFactory: Option[ActivationFunctionFactory[T]]
) extends NetworkRunnableFactory[T] {

  private val networkScriptFactory = new NetworkScriptFactory(functionEvaluatorFactory)

  override def createNonInteractive(
    network: Network[T],
    config: NetworkSimulationConfig
  ): NetworkRunnable[T] =
    createGenericWithStateProducer(createNonInteractiveRunnable)(network, config)._1

  override def createInteractive(
    network: Network[T],
    config: NetworkSimulationConfig,
    actionSeries: NetworkActionSeries[T]
  ): NetworkRunnable[T] =
    createGenericWithStateProducer(createInteractiveRunnable({ topology: Topology =>
      networkScriptFactory(config)(actionSeries, topology.getAllNodes.asScala.toSeq)
    }) _)(network, config)._1

  override def createInteractiveWithTrace(
    network: Network[T],
    config: NetworkSimulationConfig,
    actionSeries: NetworkActionSeries[T]
  ): TimeRunnable with RunTraceHolder[T, TopologicalNode] = {
    val networkRunnable = createInteractive(network, config, actionSeries)
    TraceTimeRunnable(networkRunnable)
  }

  override def createNonInteractiveWeightAccessible(
    network: Network[T],
    config: NetworkSimulationConfig
  ) = {
    val (runnable, producer) = createGenericWithStateProducer(createNonInteractiveRunnable)(network, config)

    (runnable, createWeightAccessible(producer))
  }

  override def createInteractiveLayeredWeightAccessible(
    network: Network[T],
    config: NetworkSimulationConfig,
    initialDelay : BigDecimal,
    singleIterationLength : BigDecimal,
    inputStream: Stream[Seq[T]]
  ) = {
    val runnableWithProducer = createGenericWithStateProducer(createInteractiveRunnable({ topology: Topology =>
      if (topology.hasLayers) {
        val layers = topology.getLayers.asScala
        if (layers.nonEmpty) {
          createTrainingAlternations(initialDelay, singleIterationLength, layers.head.getNonBiasNodes().asScala, inputStream)
        } else {
          throw new BndNetworkException("Layers collection is empty.")
        }
      } else {
        throw new BndNetworkException("Layers expected for training network instance.")
      }
    }) _)(network, config)

    (runnableWithProducer._1, createWeightAccessible(runnableWithProducer._2))
  }

  def createTrainingAlternations(
    initialDelay : BigDecimal,
    singleIterationLength : BigDecimal,
    inputComponents : Iterable[TopologicalNode],
    inputStream : Stream[Seq[T]]
  ) : Stream[StateAlternation[T, TopologicalNode, Nothing]] = {
    val startTimes = Stream.iterate(initialDelay)(singleIterationLength + _)
    (inputStream.zip(startTimes)).map{ case (inputValues, startTime) => {
      new StateAssignmentAlternation[T, TopologicalNode](startTime, 0, inputComponents zip inputValues)
    }}
  }

  private def createNonInteractiveRunnable(
    stateProducer: ComposedStateProducer[T, TopologicalNode, ju.List],
    fpd: Option[FixedPointDetector[T]],
    topology: Topology
  ) = TimeStateManager(stateProducer, fpd, Some(null.asInstanceOf[T]))

  private def createInteractiveRunnable(
    scriptCreateFun: Topology => Stream[StateAlternation[T, TopologicalNode, Nothing]])(
    stateProducer: ComposedStateProducer[T, TopologicalNode, ju.List],
    fpd: Option[FixedPointDetector[T]],
    topology: Topology
  ) = TimeStateManager.interactiveInstance(stateProducer, scriptCreateFun(topology), fpd, Some(null.asInstanceOf[T]), None)

  private def createGenericWithStateProducer(
    createRunnableFun: (ComposedStateProducer[T, TopologicalNode, ju.List], Option[FixedPointDetector[T]], Topology) => NetworkRunnable[T])(
    network: Network[T],
    config: NetworkSimulationConfig
  ) = {
    val networkFunction = network.getFunction
    val integratorType = networkFunction.getStatesWeightsIntegratorType
    if (integratorType != null && integratorFactory == null)
      throw new BndNetworkException("States-weights integrator of type '" + integratorType + "' specified for network '" + network.getId() + "' but no integrator provided.")

    val topology = topologyFactory.apply(network.getTopology)
    if (topology.isTemplate)
      throw new BndNetworkException("At this point template the topology should already be initialized.")

    val stateProducer = createStateProducer(topology, networkFunction, false)
    if (!stateProducer.isDefined)
      throw new BndNetworkException("No state producer created for network " + network.getId())

    val fpDetector = fixedPointDetector(config)
    val runnable = createRunnableFun(stateProducer.get, fpDetector, topology)

    val componentIndexMap = runnable.componentIndexMap
    stateProducer.get.setComponentIndexMap(componentIndexMap)

    // set default bias state if provided
    if (network.hasDefaultBiasState)
      topology.getBiasNodes.asScala.foreach(biasNode => runnable.setState(biasNode, network.getDefaultBiasState))

    // set default non bias state if provided
    if (network.hasDefaultNonBiasState)
      topology.getNonBiasNodes.asScala.foreach(nonBiasNode => runnable.setState(nonBiasNode, network.getDefaultNonBiasState))

    // add weights
    if (network.getWeightSetting() != null)
      networkWeightSetter.setWeights(stateProducer.get, network.getWeightSetting)

    (runnable, stateProducer.get)
  }

  private def createStateProducer(
    initializedTopology: Topology,
    networkFunction: NetworkFunction[T],
    predecessorFunctionInherited: Boolean
  ): Option[ComposedStateProducer[T, TopologicalNode, ju.List]] =
    if (initializedTopology.hasLayers)
      createStateProducerForLayers(initializedTopology, networkFunction, predecessorFunctionInherited)
    else if (initializedTopology.hasNodes)
      createStateProducerForNodes(initializedTopology, networkFunction)
    else
      throw new BndNetworkException("Initialized topology must have either layers or nodes, but none of them present.")

  private def createWeightAccessible[S[X]](
    composedStateProducer: ComposedStateProducer[T, TopologicalNode, S]
  ): WeightAccessible[T] = new WeightAccessibleImpl[T](collectInWeightAccessiblesRecursively(composedStateProducer).toMap)

  private def collectInWeightAccessiblesRecursively[S[X]](
    composedStateProducer: ComposedStateProducer[T, TopologicalNode, S]
  ): Iterable[(TopologicalNode, InWeightAccessible[T])] =
    composedStateProducer.listNestedProducers.map(stateProducer =>
      if (stateProducer.isInstanceOf[ComposedStateProducer[T, TopologicalNode, S]])
        collectInWeightAccessiblesRecursively(stateProducer.asInstanceOf[ComposedStateProducer[T, TopologicalNode, S]])
      else if (stateProducer.isInstanceOf[InWeightAccessible[T]])
        List((stateProducer.listOutputComponentsInOrder.head, stateProducer.asInstanceOf[InWeightAccessible[T]]))
      else throw new BndNetworkException("Network state producer of type " + stateProducer.getClass().getName() + " is not expected.")
    ).flatten

  private def createStateProducerForLayers(
    initializedTopology: Topology,
    networkFunction: NetworkFunction[T],
    predecessorFunctionInherited: Boolean
  ): Option[ComposedStateProducer[T, TopologicalNode, ju.List]] = {
    if (!initializedTopology.hasLayers) throw new BndNetworkException("A layered topology expected.")
    var networkFunctions = networkFunction.getLayerFunctions
    if (networkFunctions == null)
      networkFunctions = new ju.ArrayList[NetworkFunction[T]]

    val layerFunctionIterator = networkFunctions.iterator

    val layers = initializedTopology.getLayers.asScala.map(layer =>
      if (predecessorFunctionInherited || !layerFunctionIterator.hasNext)
        createStateProducer(layer, networkFunction, true)
      else
        createStateProducer(layer, layerFunctionIterator.next, false)
    )

    val filteredLayers = layers.flatten
    if (filteredLayers.isEmpty)
      throw new BndNetworkException("No layers created for topology " + initializedTopology.getId())

    Some(ComposedStateProducer.nestedMultiOutputInstance(filteredLayers, networkFunction.getMultiComponentUpdaterType, true))
  }

  private def createStateProducerForNodes(
    initializedTopology: Topology,
    networkFunction: NetworkFunction[T]
  ): Option[ComposedStateProducer[T, TopologicalNode, ju.List]] = {
    if (!initializedTopology.hasNodes) throw new BndNetworkException("A topology with nodes expected.")
    val nodes = initializedTopology.getNodesWithInputs
    if (!nodes.isEmpty)
      Some(createStateProducerForNodes(nodes, networkFunction))
    else None
  }

  private def createStateProducerForNodes(
    nodes: ju.List[TopologicalNode],
    networkFunction: NetworkFunction[T]
  ) = {
    val nodesList = nodes.asScala
    if (nodesList.nonEmpty && nodesList.head.hasLocation)
      Collections.sort(nodes, new TopologicalNodeLocationComparator)

    val statesWeightsIntegrator: Option[StatesWeightsIntegrator[T]] =
      if (integratorFactory != null && networkFunction.getStatesWeightsIntegratorType != null)
        Some(integratorFactory(networkFunction.getStatesWeightsIntegratorType()))
      else None

    val stateProducers = networkFunction match {
      case customNetworkFunction : CustomNetworkFunction[T] =>
        if (statesWeightsIntegrator.isDefined)
          if (customNetworkFunction.activationFunction.isDefined)
            nodesList.map(node => NodeStateProducer(node, statesWeightsIntegrator.get, customNetworkFunction.activationFunction.get))
          else if (networkFunction.getActivationFunctionType != null && activationFunctionFactory.isDefined) {
            if (!customNetworkFunction.perNodeActivationFunctionWithParams.isDefined)
              throw new BndNetworkException("Per node activation function params expected for a custom network function.")

            val params = customNetworkFunction.perNodeActivationFunctionWithParams.get
            nodesList.zip(params).map { case (node, (functionType, params)) =>
              val outputFunction = activationFunctionFactory.get(functionType, Some(params))
              NodeStateProducer(node, statesWeightsIntegrator.get, outputFunction)
            }

          } else
            nodesList.map(node => NodeStateProducer(node, statesWeightsIntegrator.get))

        else if (customNetworkFunction.weightFunction.isDefined)
          nodesList.map(node => NodeStateProducer(node, customNetworkFunction.weightFunction.get))
        else
          throw new BndNetworkException("No state-weight integrator nor custom weight function defined.")

      case _: NetworkFunction[T] =>
        if (networkFunction.getActivationFunctionType != null && statesWeightsIntegrator.isDefined && activationFunctionFactory.isDefined) {
          val activationFunction = activationFunctionFactory.get(
            networkFunction.getActivationFunctionType,
            if (networkFunction.getActivationFunctionParams != null) Some(networkFunction.getActivationFunctionParams.asScala.toSeq) else None)
          nodesList.map(node => NodeStateProducer(node, statesWeightsIntegrator.get, activationFunction))
        } else
          // no output function factory, output function type or SW integrator provided, moving to explicit function
          if (networkFunction.getFunction != null) {
            val functionEvaluator = functionEvaluatorFactory.createInstance(networkFunction.getFunction)
            nodesList.map(node => NodeStateProducer(node, functionEvaluator))
          } else
            nodesList.map(node => NodeStateProducer(node, statesWeightsIntegrator.get))
    }

    ComposedStateProducer.singleOutputInstance(stateProducers, networkFunction.getMultiComponentUpdaterType, true)
  }

  private def fixedPointDetector(simConfig: NetworkSimulationConfig): Option[FixedPointDetector[T]] =
    if (simConfig != null && simConfig.getFixedPointDetectionPeriodicity != null)
      if (simConfig.getFixedPointDetectionPrecision != null) {
        val clazz = ClassUtil.extract[T]
        if (clazz == classOf[jl.Double])
          Some(new DistanceFixedPointDetector[jl.Double](simConfig.getFixedPointDetectionPrecision, simConfig.getFixedPointDetectionPeriodicity).asInstanceOf[FixedPointDetector[T]])
        else
          Some(new StrictFixedPointDetector[T](simConfig.getFixedPointDetectionPeriodicity))
      } else
        Some(new StrictFixedPointDetector[T](simConfig.getFixedPointDetectionPeriodicity))
    else None
}

private[business] class MetaNetworkRunnableFactoryImpl(
  functionEvaluatorFactory: FunctionEvaluatorFactory,
  topologyFactory: TopologyFactory,
  classNetworkWeightSetterMap: ju.Map[Class[_], UntypedNetworkWeightBuilder[_]],
  classIntegratorFactoryMap: ju.Map[Class[_], StatesWeightsIntegratorFactory[_]],
  classOutputFunctionFactoryMap: ju.Map[Class[_], ActivationFunctionFactory[_]]
) extends MetaNetworkRunnableFactory {

  // TODO: cannot use these because Java serialization of type and Scala list fails
  //	private val typeNetworkWeightSetters = classNetworkWeightSetterMap.map{
  //    	case (servedClazz, networkWeightSetter) => (toType(servedClazz), networkWeightSetter)
  //    }
  //
  //	private val typeIntegratorFactories = classIntegratorFactoryMap.map{
  //    	case (servedClazz, integratorFactory) => (toType(servedClazz), integratorFactory)
  //    }

  private val orderedNetworkWeightSetters = classNetworkWeightSetterMap.asScala.toList
  private val orderedIntegratorFactories = classIntegratorFactoryMap.asScala.toList
  private val orderedOutputFunctionFactories = classOutputFunctionFactoryMap.asScala.toList

  override def createInstanceFromClass[T](clazz: Class[T]) = {
    implicit val manifest = toManifest(clazz)
    val inputType = toType(manifest.runtimeClass)

    //        val typeIntegratorFactory = typeIntegratorFactories.find {
    //            case (servedType, integratorFactory) => inputType <:< servedType
    //        }
    //
    //        val typeNetworkWeightSetter = typeNetworkWeightSetters.find {
    //            case (servedType, networkWeightSetter) => inputType <:< servedType
    //        }

    val typeIntegratorFactory = orderedIntegratorFactories.find {
      case (servedClass, integratorFactory) => inputType <:< toType(servedClass)
    }

    val typeNetworkWeightSetter = orderedNetworkWeightSetters.find {
      case (servedClass, networkWeightSetter) => inputType <:< toType(servedClass)
    }

    val typeOutputFunctionFactory = orderedOutputFunctionFactories.find {
      case (servedClass, outputFunctionFactory) => inputType <:< toType(servedClass)
    }

    if (typeIntegratorFactory.isDefined) {
      val integratorFactory = typeIntegratorFactory.get._2
      if (typeNetworkWeightSetter.isDefined) {
        val networkWeightSetter = typeNetworkWeightSetter.get._2
        new NetworkRunnableFactoryImpl[T](
          functionEvaluatorFactory,
          topologyFactory,
          networkWeightSetter.asInstanceOf[UntypedNetworkWeightBuilder[T]],
          integratorFactory.asInstanceOf[StatesWeightsIntegratorFactory[T]],
          if (typeOutputFunctionFactory.isDefined)
            Some(typeOutputFunctionFactory.get._2.asInstanceOf[ActivationFunctionFactory[T]])
          else None
        )
      } else
        throw new BndNetworkException("No network weight setter found to serve the class " + clazz.getName + ".")
    } else
      throw new BndNetworkException("No states-weights integrator found to serve the class " + clazz.getName + ".")
  }
}

object NetworkRunnableFactoryUtil {

  type NetworkRunnable[T] = TimeRunnable with FullStateAccessible[T, TopologicalNode] with ScalaPublisher[StateEvent[T, ju.List]]

  def apply[T](
    clazz: Class[T],
    functionEvaluatorFactory: FunctionEvaluatorFactory,
    topologyFactory: TopologyFactory,
    networkWeightSetter: UntypedNetworkWeightBuilder[T],
    integratorFactory: StatesWeightsIntegratorFactory[T],
    outputFunctionFactory: ActivationFunctionFactory[T]
  ): NetworkRunnableFactory[T] = {
    implicit val manifest = toManifest(clazz)
    new NetworkRunnableFactoryImpl[T](functionEvaluatorFactory, topologyFactory, networkWeightSetter, integratorFactory, Some(outputFunctionFactory))
  }

  def apply[T](
    clazz: Class[T],
    functionEvaluatorFactory: FunctionEvaluatorFactory,
    topologyFactory: TopologyFactory,
    networkWeightSetter: UntypedNetworkWeightBuilder[T],
    integratorFactory: StatesWeightsIntegratorFactory[T]
  ): NetworkRunnableFactory[T] = {
    implicit val manifest = toManifest(clazz)
    new NetworkRunnableFactoryImpl[T](functionEvaluatorFactory, topologyFactory, networkWeightSetter, integratorFactory, None)
  }
}