package com.bnd.network.business.learning

import java.{lang => jl, util => ju}

import com.bnd.core.domain.MultiStateUpdateType
import com.bnd.math.business.learning.{IOStream, Trainer}
import com.bnd.network.domain.ReservoirLearningSetting
import com.bnd.math.domain.rand.RandomDistribution
import com.bnd.network.business.NetworkRunnableFactoryUtil.NetworkRunnable
import com.bnd.network.business.learning.NetworkTrainer
import com.bnd.network.business.learning.NetworkTrainer.NetworkTrainer
import com.bnd.network.business.{MetaNetworkRunnableFactory, TopologyFactory, WeightAccessible}
import com.bnd.network.domain._
import com.bnd.core.domain.MultiStateUpdateType
import com.bnd.core.metrics.MetricsFactory

import scala.collection.JavaConversions._

class ReservoirTrainerFactory(
    metaNetworkRunnableFactory: MetaNetworkRunnableFactory,
    topologyFactory: TopologyFactory,
    doubleMetricsFactory: MetricsFactory[jl.Double]
  ) extends Serializable {

  def apply(
    setting: ReservoirLearningSetting,
    ioStream: IOStream[jl.Double],
    iterationNum: Int
  ): (NetworkTrainer, WeightAccessible[jl.Double]) = {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    // Scale the IO stream
    val trainingStream = ioStream.transformStream({_ * setting.getInScale}, {_ * setting.getOutScale})

    // Create network
    val network = createThreeLayerReservoirNetwork(setting.getReservoirFunctionType, setting.getReservoirFunctionParams, setting.getPerNodeReservoirFunctionWithParams)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      setting.getReservoirNodeNum,
      setting.getReservoirInDegree,
      setting.getReservoirInDegreeDistribution,
      setting.getReservoirEdgesNum,
      setting.getReservoirBias,
      setting.getReservoirCircularInEdges,
      setting.getInputReservoirConnectivity,
      setting.getReservoirPreferentialAttachment,
      true,
      false,
      setting.getWeightDistribution,
      setting.getBias,
      setting.getNonBiasInitial)

    val topology = topologyFactory(network.getTopology)
    network.setTopology(topology)

    NetworkTrainer.newLinearRegressionNetworkTrainerWithWeightAccessible(
      doubleNetworkRunnableFactory
    )(
      setting,
      iterationNum,
      setting.getWeightAdaptationIterationNum,
      setting.getReservoirSpectralRadius,
      trainingStream,
      network,
      topology.getLayers.last.getNonBiasNodes
    )
  }

  def apply(
    topology: Topology,
    setting: ReservoirLearningSetting,
    ioStream: IOStream[jl.Double],
    iterationNum: Int
  ): (NetworkTrainer, WeightAccessible[jl.Double]) = {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    // Scale the IO stream
    val trainingStream = ioStream.transformStream({_ * setting.getInScale}, {_ * setting.getOutScale})

    // Create network
    val network = createReservoirNetwork(
      setting.getReservoirFunctionType,
      setting.getReservoirFunctionParams,
      setting.getPerNodeReservoirFunctionWithParams,
      topology,
      setting.getWeightDistribution,
      setting.getBias,
      setting.getNonBiasInitial)

    NetworkTrainer.newLinearRegressionNetworkTrainerWithWeightAccessible(
      doubleNetworkRunnableFactory
    )(
      setting,
      iterationNum,
      setting.getWeightAdaptationIterationNum,
      setting.getReservoirSpectralRadius,
      trainingStream,
      network,
      topology.getLayers.last.getNonBiasNodes
    )
  }

  private def createThreeLayerReservoirNetwork(
    reservoirFunctionType: ActivationFunctionType,
    reservoirFunctionParams: Option[Seq[jl.Double]] = None,
    perNodeReservoirFunctionWithParams: Option[Stream[(ActivationFunctionType, Seq[jl.Double])]] = None
  )(
    inputNum: Int,
    outputNum: Int,
    reservoirNum: Int,
    reservoirInDegree: Option[Int],
    reservoirInDegreeDistribution: Option[RandomDistribution[Integer]],
    reservoirEdgesNum: Option[Int],
    reservoirBias: Boolean,
    reservoirCircularInEdges: Option[Seq[Int]],
    inputReservoirConnectivity: Double,
    reservoirPreferentialAttachment: Boolean,
    reservoirAllowSelfEdges: Boolean,
    reservoirAllowMultiEdges: Boolean,
    weightDistribution: RandomDistribution[jl.Double],
    biasState: jl.Double,
    nonBiasState: jl.Double
  ) = {
    val topology = createThreeLayerReservoirTopology(
      inputNum,
      outputNum,
      reservoirNum,
      reservoirInDegree,
      reservoirInDegreeDistribution,
      reservoirEdgesNum,
      reservoirBias,
      reservoirCircularInEdges,
      inputReservoirConnectivity,
      reservoirPreferentialAttachment,
      reservoirAllowSelfEdges,
      reservoirAllowMultiEdges
    )

    createReservoirNetwork(reservoirFunctionType, reservoirFunctionParams, perNodeReservoirFunctionWithParams, topology, weightDistribution, biasState, nonBiasState)
  }

  private def createReservoirNetwork(
    reservoirFunctionType: ActivationFunctionType,
    reservoirFunctionParams: Option[Seq[jl.Double]] = None,
    perNodeReservoirFunctionWithParams: Option[Stream[(ActivationFunctionType, Seq[jl.Double])]] = None,
    topology: Topology,
    weightDistribution: RandomDistribution[jl.Double],
    biasState: jl.Double,
    nonBiasState: jl.Double
  ) = {
    val networkWeightSetting = new TemplateNetworkWeightSetting[jl.Double]
    networkWeightSetting.setRandomDistribution(weightDistribution)

    val network = new Network[jl.Double]
    network.setDefaultBiasState(biasState)
    network.setDefaultNonBiasState(nonBiasState)
    network.setTopology(topology)
    network.setFunction(createThreeLayerNeuralNetworkFunction(reservoirFunctionType, reservoirFunctionParams, perNodeReservoirFunctionWithParams))
    network.setWeightSetting(networkWeightSetting)

    network
  }

  def createThreeLayerReservoirTopology(
    inputNum: Int,
    outputNum: Int,
    reservoirNum: Int,
    reservoirInDegree: Option[Int],
    reservoirInDegreeDistribution: Option[RandomDistribution[Integer]],
    reservoirEdgesNum: Option[Int],
    reservoirBias: Boolean = true,
    reservoirCircularInEdges: Option[Seq[Int]] = None,
    inputReservoirConnectivity: Double,
    reservoirPreferentialAttachment: Boolean = false,
    reservoirAllowSelfEdges: Boolean = true,
    reservoirAllowMultiEdges: Boolean = false
  ) = {
    // layer 1
    val inputLayer = new TemplateTopology
    inputLayer.setIndex(1)
    inputLayer.setNodesNum(inputNum)

    // layer 2
    val reservoir: Topology =
      if (reservoirCircularInEdges.isDefined) {
        val reservoir = new SpatialTopology
        reservoir.setIndex(2)
        reservoir.addSize(reservoirNum: jl.Integer)

        val neighborhood = new SpatialNeighborhood

        reservoirCircularInEdges.get.foreach { inEdgeCoordinate =>
          val neighbor = new SpatialNeighbor
          neighbor.addCoordinateDiff(inEdgeCoordinate: jl.Integer)
          neighborhood.addNeighbor(neighbor)
        }

        reservoir.setNeighborhood(neighborhood)
        reservoir.setTorusFlag(true)

        reservoir
      } else {
        val reservoir = new TemplateTopology
        reservoir.setIndex(2)
        reservoir.setNodesNum(reservoirNum)
        reservoir.setAllowSelfEdges(reservoirAllowSelfEdges)
        reservoir.setAllowMultiEdges(reservoirAllowMultiEdges)
        reservoir.setGenerateBias(reservoirBias)

        if (reservoirInDegree.isDefined)
          reservoir.setInEdgesNum(reservoirInDegree.get)
        if (reservoirInDegreeDistribution.isDefined)
          reservoir.setInEdgesDistribution(reservoirInDegreeDistribution.get)
        if (reservoirEdgesNum.isDefined)
          reservoir.setEdgesNum(reservoirEdgesNum.get)
        reservoir.setPreferentialAttachment(reservoirPreferentialAttachment)

        reservoir
      }

    // layer 3
    val readout = new TemplateTopology
    readout.setIndex(3)
    readout.setNodesNum(outputNum)
    readout.setGenerateBias(true)

    // top-level topology
    val topLevelTopology = new TemplateTopology
    topLevelTopology.addLayer(inputLayer)
    topLevelTopology.addLayer(reservoir)
    topLevelTopology.addLayer(readout)

    //    topLevelTopology.setIntraLayerAllEdges(true)

    val inputReservoirEdgeSpec = new TemplateTopology.IntraLayerEdgeSpec()
    inputReservoirEdgeSpec.setAllowMultiEdges(false)
    //    inputReservoirEdgeSpec.setInEdgesNum(1);
    inputReservoirEdgeSpec.setEdgesNum((inputReservoirConnectivity * reservoirNum * inputNum).toInt)

    val reservoirOutputEdgeSpec = new TemplateTopology.IntraLayerEdgeSpec()
    reservoirOutputEdgeSpec.setAllEdges(true)

    topLevelTopology.addIntraLayerEdgeSpec(inputReservoirEdgeSpec)
    topLevelTopology.addIntraLayerEdgeSpec(reservoirOutputEdgeSpec)

    topLevelTopology
  }

  private def createThreeLayerNeuralNetworkFunction(
    layer2FunctionType: ActivationFunctionType,
    layer2FunctionParams: Option[Seq[jl.Double]] = None,
    perNodeLayer2FunctionWithParams: Option[Stream[(ActivationFunctionType, Seq[jl.Double])]] = None
  ) = {
    val layer1Function = new NetworkFunction[jl.Double]
    layer1Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
    layer1Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum)

    val layer2Function = if (perNodeLayer2FunctionWithParams.isDefined) {
      val layerFunction = new CustomNetworkFunction[jl.Double]
      layerFunction.perNodeActivationFunctionWithParams = perNodeLayer2FunctionWithParams.get
      layerFunction
    } else {
      val layerFunction = new NetworkFunction[jl.Double]
      if (layer2FunctionParams.isDefined)
        layerFunction.setActivationFunctionParams(layer2FunctionParams.get)
      layerFunction
    }

    layer2Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
    layer2Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum)
    layer2Function.setActivationFunctionType(layer2FunctionType)

    val layer3Function = new NetworkFunction[jl.Double]
    layer3Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
    layer3Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum)

    val topLevelFunction = new NetworkFunction[jl.Double]
    topLevelFunction.setMultiComponentUpdaterType(MultiStateUpdateType.AsyncFixedOrder)
    topLevelFunction.addLayerFunction(layer1Function)
    topLevelFunction.addLayerFunction(layer2Function)
    topLevelFunction.addLayerFunction(layer3Function)

    topLevelFunction
  }
}