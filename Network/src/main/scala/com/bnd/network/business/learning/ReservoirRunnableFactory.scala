package com.bnd.network.business.learning

import java.util.Collections
import java.{lang => jl}

import com.bnd.network.business.NetworkRunnableFactoryUtil.NetworkRunnable
import com.bnd.network.business.{MetaNetworkRunnableFactory, TopologyFactory}
import com.bnd.network.domain._
import com.bnd.core.domain.MultiStateUpdateType

import scala.collection.JavaConversions._

class ReservoirRunnableFactory(
    metaNetworkRunnableFactory: MetaNetworkRunnableFactory,
    topologyFactory: TopologyFactory
  ) extends Serializable {

  def apply(
    setting: ReservoirSetting
  ): (NetworkRunnable[jl.Double], Seq[TopologicalNode], Seq[TopologicalNode]) = {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    // Create network
    val network = createTwoLayerReservoirNetwork(setting)

    val topology = topologyFactory(network.getTopology)
    network.setTopology(topology)

    val (networkRunnable, weightAccessor) = doubleNetworkRunnableFactory.createNonInteractiveWeightAccessible(network, new NetworkSimulationConfig)

    val input = topology.getLayers.head
    val reservoir = topology.getLayers.last

    // normalize the reservoir weights for a given spectral radius (if defined)
    if (setting.reservoirSpectralRadius.isDefined) {
      NetworkTrainer.normalizeWeights(reservoir.getAllNodes, weightAccessor, setting.reservoirSpectralRadius.get)
    }

    val inputNodes = input.getNonBiasNodes
    val reservoirNodes = reservoir.getNonBiasNodes

    Collections.sort(inputNodes)
    Collections.sort(reservoirNodes)

    (networkRunnable, inputNodes, reservoirNodes)
  }

  private def createTwoLayerReservoirNetwork(
    setting: ReservoirSetting
  ) = {
    val topology = createTwoLayerReservoirTopology(setting)

    val networkFunction = createTwoLayerReservoirFunction(
      setting.reservoirFunctionType, setting.reservoirFunctionParams, setting.perNodeReservoirFunctionWithParams
    )

    createReservoirNetwork(topology, networkFunction, setting)
  }

  private def createReservoirNetwork(
    topology: Topology,
    networkFunction: NetworkFunction[jl.Double],
    setting: ReservoirSetting
  ) = {
    val networkWeightSetting = new TemplateNetworkWeightSetting[jl.Double]
    networkWeightSetting.setRandomDistribution(setting.weightDistribution)

    val network = new Network[jl.Double]
    network.setDefaultBiasState(setting.bias)
    network.setDefaultNonBiasState(setting.nonBiasInitial)
    network.setTopology(topology)
    network.setFunction(networkFunction)
    network.setWeightSetting(networkWeightSetting)

    network
  }

  private def createTwoLayerReservoirTopology(
    setting: ReservoirSetting
  ) = {
    // layer 1 (input)
    val inputLayer = new TemplateTopology
    inputLayer.setIndex(1)
    inputLayer.setNodesNum(setting.inputNodeNum)

    // layer 2 (reservoir)
    val reservoir: Topology =
      if (setting.reservoirCircularInEdges.isDefined) {
        val reservoir = new SpatialTopology
        reservoir.setIndex(2)
        reservoir.addSize(setting.reservoirNodeNum: jl.Integer)

        val neighborhood = new SpatialNeighborhood

        setting.reservoirCircularInEdges.get.foreach { inEdgeCoordinate =>
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
        reservoir.setNodesNum(setting.reservoirNodeNum)
        reservoir.setAllowSelfEdges(setting.reservoirAllowSelfEdges)
        reservoir.setAllowMultiEdges(setting.reservoirAllowMultiEdges)
        reservoir.setGenerateBias(setting.reservoirBias)

        if (setting.reservoirInDegree.isDefined)
          reservoir.setInEdgesNum(setting.reservoirInDegree.get)

        if (setting.reservoirInDegreeDistribution.isDefined)
          reservoir.setInEdgesDistribution(setting.reservoirInDegreeDistribution.get)

        if (setting.reservoirEdgesNum.isDefined)
          reservoir.setEdgesNum(setting.reservoirEdgesNum.get)

        reservoir.setPreferentialAttachment(setting.reservoirPreferentialAttachment)
        reservoir
      }

    // top-level topology
    val topLevelTopology = new TemplateTopology
    topLevelTopology.addLayer(inputLayer)
    topLevelTopology.addLayer(reservoir)

    //    topLevelTopology.setIntraLayerAllEdges(true)

    val inputReservoirEdgeSpec = new TemplateTopology.IntraLayerEdgeSpec()
    inputReservoirEdgeSpec.setAllowMultiEdges(false)
    //    inputReservoirEdgeSpec.setInEdgesNum(1);
    inputReservoirEdgeSpec.setEdgesNum((setting.inputReservoirConnectivity * setting.reservoirNodeNum * setting.inputNodeNum).toInt)

    topLevelTopology.addIntraLayerEdgeSpec(inputReservoirEdgeSpec)

    topLevelTopology
  }

  private def createTwoLayerReservoirFunction(
    reservoirFunctionType: ActivationFunctionType,
    reservoirFunctionParams: Seq[Double] = Nil,
    perNodeReservoirFunctionWithParams: Option[Stream[(ActivationFunctionType, Seq[jl.Double])]] = None
  ) = {
    val layer1Function = new NetworkFunction[jl.Double]
    layer1Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
    layer1Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum)

    val layer2Function = if (perNodeReservoirFunctionWithParams.isDefined) {
      val layerFunction = new CustomNetworkFunction[jl.Double]
      layerFunction.perNodeActivationFunctionWithParams = perNodeReservoirFunctionWithParams.get
      layerFunction
    } else {
      val layerFunction = new NetworkFunction[jl.Double]
      if (reservoirFunctionParams.nonEmpty)
        layerFunction.setActivationFunctionParams(reservoirFunctionParams.map(x => x: jl.Double))

      layerFunction
    }

    layer2Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
    layer2Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum)
    layer2Function.setActivationFunctionType(reservoirFunctionType)

    val topLevelFunction = new NetworkFunction[jl.Double]
    topLevelFunction.setMultiComponentUpdaterType(MultiStateUpdateType.AsyncFixedOrder)
    topLevelFunction.addLayerFunction(layer1Function)
    topLevelFunction.addLayerFunction(layer2Function)

    topLevelFunction
  }
}