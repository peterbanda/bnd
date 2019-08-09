package com.bnd.network.business.learning

import java.{lang => jl, util => ju}

import com.bnd.core.metrics.{MetricsFactory, MetricsType}
import com.bnd.core.util.{DateTimeUtil, FileUtil, RandomUtil}
import com.bnd.math.business.learning._
import com.bnd.math.business.rand.RandomDistributionProviderFactory
import com.bnd.math.domain.learning.MachineLearningSetting.LearningRateAnnealingType
import com.bnd.math.domain.learning._
import com.bnd.math.domain.rand._
import com.bnd.network.business._
import com.bnd.network.domain._
import com.bnd.plotter.{Plotter, SeriesPlotSetting}
import com.bnd.core.domain.MultiStateUpdateType
import com.bnd.core.metrics.MetricsFactory
import com.bnd.core.util.{DateTimeUtil, FileUtil, RandomUtil}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, _}
import scala.concurrent.duration._

/**
 * @author © Peter Banda
 * @since 2012  
 */
class MachineLearnerTest extends NetworkTest {

  //	@Autowired
  //	val doubleNetworkRunnableFactory : NetworkRunnableFactory[jl.Double] = null

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

  //	@Test
  //	def testLMSNetworkEasyFunLearning() {
  //		val mlSetting = createMachineLearningSetting
  //		val trainingStream = createIOStream(createTrainingSet, mlSetting.getSelectionType)
  //
  //		val networkBO = doubleNetworkFactory.createNetworkBO(
  //				createTwoLayerDoubleNetwork(trainingStream.inputDim, trainingStream.outputDim)).asInstanceOf[LayeredNetworkBO[jl.Double]]
  //
  //		val lmsNetworkLearningBO = new LMSNetworkLearningBO(networkBO, doubleMetricsFactory, mlSetting).asInstanceOf[Trainable[jl.Double]]
  //		reportLayeredNetwork(networkBO)
  //
  //		lmsNetworkLearningBO.train(trainingStream)
  //
  //		plotter.plotSingleTimeSeries(lmsNetworkLearningBO.errors, "Errors", true)
  //	}

  class BinaryFunction(val name: String, val outputs: Int*) {
    def doubleOutputs = outputs.map(_.toDouble: jl.Double)
  }

  val binaryFunctions = Seq(
    new BinaryFunction("FALSE", 0, 0, 0, 0),
    new BinaryFunction("NOR", 0, 0, 0, 1),
    new BinaryFunction("NCIMPL", 0, 0, 1, 0),
    new BinaryFunction("NOT X1", 0, 0, 1, 1),
    new BinaryFunction("NIMPL", 0, 1, 0, 0),
    new BinaryFunction("NOT X2", 0, 1, 0, 1),
    new BinaryFunction("XOR", 0, 1, 1, 0),
    new BinaryFunction("NAND", 0, 1, 1, 1),
    new BinaryFunction("AND", 1, 0, 0, 0),
    new BinaryFunction("NXOR", 1, 0, 0, 1),
    new BinaryFunction("PROJ X2", 1, 0, 1, 0),
    new BinaryFunction("IMPL", 1, 0, 1, 1),
    new BinaryFunction("PROJ X1", 1, 1, 0, 0),
    new BinaryFunction("CIMPL", 1, 1, 0, 1),
    new BinaryFunction("OR", 1, 1, 1, 0),
    new BinaryFunction("TRUE", 1, 1, 1, 1))

  private def twoInputLinearAnalogFunction(
    k1 : Double, k2 : Double, k0 : Double
  )(xs: Iterable[jl.Double]) = k1 * xs.head + k2 * xs.tail.head + k0

  private def twoInputQuadraticAnalogFunction(
    k : Double, k0 : Double
  )(xs: Iterable[jl.Double]) = k * (xs.head + xs.tail.head) + k0

  def testSingleNNPerceptronBinaryFunctionLearning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1D)
      setInitialLearningRate(0.1)
      setLearningAnnealingRate(0d)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1D)
    }

    val outputFunctionType = ActivationFunctionType.Threshold
    val outputFunctionParams = List[jl.Double](0d, 0d, 1d)
    val bias = 1d
    val repetitions = 10000
    val inputSize = 2
    val inputDistribution: RandomDistribution[jl.Double] = new UniformDiscreteDistribution[jl.Double](Array[jl.Double](0d, 1d))
    val weightDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](-1d, 1d)

    def binFun(truthTableOutputs: Seq[jl.Double])(xs: Iterable[jl.Double]) = {
      val twoPows = xs.scanLeft(1) { case (a, b) => 2 * a}.toSeq.reverse.tail
      val index = (twoPows, xs).zipped.map { case (a, b) => a * b.toInt}.sum
      truthTableOutputs(index)
    }

    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType, Some(outputFunctionParams))(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    val successRateStrings = binaryFunctions.map { binFunction =>
      def fun = binFun(binFunction.doubleOutputs.reverse) _
      println(binFunction.name)
      println(binFunction.outputs.mkString(","))
      print(fun(Seq(1d, 1d)).toInt + ",")
      print(fun(Seq(1d, 0d)).toInt + ",")
      print(fun(Seq(0d, 1d)).toInt + ",")
      println(fun(Seq(0d, 0d)).toInt)

      val absThresholdErrors = (1 to repetitions).map(_ => future {
        trainNetworkForStaticFunAbsError(createNetwork, createTrainer)(fun, inputDistribution, inputSize, 201)
      })

      val meanAbsErrors = absThresholdErrors.map(Await.result(_, 100000 millis)).transpose.map(s => s.sum / s.size)
      val successRates = meanAbsErrors.map(1 - _)

      if (displayErrors) {
        val plotSetting = new SeriesPlotSetting()
            .setTitle("Errors")
            .setYRangeMax(1)
            .setYRangeMin(0)

        plotter.plotSingleSeries(meanAbsErrors, plotSetting)
      }

      println("Last error:" + meanAbsErrors.last)
      binFunction.name + "," + successRates.mkString(",")
    }
    fileUtil.overwriteStringToFileSafe(successRateStrings.mkString("\n"), "Perceptron_BinFuns_200")
  }

  def testNNLPAnalogFunctionLearning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1D)
      setInitialLearningRate(0.7)
      setLearningAnnealingRate(0.7 / 800d)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1D)
    }

    val outputFunctionType = null
    val bias = 0.5d
    val repetitions = 10000
    val inputSize = 2
    val inputDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.2d, 1d)
    val weightDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](-0.5d, 0.5d)

    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType, None)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def linearFun(ks : Iterable[jl.Double], k0 : jl.Double)(xs: Iterable[jl.Double]) : jl.Double = (xs, ks).zipped.map(_*_).sum + k0
    def prodFun(k : Double, k0 : Double)(xs: Iterable[jl.Double]) : jl.Double  = k * xs.reduce(_*_) + k0

    val ksRdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0.2, 0.8))
    val k2NegRdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0d, 0.3))
    val k0Rdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0.1d, 0.4))
    val k0NegRdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0.3d, 0.4))

    def fun1  = ("k1x1+k2x2+k0", 0.05896666666667, {() => linearFun(Seq(ksRdp.next, ksRdp.next), k0Rdp.next)_})
    def fun2  = ("k1x1-k2x2+k0", 0.03086666666667, {() => linearFun(Seq(ksRdp.next, -k2NegRdp.next), k0NegRdp.next)_})
    def fun3  = ("k1x1x2+k0",    0.01543644444444, {() => prodFun(ksRdp.next, 0.25d)_})
    def fun4  = ("k1x1",         0.02573333333333, {() => linearFun(Seq(ksRdp.next, 0d), 0d)_})
    def fun5  = ("k2x2",         0.02573333333333, {() => linearFun(Seq(0d, ksRdp.next), 0d)_})
    def fun6  = ("k0",           0.0075,           {() => linearFun(Seq(0d, 0d), k0Rdp.next)_})

    val analogFuns = Seq(fun1, fun2, fun3, fun4, fun5, fun6)

    val successRateStrings = analogFuns.map { analogFunctionInfo =>
      val name = analogFunctionInfo._1
      val variance = analogFunctionInfo._2

      val squareAndSampFutures = (1 to repetitions).map(_ => future {
        def fun = analogFunctionInfo._3()
        trainNetworkForStaticFunSquareAndSampError(createNetwork, createTrainer)(fun, inputDistribution, inputSize, 801)
      })

      val squareAndSampErrors = squareAndSampFutures.map(Await.result(_, 100000 millis))
      val mses = squareAndSampErrors.map(_._1).transpose.map(s => s.sum / s.size)
      val rnmses = mses.map(s => math.sqrt(s / variance))
      val samps = squareAndSampErrors.map(_._2).transpose.map(s => s.sum / s.size)

      println(name)
      println("Learning Rate: " + mlSetting.getInitialLearningRate)
      println("----------------------")
      println("RNMSE        : " + rnmses.last)
      println("SAMP         : " + samps.last)
      println

      name + ", " + rnmses.last + ", " + samps.last
    }
    fileUtil.overwriteStringToFileSafe(successRateStrings.mkString("\n"), "NN_LP_AnalogFuns_alpha-lin_" + mlSetting.getInitialLearningRate)
  }

  def testNNLinearRegressionAnalogFunctionLearning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1D)
      setInitialLearningRate(0d)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setLearningAnnealingRate(0d)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1D)
    }

    val outputFunctionType = null
    val bias = 0.5d
    val repetitions = 10000
    val iterationNum = 801
    val inputSize = 2
    val inputDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.2d, 1d)
    val weightDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](-0.5d, 0.5d)

    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType, None)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newPlainLinearRegressionNetworkTrainer(doubleNetworkRunnableFactory)(
      mlSetting, iterationNum, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode]
    )

    def linearFun(ks : Iterable[jl.Double], k0 : jl.Double)(xs: Iterable[jl.Double]) : jl.Double = (xs, ks).zipped.map(_*_).sum + k0
    def prodFun(k : Double, k0 : Double)(xs: Iterable[jl.Double]) : jl.Double  = k * xs.reduce(_*_) + k0

    val ksRdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0.2, 0.8))
    val k2NegRdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0d, 0.3))
    val k0Rdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0.1d, 0.4))
    val k0NegRdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0.3d, 0.4))

    def fun1  = ("k1x1+k2x2+k0", 0.05896666666667, {() => linearFun(Seq(ksRdp.next, ksRdp.next), k0Rdp.next)_})
    def fun2  = ("k1x1-k2x2+k0", 0.03086666666667, {() => linearFun(Seq(ksRdp.next, -k2NegRdp.next), k0NegRdp.next)_})
    def fun3  = ("k1x1x2+k0",    0.01543644444444, {() => prodFun(ksRdp.next, 0.25d)_})
    def fun4  = ("k1x1",         0.02573333333333, {() => linearFun(Seq(ksRdp.next, 0d), 0d)_})
    def fun5  = ("k2x2",         0.02573333333333, {() => linearFun(Seq(0d, ksRdp.next), 0d)_})
    def fun6  = ("k0",           0.0075,           {() => linearFun(Seq(0d, 0d), k0Rdp.next)_})

    val analogFuns = Seq(fun1, fun2, fun3, fun4, fun5, fun6)

    val successRateStrings = analogFuns.map { analogFunctionInfo =>
      val name = analogFunctionInfo._1
      val variance = analogFunctionInfo._2

      val squareAndSampFutures = (1 to repetitions).map(_ => future {
        def fun = analogFunctionInfo._3()
        trainNetworkForStaticFunSquareAndSampError(createNetwork, createTrainer)(fun, inputDistribution, inputSize, 801)
      })

      val squareAndSampErrors = squareAndSampFutures.map(Await.result(_, 100000 millis))
      val mses = squareAndSampErrors.map(_._1).transpose.map(s => s.sum / s.size)
      val rnmses = mses.map(s => math.sqrt(s / variance))
      val samps = squareAndSampErrors.map(_._2).transpose.map(s => s.sum / s.size)

      println(name)
      println("Learning Rate: " + mlSetting.getInitialLearningRate)
      println("----------------------")
      println("RNMSE        : " + rnmses.last)
      println("SAMP         : " + samps.last)
      println

      name + ", " + rnmses.last + ", " + samps.last
    }
    fileUtil.overwriteStringToFileSafe(successRateStrings.mkString("\n"), "NN_LRegression_AnalogFuns")
  }

  def testChemicalLPAnalogFunctionLearning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1D)
      setInitialLearningRate(0.1)
      setLearningAnnealingRate(0.0075)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1D)
    }

    val bias = 0.5d
    val repetitions = 10000
    val iterationNum = 801
    val inputSize = 2
    val inputDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.2d, 1d)
    val weightDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.5, 2d)

    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerChemicalNetwork(linearChemDotProduct)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleChemicalNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def linearFun(ks : Iterable[jl.Double], k0 : jl.Double)(xs: Iterable[jl.Double]) : jl.Double = (xs, ks).zipped.map(_*_).sum + k0
    def prodFun(k : Double, k0 : Double)(xs: Iterable[jl.Double]) : jl.Double  = k * xs.reduce(_*_) + k0

    val ksRdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0.2, 0.8))
    val k2NegRdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0d, 0.3))
    val k0Rdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0.1d, 0.4))
    val k0NegRdp = RandomDistributionProviderFactory(new UniformDistribution[jl.Double](0.3d, 0.4))

    def fun1  = ("k1x1+k2x2+k0", 0.05896666666667, {() => linearFun(Seq(ksRdp.next, ksRdp.next), k0Rdp.next)_})
    def fun2  = ("k1x1-k2x2+k0", 0.03086666666667, {() => linearFun(Seq(ksRdp.next, -k2NegRdp.next), k0NegRdp.next)_})
    def fun3  = ("k1x1x2+k0",    0.01543644444444, {() => prodFun(ksRdp.next, 0.25d)_})
    def fun4  = ("k1x1",         0.02573333333333, {() => linearFun(Seq(ksRdp.next, 0d), 0d)_})
    def fun5  = ("k2x2",         0.02573333333333, {() => linearFun(Seq(0d, ksRdp.next), 0d)_})
    def fun6  = ("k0",           0.0075,           {() => linearFun(Seq(0d, 0d), k0Rdp.next)_})

    val analogFuns = Seq(fun1, fun2, fun3, fun4, fun5, fun6)

    val successRateStrings = analogFuns.map { analogFunctionInfo =>
      val name = analogFunctionInfo._1
      val variance = analogFunctionInfo._2

      val squareAndSampFutures = (1 to repetitions).map(_ => future {
        def fun = analogFunctionInfo._3()
        trainNetworkForStaticFunSquareAndSampError(createNetwork, createTrainer)(fun, inputDistribution, inputSize, iterationNum)
      })

      val squareAndSampErrors = squareAndSampFutures.map(Await.result(_, 100000 millis))
      val mses = squareAndSampErrors.map(_._1).transpose.map(s => s.sum / s.size)
      val rnmses = mses.map(s => math.sqrt(s / variance))
      val samps = squareAndSampErrors.map(_._2).transpose.map(s => s.sum / s.size)

      println(name)
      println("Learning Rate: " + mlSetting.getInitialLearningRate)
      println("----------------------")
      println("RNMSE        : " + rnmses.last)
      println("SAMP         : " + samps.last)
      println

      name + ", " + rnmses.last + ", " + samps.last
    }
    fileUtil.overwriteStringToFileSafe(successRateStrings.mkString("\n"), "CH_LP_AnalogFuns_gamma_" + mlSetting.getInitialLearningRate + "-anneal_" + mlSetting.getLearningAnnealingRate)
  }

  @Test
  def testChemicalLPNarma2Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1D)
      setInitialLearningRate(0.05)
      setLearningAnnealingRate(0.001)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1D)
    }

    val taskName = "NARMA2"
    val modelName = "LP"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.1
    val repetitions = 10000
    val variance = 0.040869268 / 4
    val iterationNum = 801

    val inputDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0d, 0.5)
    val weightDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.5, 2d)

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerChemicalNetwork(linearChemDotProduct)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleChemicalNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def createIOStream = ioStreamFactory.createNarmaInstance(2, true)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  private def wmmFun(k: Double, k0: Double)(xs: Iterable[jl.Double]): jl.Double = k * xs.foldLeft(0d) { case (a, b) => math.max(a, b)} + k0

  @Test
  def testChemicalLPWMM2Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1D)
      setInitialLearningRate(0.05)
      setLearningAnnealingRate(0.001)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1D)
    }

    val taskName = "WMM2"
    val modelName = "LP"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.5
    val repetitions = 10000
    val variance = 0.033598965
    val wmmOrder = 2
    val iterationNum = 801

    val inputDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.2, 1d)
    val weightDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.5, 2d)
    val kRDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.2, 0.8))
    val k0RDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.1, 0.4))

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerChemicalNetwork(linearChemDotProduct)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleChemicalNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def createIOStream = ioStreamFactory.createInstance1D(wmmFun(kRDP.next, k0RDP.next) _)(0d, wmmOrder, 0)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  private def lwmaFun(ks: Iterable[Double], k0: Double)(xs: Iterable[jl.Double]): jl.Double = (ks, xs).zipped.map(_ * _).sum + k0

  @Test
  def testChemicalLPLWMA2Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1D)
      setInitialLearningRate(0.05)
      setLearningAnnealingRate(0.001)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1D)
    }

    val taskName = "LWMA2"
    val modelName = "LP"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.5
    val repetitions = 10000
    val variance = 0.05896666666667
    val lwmaOrder = 2
    val iterationNum = 801

    val inputDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.2, 1d)
    val weightDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.5, 2d)
    val ksRDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.2, 0.8))
    val k0RDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.1, 0.4))

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerChemicalNetwork(linearChemDotProduct)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleChemicalNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def createIOStream = ioStreamFactory.createInstance1D(lwmaFun(ksRDP.nextList(lwmaOrder), k0RDP.next) _)(0d, lwmaOrder, 0)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  @Test
  def testChemicalLPNarma10Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1D)
      setInitialLearningRate(0.05)
      setLearningAnnealingRate(0.001)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1D)
    }

    val taskName = "NARMA10"
    val modelName = "LP"
    val inScale = 1d
    val outScale = 1
    val bias = 0.1
    val repetitions = 10000
    val variance = 0.006744779
    val iterationNum = 801

    val inputDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0d, 0.5)
    val weightDistribution: RandomDistribution[jl.Double] = new UniformDistribution[jl.Double](0.5, 2d)

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerChemicalNetwork(linearChemDotProduct)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleChemicalNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def createIOStream = ioStreamFactory.createNarmaTanhInstance(10, true)(inputDistribution)

     // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  // NN

  def testNeuralNetworkNarma10Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1d)
      setInitialLearningRate(0.5)
      setLearningAnnealingRate(0.5 / 800d)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1d)
    }

    val taskName = "NARMA10"
    val modelName = "NN"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.1
    val repetitions = 10000
    val variance = 0.006744779
    val iterationNum = 801

    val outputFunctionType = null // ActivationFunctionType.Sigmoid
    val inputDistribution = new UniformDistribution[jl.Double](0d, 0.5)
    val weightDistribution = new UniformDistribution[jl.Double](-0.5d, 0.5d)

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])
//    def createTrainer = NetworkTrainer.newLinearRegressionNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def createIOStream = ioStreamFactory.createNarmaTanhInstance(10, true)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  def testNeuralNetworkNarma2Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1d)
      setInitialLearningRate(0.5)
      setLearningAnnealingRate(0.5 / 800d)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1d)
    }

    val taskName = "NARMA2"
    val modelName = "NN"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.1
    val repetitions = 10000
    val variance = 0.040869268 / 4
    val iterationNum = 801

    val outputFunctionType = null // ActivationFunctionType.Sigmoid
    val inputDistribution = new UniformDistribution[jl.Double](0d, 0.5)
    val weightDistribution = new UniformDistribution[jl.Double](-0.5d, 0.5d)

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def createIOStream = ioStreamFactory.createNarmaInstance(2, true)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  def testNeuralNetworkLWMA2Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1d)
      setInitialLearningRate(0.5)
      setLearningAnnealingRate(0.5 / 800d)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1d)
    }

    val taskName = "LWMA2"
    val modelName = "NN"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.5
    val repetitions = 10000
    val variance = 0.05896666666667
    val lwmaOrder = 2
    val iterationNum = 801

    val outputFunctionType = null // ActivationFunctionType.Sigmoid
    val inputDistribution = new UniformDistribution[jl.Double](0.2, 1d)
    val weightDistribution = new UniformDistribution[jl.Double](-0.5d, 0.5d)
    val ksRDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.2, 0.8))
    val k0RDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.1, 0.4))

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def createIOStream = ioStreamFactory.createInstance1D(lwmaFun(ksRDP.nextList(lwmaOrder), k0RDP.next) _)(0d, lwmaOrder, 0)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  def testNeuralNetworkWMM2Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1d)
      setInitialLearningRate(0.5)
      setLearningAnnealingRate(0.5 / 800d)
      setLearningAnnealingType(LearningRateAnnealingType.Linear)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1d)
    }

    val taskName = "WMM2"
    val modelName = "NN"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.5
    val repetitions = 10000
    val variance = 0.033598965
    val wmmOrder = 2
    val iterationNum = 801

    val outputFunctionType = null // ActivationFunctionType.Sigmoid
    val inputDistribution = new UniformDistribution[jl.Double](0.2, 1d)
    val weightDistribution = new UniformDistribution[jl.Double](-0.5d, 0.5d)
    val kRDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.2, 0.8))
    val k0RDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.1, 0.4))

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newDeltaRuleNetworkTrainer(doubleNetworkRunnableFactory)(mlSetting, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode])

    def createIOStream = ioStreamFactory.createInstance1D(wmmFun(kRDP.next, k0RDP.next) _)(0d, wmmOrder, 0)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  // Linear regression

  def testNNLinearRegressionNarma10Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1d)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1d)
    }

    val taskName = "NARMA10"
    val modelName = "LR"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.1
    val repetitions = 10000
    val variance = 0.006744779
    val iterationNum = 801

    val outputFunctionType = null // ActivationFunctionType.Sigmoid
    val inputDistribution = new UniformDistribution[jl.Double](0d, 0.5)
    val weightDistribution = new UniformDistribution[jl.Double](-0.5d, 0.5d)

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newPlainLinearRegressionNetworkTrainer(doubleNetworkRunnableFactory)(
      mlSetting, iterationNum, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode]
    )

    def createIOStream = ioStreamFactory.createNarmaTanhInstance(10, true)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  def testNNLinearRegressionNarma2Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1d)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1d)
    }

    val taskName = "NARMA2"
    val modelName = "LR"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.1
    val repetitions = 10000
    val variance = 0.040869268 / 4
    val iterationNum = 801

    val outputFunctionType = null // ActivationFunctionType.Sigmoid
    val inputDistribution = new UniformDistribution[jl.Double](0d, 0.5)
    val weightDistribution = new UniformDistribution[jl.Double](-0.5d, 0.5d)

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newPlainLinearRegressionNetworkTrainer(doubleNetworkRunnableFactory)(
      mlSetting, iterationNum, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode]
    )

    def createIOStream = ioStreamFactory.createNarmaInstance(2, true)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  def testNNLinearRegressionLWMA2Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1d)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1d)
    }

    val taskName = "LWMA2"
    val modelName = "LR"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.5
    val repetitions = 10000
    val variance = 0.05896666666667
    val lwmaOrder = 2
    val iterationNum = 801

    val outputFunctionType = null // ActivationFunctionType.Sigmoid
    val inputDistribution = new UniformDistribution[jl.Double](0.2, 1d)
    val weightDistribution = new UniformDistribution[jl.Double](-0.5d, 0.5d)
    val ksRDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.2, 0.8))
    val k0RDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.1, 0.4))

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newPlainLinearRegressionNetworkTrainer(doubleNetworkRunnableFactory)(
      mlSetting, iterationNum, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode]
    )

    def createIOStream = ioStreamFactory.createInstance1D(lwmaFun(ksRDP.nextList(lwmaOrder), k0RDP.next) _)(0d, lwmaOrder, 0)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  def testNNLinearRegressionWMM2Learning() {
    val doubleNetworkRunnableFactory = metaNetworkRunnableFactory.createInstance[jl.Double]

    val mlSetting = new MachineLearningSetting {
      setSingleIterationLength(1d)
      setInitialDelay(0d)
      setInputTimeLength(1d)
      setOutputInterpretationRelativeTime(1d)
    }

    val taskName = "WMM2"
    val modelName = "LR"
    val inScale = 1d
    val outScale = 1d
    val bias = 0.5
    val repetitions = 10000
    val variance = 0.033598965
    val wmmOrder = 2
    val iterationNum = 801

    val outputFunctionType = null // ActivationFunctionType.Sigmoid
    val inputDistribution = new UniformDistribution[jl.Double](0.2, 1d)
    val weightDistribution = new UniformDistribution[jl.Double](-0.5d, 0.5d)
    val kRDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.2, 0.8))
    val k0RDP = RandomDistributionProviderFactory(new UniformDistribution[Double](0.1, 0.4))

    // create functions to use
    def createNetwork(trainingStream: IOStream[jl.Double]) = createTwoLayerNeuralNetwork(outputFunctionType)(
      trainingStream.inputDim,
      trainingStream.outputDim,
      weightDistribution,
      bias)

    def createTrainer = NetworkTrainer.newPlainLinearRegressionNetworkTrainer(doubleNetworkRunnableFactory)(
      mlSetting, iterationNum, _: IOStream[jl.Double], _: Network[jl.Double], _: Iterable[TopologicalNode]
    )

    def createIOStream = ioStreamFactory.createInstance1D(wmmFun(kRDP.next, k0RDP.next) _)(0d, wmmOrder, 0)(inputDistribution)

    // eval RNMSE and SAMP error
    evaluateErrorAndStoreToFile(createNetwork, createTrainer, createIOStream)(taskName, modelName, variance, repetitions, iterationNum, inScale, outScale)
  }

  //  @Test
  def testNarma10InputOutput() {
    val inScale = 1
    val outScale = 1
    val dlSize = 2
    val inputDistribution = new RepeatedDistribution[jl.Double]((1 to 100).map(_ * 0.1: jl.Double).toArray)

    // Create a NARMA 10 IO stream
    val ioStream = ioStreamFactory.createNarmaTanhInstance(10, true)(inputDistribution)
    // Scale the IO stream
    val ioStreamScaled = ioStream.transformStream({_ * inScale}, {_ * outScale})
    // Adapt the IO stream for DL of given size
    val trainingStream = ioStreamFactory.createDelayLineIOStream[jl.Double](0d: jl.Double)(ioStreamScaled, dlSize)

    println("NARMA10 Inputs")
    ioStream.inputStream.take(20).foreach(println)

    println
    println("NARMA10 Outputs")
    ioStream.outputStream.take(20).foreach(println)

    println("NARMA10 DL Inputs")
    trainingStream.inputStream.take(20).foreach(println)

    println
    println("NARMA10 DL Outputs")
    trainingStream.outputStream.take(20).foreach(println)
  }

  private def trainNetworkForStaticFunAbsError(
    createNetwork: IOStream[jl.Double] => Network[jl.Double],
    createTrainer: (IOStream[jl.Double], Network[jl.Double], Iterable[TopologicalNode]) => Trainer[jl.Double, TopologicalNode, ju.List]
  )(
    staticFun: Iterable[jl.Double] => jl.Double,
    inputDistribution: RandomDistribution[jl.Double],
    inputSize: Int,
    iterationNum: Int
  ) = {
    // Create an IO stream
    val trainingStream = ioStreamFactory.createStaticInstance(staticFun)(inputDistribution, inputSize)

    // Train network and get outputs
    val outputs = trainNetworkAndGetOutputs(createNetwork, createTrainer, trainingStream, iterationNum)
    val desiredOutputs = (trainingStream.outputStream take outputs.size).toList.map(_.head)

    // Return abs errors
    (outputs, desiredOutputs).zipped.map { case (y, ye) => math.abs(y - ye)}
  }

  private def trainNetworkForStaticFunSquareAndSampError(
    createNetwork: IOStream[jl.Double] => Network[jl.Double],
    createTrainer: (IOStream[jl.Double], Network[jl.Double], Iterable[TopologicalNode]) => Trainer[jl.Double, TopologicalNode, ju.List]
  )(
    staticFun: Iterable[jl.Double] => jl.Double,
    inputDistribution: RandomDistribution[jl.Double],
    inputSize: Int,
    iterationNum: Int
  ) = {
    // Create an IO stream
    val trainingStream = ioStreamFactory.createStaticInstance(staticFun)(inputDistribution, inputSize)

    // Train network and get outputs
    val outputs = trainNetworkAndGetOutputs(createNetwork, createTrainer, trainingStream, iterationNum)
    val desiredOutputs = (trainingStream.outputStream take outputs.size).toList.map(_.head)

    // Return square and samp errors
    val squares = (outputs, desiredOutputs).zipped.map { case (y, ye) => (y - ye) * (y - ye)}
    val samps = (outputs, desiredOutputs).zipped.map { case (y, ye) => if (y + ye == 0) 100 else 100d * math.abs(y - ye) / (y + ye)}

//    val errorSetting = new TimeSeriesPlotSetting {
//      title = "Errors"
//      yRangeMax = 0.1
//      yRangeMin = 0
//    }
//    plotter.plotSingleSeries(squares, errorSetting)

    (squares, samps)
  }

  private def createTrainingSet = {
    val trainingSet = new TrainingSet[jl.Double]
    (1 to 100).foreach {
      _ -> {
        val trainingPair = new TrainingPair[jl.Double]
        val input = List[jl.Double](RandomUtil.nextDouble(0D, 10D), RandomUtil.nextDouble(0D, 10D))
        val desiredOutput = List[jl.Double](0.25 * (input(0) + input(1)), 0.5 * (input(0) - input(1)))

        trainingPair.setInput(input)
        trainingPair.setDesiredOutput(desiredOutput)
        trainingSet.addTrainingPair(trainingPair)
      }
    }
    trainingSet
  }

  private def evaluateErrorAndStoreToFile(
    createNetwork: IOStream[jl.Double] => Network[jl.Double],
    createTrainer: (IOStream[jl.Double], Network[jl.Double], Iterable[TopologicalNode]) => Trainer[jl.Double, TopologicalNode, ju.List],
    createIOStream: => IOStream[jl.Double])(
    taskName : String,
    modelName : String,
    variance : Double,
    repetitions : Int,
    iterationNum: Int,
    inScale : Double = 1,
    outScale : Double = 1,
    dlSizeFrom : Int = 2,
    dlSizeTo : Int = 20
  ) {
    println(taskName)
    println(modelName)
    println("Repetitions: " + repetitions)
    println("-----------")

    val globalMseOutput = new StringBuilder
    val globalRnmseOutput = new StringBuilder
    val globalSampOutput = new StringBuilder
    for (dlSize <- dlSizeFrom to dlSizeTo) {
      println("DL size " + dlSize)

      val squareAndSampFutures = (1 to repetitions).map(_ => future {
        val ioStream = createIOStream
        trainNetworkWithDelayLine(createNetwork, createTrainer)(ioStream, iterationNum, dlSize, inScale, outScale)
      })

      val squareAndSampErrors = squareAndSampFutures.map(Await.result(_, 100000 millis))
      val mses = squareAndSampErrors.map(_._1).transpose.map(s => s.sum / s.size)
      val rnmses = mses.map(s => math.sqrt(s / variance))
      val samps = squareAndSampErrors.map(_._2).transpose.map(s => s.sum / s.size)

      if (displayErrors) {
        val plotSetting = new SeriesPlotSetting()
            .setTitle("Errors")
            .setYRangeMax(0.02)
            .setYRangeMin(0)

        plotter.plotSingleSeries(mses, plotSetting)
        println("Last error:" + mses.last)
      }

      val mseOutput = mses.mkString(",")
      val rnmseOutput = rnmses.mkString(",")
      val sampOutput = samps.mkString(",")

      globalMseOutput.append(mseOutput + '\n')
      globalRnmseOutput.append(rnmseOutput + '\n')
      globalSampOutput.append(sampOutput + '\n')

      println("RNMSE: " + rnmses.last)
      println("SAMP:  " + samps.last)

      fileUtil.overwriteStringToFileSafe(mseOutput, taskName + "_MSE_" + modelName + "n" + dlSize)
      fileUtil.overwriteStringToFileSafe(rnmseOutput, taskName + "_RNMSE_" + modelName + "n" + dlSize)
      fileUtil.overwriteStringToFileSafe(sampOutput, taskName + "_SAMP_" + modelName+ "n" + dlSize)
    }
    fileUtil.overwriteStringToFileSafe(globalMseOutput.toString, taskName + "_MSE_" + modelName)
    fileUtil.overwriteStringToFileSafe(globalRnmseOutput.toString, taskName + "_RNMSE_" + modelName)
    fileUtil.overwriteStringToFileSafe(globalSampOutput.toString, taskName + "_SAMP_" + modelName)
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
    createTrainer: (IOStream[jl.Double], Network[jl.Double], Iterable[TopologicalNode]) => Trainer[jl.Double, TopologicalNode, ju.List]
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
    val topology = topologyFactory.apply(network.getTopology)
    network.setTopology(topology)

    // Create trainer
    val networkTrainer = createTrainer(trainingStream, network, topology.getLayers.last.getNonBiasNodes)

    // Train the network
    networkTrainer.train(iterationNum)

    // Return outputs
    networkTrainer.outputs
  }

  private def createTwoLayerChemicalNetwork(
    fun: (ju.List[jl.Double], ju.List[jl.Double]) => jl.Double)(
    inputNum: Int,
    outputNum: Int,
    weightDistribution: RandomDistribution[jl.Double],
    biasState: jl.Double
  ) = {
    val networkWeightSetting = new TemplateNetworkWeightSetting[jl.Double]
    networkWeightSetting.setRandomDistribution(weightDistribution)

    val network = new Network[jl.Double]
    network.setDefaultBiasState(biasState)
    network.setTopology(createTwoLayerTopology(inputNum, outputNum))
    network.setFunction(createTwoLayerChemicalNetworkFunction(fun))
    network.setWeightSetting(networkWeightSetting)

    network
  }

  private def createTwoLayerNeuralNetwork(
    outputFunctionType: ActivationFunctionType,
    outputFunctionParams: Option[Seq[jl.Double]] = None)(
    inputNum: Int,
    outputNum: Int,
    weightDistribution: RandomDistribution[jl.Double],
    biasState: jl.Double
  ) = {
    val networkWeightSetting = new TemplateNetworkWeightSetting[jl.Double]
    networkWeightSetting.setRandomDistribution(weightDistribution)

    val network = new Network[jl.Double]
    network.setDefaultBiasState(biasState)
    network.setTopology(createTwoLayerTopology(inputNum, outputNum))
    network.setFunction(createTwoLayerNeuralNetworkFunction(outputFunctionType, outputFunctionParams))
    network.setWeightSetting(networkWeightSetting)

    network
  }

  private def linearChemDotProduct(
    inputs: ju.List[jl.Double],
    weights: ju.List[jl.Double]
  ): jl.Double = {
    val output = (inputs, weights).zipped.map { (input, weight) => input * (1 - 2 / (1 + weight))}.sum
    if (output < 0) 0d else output
  }

  private def sigmoidChemDotProduct(
    inputs: ju.List[jl.Double],
    weights: ju.List[jl.Double]
  ): jl.Double = {
    val z = linearChemDotProduct(inputs, weights)
    1 - (math.exp(-z) + math.exp(-z / 2)) / 2
  }

  private def createTwoLayerChemicalNetworkFunction(
    fun: (ju.List[jl.Double], ju.List[jl.Double]) => jl.Double
  ) = {
    val layer1Function = new NetworkFunction[jl.Double]
    layer1Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
    layer1Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum)

    val layer2Function = new CustomNetworkFunction[jl.Double]
    layer2Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
    layer2Function.weightFunction = fun

    val topLevelFunction = new NetworkFunction[jl.Double]
    topLevelFunction.setMultiComponentUpdaterType(MultiStateUpdateType.AsyncFixedOrder)
    topLevelFunction.addLayerFunction(layer1Function)
    topLevelFunction.addLayerFunction(layer2Function)

    topLevelFunction
  }

  private def createTwoLayerNeuralNetworkFunction(
    outputFunctionType: ActivationFunctionType,
    outputFunctionParams: Option[Seq[jl.Double]] = None
  ) = {
    val layer1Function = new NetworkFunction[jl.Double]
    layer1Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
    layer1Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum)

    val layer2Function = new NetworkFunction[jl.Double]
    layer2Function.setMultiComponentUpdaterType(MultiStateUpdateType.Sync)
    layer2Function.setStatesWeightsIntegratorType(StatesWeightsIntegratorType.LinearSum)
    layer2Function.setActivationFunctionType(outputFunctionType)
    if (outputFunctionParams.isDefined)
      layer2Function.setActivationFunctionParams(outputFunctionParams.get)

    val topLevelFunction = new NetworkFunction[jl.Double]
    topLevelFunction.setMultiComponentUpdaterType(MultiStateUpdateType.AsyncFixedOrder)
    topLevelFunction.addLayerFunction(layer1Function)
    topLevelFunction.addLayerFunction(layer2Function)

    topLevelFunction
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

  private def createTwoLayerTopology(inputNum: Int, outputNum: Int) = {
    // layer 1
    val layer1 = new TemplateTopology
    layer1.setIndex(1)
    layer1.setNodesNum(inputNum)

    // layer 2
    val layer2 = new TemplateTopology
    layer2.setIndex(2)
    layer2.setNodesNum(outputNum)
    layer2.setGenerateBias(true)

    // top-level topology
    val topLevelTopology = new TemplateTopology
    topLevelTopology.setIntraLayerAllEdges(true)
    topLevelTopology.addLayer(layer1)
    topLevelTopology.addLayer(layer2)

    topLevelTopology
  }
}