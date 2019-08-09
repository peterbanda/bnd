package com.bnd.network.business.learning

import java.{lang => jl, util => ju}

import com.bnd.core.domain.MultiStateUpdateType
import com.bnd.core.util.{DateTimeUtil, FileUtil}
import com.bnd.math.business.learning._
import com.bnd.math.domain.learning._
import com.bnd.math.domain.rand._
import com.bnd.network.business._
import com.bnd.network.business.learning.NetworkTrainer.NetworkTrainer
import com.bnd.network.domain._
import com.bnd.plotter.Plotter
import com.bnd.core.domain.MultiStateUpdateType
import com.bnd.core.metrics.MetricsFactory
import com.bnd.core.util.{DateTimeUtil, FileUtil}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, _}

/**
 * @author © Peter Banda
 * @since 2012  
 */
class ReservoirLearnerTest extends NetworkTest {

  val dtUtil  = DateTimeUtil.createInstance

  @Autowired
  val metaNetworkRunnableFactory: MetaNetworkRunnableFactory = null

  @Autowired
  val topologyFactory: TopologyFactory = null

  @Autowired
  val doubleMetricsFactory: MetricsFactory[jl.Double] = null

  @Autowired
  val ioStreamFactory: IOStreamFactory = null

  val plotter = Plotter.apply

  val fileUtil = FileUtil.getInstance

  val displayErrors = false

  @Test
  def testReservoirNarma10Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1d)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1d)
    }

    val taskName = "NARMA10"
    val modelName = "RC"
    val inScale = 1d
    val outScale = 1d
    val bias = 1d
    val nonBiasInitial = 0d
    val reservoirNodeNum = 200
    val reservoirInDegree = Some(200)
    val reservoirInDegreeDistribution = None
    val reservoirEdgesNum = None
    val repetitions = 200
    val iterationNum = 801
    val spectralRadius = 0.9
    val weightAdaptationIterationNum = 20
    val variance = 0.006744779

    val reservoirFunctionType = ActivationFunctionType.Tanh
    val readoutFunctionType = null
    val inputDistribution = new UniformDistribution[jl.Double](0d, 0.5)
    val weightDistribution = RandomDistribution.createNormalDistribution(0d, 0.05d)

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createThreeLayerReservoirNetwork(reservoirFunctionType, None, readoutFunctionType)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      reservoirNodeNum,
      reservoirInDegree,
      reservoirInDegreeDistribution,
      reservoirEdgesNum,
      true,
      1d,
      false,
      true,
      false,
      weightDistribution,
      bias,
      nonBiasInitial)

//    def createTrainer = NetworkTrainer.newDeltaRuleNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode], true)

    def createTrainer = NetworkTrainer.newLinearRegressionNetworkTrainerWithWeightAccessible(
      doubleNetworkRunnableFactory
    )(
      mlSetting,
      iterationNum,
      weightAdaptationIterationNum,
      spectralRadius,
      _: IOStream[jl.Double],
      _: Network[jl.Double],
      _: Iterable[TopologicalNode]
    )

    def createIOStream = ioStreamFactory.createNarmaTanhInstance(10, false)(inputDistribution)

    val squareAndSampFutures = (1 to repetitions).map(_ => future {
      val ioStream = createIOStream
      trainNetwork(createNetwork, createTrainer(_, _, _)._1)(ioStream, iterationNum, inScale, outScale)
    })

    val squareAndSampErrors = squareAndSampFutures.map(Await.result(_, 100000 millis))
    val mses = squareAndSampErrors.map(_._1).transpose.map(s => s.sum / s.size)
    val rnmses = mses.map(s => math.sqrt(s / variance))
    val samps = squareAndSampErrors.map(_._2).transpose.map(s => s.sum / s.size)

    println(taskName)
    println(modelName)
    println("Reservoir node #    :" + reservoirNodeNum)
    println("Reservoir in-degree :" + reservoirInDegree)
    println("-------------------")
    println("SAMP:  " + samps)
  }

  /**
   *
   * @param createNetwork
   * @param createTrainer
   * @param ioStream
   * @param dlSize
   * @param inScale
   * @param outScale
   * @return Square and SAMP errors
   */
  private def trainNetworkWithDelayLine(
    createNetwork: IOStream[jl.Double] => Network[jl.Double],
    createTrainer: (IOStream[jl.Double], Network[jl.Double], Iterable[TopologicalNode]) => Trainer[jl.Double, TopologicalNode, ju.List]
  )(
    ioStream: IOStream[jl.Double],
    iterationNum: Int,
    dlSize: Int,
    inScale: Double = 1d,
    outScale: Double = 1d
  ) = {
    // Scale the IO stream
    val ioStreamScaled = ioStream.transformStream({_ * inScale}, {_ * outScale})
    // Adapt the IO stream for DL of given size
    val trainingStream = ioStreamFactory.createDelayLineIOStream[jl.Double](0d: jl.Double)(ioStreamScaled, dlSize)

//    println("IO Stream")
//    println("Input : " + ioStream.inputStream.map(_.head).take(20).toSeq.mkString(","))
//    println("Output: " + ioStream.outputStream.map(_.head).take(20).toSeq.mkString(","))
//
//    println("DL")
//    println("Input : " + trainingStream.inputStream.take(20).toSeq.mkString(","))
//    println("Output: " + trainingStream.outputStream.map(_.head).take(20).toSeq.mkString(","))

    // Train network and get outputs
    val outputs = trainNetworkAndGetOutputs(createNetwork, createTrainer, trainingStream, iterationNum)
    val desiredOutputs = (trainingStream.outputStream take outputs.size).toList.map(_.head)

    // Return error squares
    val squares = (outputs, desiredOutputs).zipped.map { case (y, ye) => (y - ye) * (y - ye)}
    val samps = (outputs, desiredOutputs).zipped.map { case (y, ye) => if (y + ye == 0) 100 else 100d * math.abs(y - ye) / (y + ye)}
    (squares, samps)
  }

  /**
   *
   * @param createNetwork
   * @param createTrainer
   * @param ioStream
   * @param inScale
   * @param outScale
   * @return Square and SAMP errors
   */
  private def trainNetwork(
    createNetwork: IOStream[jl.Double] => Network[jl.Double],
    createTrainer: (IOStream[jl.Double], Network[jl.Double], Iterable[TopologicalNode]) => NetworkTrainer
  )(
    ioStream: IOStream[jl.Double],
    iterationNum: Int,
    inScale: Double = 1d,
    outScale: Double = 1d
  ) = {
    // Scale the IO stream
    val trainingStream = ioStream.transformStream({_ * inScale}, {_ * outScale})

//    println("IO Stream")
//    println("Input : " + trainingStream.inputStream.map(_.head).take(20).toSeq.mkString(","))
//    println("Output: " + trainingStream.outputStream.map(_.head).take(20).toSeq.mkString(","))
//    println("Shift: " + trainingStream.outputShift)

    // Train network and get outputs
    val outputs = trainNetworkAndGetOutputs(createNetwork, createTrainer, trainingStream, iterationNum)
    val desiredOutputs = (trainingStream.outputStream take outputs.size).toList.map(_.head)

//    val plotSetting = new TimeSeriesPlotSetting {
//      title = "Output vs Desired Output"
//      yRangeMax = 0.2
//      yRangeMin = -0.2
//      captions = List("Output", "Desired Output")
//    }
//    plotter.plotSeries(List(outputs.takeRight(50), desiredOutputs.takeRight(50)), plotSetting)

    // Return error squares
    val squares = (outputs, desiredOutputs).zipped.map { case (y, ye) => (y - ye) * (y - ye)}
    val samps = (outputs, desiredOutputs).zipped.map { case (y, ye) => if (y + ye == 0) 100 else 100d * math.abs(y - ye) / (math.abs(y) + math.abs(ye))}
    (squares, samps)
  }

  private def trainNetworkAndGetOutputs(
    createNetwork: IOStream[jl.Double] => Network[jl.Double],
    createTrainer: (IOStream[jl.Double], Network[jl.Double], Iterable[TopologicalNode]) => Trainer[jl.Double, TopologicalNode, ju.List],
    trainingStream: IOStream[jl.Double],
    iterationNum: Int
  ): Seq[jl.Double] = {
    // Create network
    val network = createNetwork(trainingStream)
    val topology = topologyFactory(network.getTopology)
    network.setTopology(topology)

    // Create trainer
    val networkTrainer = createTrainer(trainingStream, network, topology.getLayers.last.getNonBiasNodes)

    // Train the network
    networkTrainer.train(iterationNum)

    // Return outputs
    networkTrainer.outputs
  }

  private def createThreeLayerReservoirNetwork(
    reservoirFunctionType: ActivationFunctionType,
    reservoirFunctionParams: Option[Seq[jl.Double]] = None,
    perNodeReservoirFunctionWithParams: Option[Stream[(ActivationFunctionType, Seq[jl.Double])]] = None
  )(
    inputNum: Int,
    outputNum: Int,
    reservoirNum: Int,
    reservoirInDegree : Option[Int],
    reservoirInDegreeDistribution : Option[RandomDistribution[Integer]],
    reservoirEdgesNum : Option[Int],
    reservoirBias : Boolean,
    inputReservoirConnectivity: Double,
    reservoirPreferentialAttachment : Boolean,
    reservoirAllowSelfEdges : Boolean,
    reservoirAllowMultiEdges : Boolean,
    weightDistribution: RandomDistribution[jl.Double],
    biasState : jl.Double,
    nonBiasState : jl.Double
  ) = {
    val networkWeightSetting = new TemplateNetworkWeightSetting[jl.Double]
    networkWeightSetting.setRandomDistribution(weightDistribution)

    val network = new Network[jl.Double]
    network.setDefaultBiasState(biasState)
    network.setDefaultNonBiasState(nonBiasState)
    network.setTopology(createThreeLayerReservoirTopology(
      inputNum, outputNum, reservoirNum, reservoirInDegree, reservoirInDegreeDistribution, reservoirEdgesNum, reservoirBias, inputReservoirConnectivity, reservoirPreferentialAttachment, reservoirAllowSelfEdges, reservoirAllowMultiEdges))
    network.setFunction(createThreeLayerNeuralNetworkFunction(reservoirFunctionType, reservoirFunctionParams, perNodeReservoirFunctionWithParams))
    network.setWeightSetting(networkWeightSetting)

    network
  }

  private def createThreeLayerReservoirTopology(
    inputNum: Int,
    outputNum: Int,
    reservoirNum: Int,
    reservoirInDegree : Option[Int],
    reservoirInDegreeDistribution : Option[RandomDistribution[Integer]],
    reservoirEdgesNum : Option[Int],
    reservoirBias : Boolean = true,
    inputReservoirConnectivity: Double,
    reservoirPreferentialAttachment : Boolean = false,
    reservoirAllowSelfEdges : Boolean = true,
    reservoirAllowMultiEdges : Boolean = false
  ) = {
    // layer 1
    val inputLayer = new TemplateTopology
    inputLayer.setIndex(1)
    inputLayer.setNodesNum(inputNum)

    // layer 2
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

    //     val sizes = new ju.ArrayList[jl.Integer]
//     sizes.add(16)
//     sizes.add(16)
//
//     val reservoir = new SpatialTopology
//     reservoir.setIndex(2)
//     reservoir.setItsOwnNeighor(false)
//     reservoir.setTorusFlag(true)
//     reservoir.setSizes(sizes)
//     reservoir.setMetricsType(MetricsType.Max)
//     reservoir.setRadius(2)

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

    val inputReservoirEdgeSpec = new TemplateTopology.IntraLayerEdgeSpec();
    inputReservoirEdgeSpec.setAllowMultiEdges(false);
    inputReservoirEdgeSpec.setEdgesNum((inputReservoirConnectivity * reservoirNum).toInt);

    val reservoirOutputEdgeSpec = new TemplateTopology.IntraLayerEdgeSpec();
    reservoirOutputEdgeSpec.setAllEdges(true);

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