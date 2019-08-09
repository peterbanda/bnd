package com.bnd.network.business.learning

import java.{lang => jl, util => ju}

import com.bnd.core.runnable.StateEvent
import com.bnd.math.BndMathException
import com.bnd.math.business.learning.{IOStream, Trainer}
import com.bnd.math.domain.learning.MachineLearningSetting
import com.bnd.math.domain.learning.MachineLearningSetting.LearningRateAnnealingType
import com.bnd.network.business.{NetworkRunnableFactory, WeightAccessible}
import com.bnd.network.domain.{Network, NetworkSimulationConfig, TopologicalNode}
import com.bnd.core.runnable.TimeRunnable
import org.jblas.{DoubleMatrix, Eigen, Solve}

import scala.collection.JavaConversions._
import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.Publisher

/**
 * A linear regression trainer of the underlying network runnable
 *
 * @author © Peter Banda
 * @since 2015
 */
private class LinearRegressionNetworkTrainer(
  calcOutputError: (jl.Double, jl.Double) => jl.Double,
  calcErrorMetrics: (Seq[jl.Double], Seq[jl.Double]) => jl.Double)(
  setting: MachineLearningSetting,
  iterationNum: Int,
  weightAdaptationIterationNum: Option[Int],
  trainingStream: IOStream[jl.Double],
  learner: TimeRunnable with Publisher[StateEvent[jl.Double, ju.List]],
  weightAccessor: WeightAccessible[jl.Double],
  outputNodes: Iterable[TopologicalNode]
) extends Trainer[jl.Double, TopologicalNode, ju.List](calcOutputError, calcErrorMetrics)(setting, trainingStream)(learner, outputNodes) {

  private val nodeInStateDesiredOutputHistoryMatrixMap = MMap[TopologicalNode, (DoubleMatrix, DoubleMatrix)]()

  // TODO: add ridge regression

  override def adapt(
    outputComponentDesiredOutputErrorTuples: Iterable[(TopologicalNode, jl.Double, jl.Double)],
    state: ju.List[jl.Double]
  ) =
    outputComponentDesiredOutputErrorTuples.foreach {
      case (node, desiredOutput, error) =>
        val inStates = node.getInNeighbors.map { case inNode => componentState(inNode, state)}
        val inStateDesiredOutputHistoryMatrixOption = nodeInStateDesiredOutputHistoryMatrixMap.get(node)

        val (inStateMatrix, desiredOutputMatrix) = if (inStateDesiredOutputHistoryMatrixOption.isDefined)
          (DoubleMatrix.concatVertically(inStateDesiredOutputHistoryMatrixOption.get._1, new DoubleMatrix(inStates).transpose),
          DoubleMatrix.concatVertically(inStateDesiredOutputHistoryMatrixOption.get._2, new DoubleMatrix(List(desiredOutput))))
        else
          (new DoubleMatrix(inStates).transpose, new DoubleMatrix(List(desiredOutput)))

        nodeInStateDesiredOutputHistoryMatrixMap.update(node, (inStateMatrix, desiredOutputMatrix))

        if (weightAdaptationIterationNum.isEmpty || currentTrainingIteration > iterationNum - (weightAdaptationIterationNum.get + 1)) {
//          val inStateTranspose = inStateMatrix.transpose()
//          val pinv = Solve.pinv(inStateTranspose.mul(inStateMatrix))
//          val pinvT = pinv.mul(inStateTranspose)
//          val std = inStateTranspose.mmul(desiredOutputMatrix)
//
//          println("S        : " + inStateMatrix.rows + " " + inStateMatrix.columns)
//          println("ST       : " + inStateTranspose.rows + " " + inStateTranspose.columns)
//          println("(STS)-1  : " + pinv.rows + " " + pinv.columns)
//          println("(STS)-1ST: " + pinvT.rows + " " + pinvT.columns)
//          println("D        : " + desiredOutputMatrix.rows + " " + desiredOutputMatrix.columns)
//          println("STD      : " + std.rows + " " + std.columns)
//          val newWeights = pinv.mul(inStateTranspose).transpose().mmul(desiredOutputMatrix)

          val newWeights = Solve.solveLeastSquares(inStateMatrix, desiredOutputMatrix)

          (node.getInNeighbors, newWeights.data).zipped.foreach { case (inNode, newWeight) =>
            weightAccessor.setWeight(inNode, node, newWeight)
          }
        }
    }
}

/**
 * A trainer of the underlying network runnable
 *
 * @author © Peter Banda
 * @since 2015
 */
private class DeltaRuleNetworkTrainer(
  calcOutputError: (jl.Double, jl.Double) => jl.Double,
  calcErrorMetrics: (Seq[jl.Double], Seq[jl.Double]) => jl.Double,
  updateLearningRate: (Double, Double) => Double)(
  setting: MachineLearningSetting,
  trainingStream: IOStream[jl.Double],
  learner: TimeRunnable with Publisher[StateEvent[jl.Double, ju.List]],
  weightAccessor: WeightAccessible[jl.Double],
  outputNodes: Iterable[TopologicalNode],
  trainOutputNodesOnly: Boolean
) extends Trainer[jl.Double, TopologicalNode, ju.List](calcOutputError, calcErrorMetrics)(setting, trainingStream)(learner, outputNodes) {

  var learningRate = setting.getInitialLearningRate

  override def adapt(
    outputComponentDesiredOutputErrorTuples: Iterable[(TopologicalNode, jl.Double, jl.Double)],
    state: ju.List[jl.Double]
  ) = {
    val originalErrorMap = outputComponentDesiredOutputErrorTuples.map { case (node, desiredOutput, error) => (node, error)}.toMap
    val errorMap = if (trainOutputNodesOnly)
      originalErrorMap
    else
      calcBackpropErrors(originalErrorMap, state)

    errorMap.foreach { case (node, error) =>
      node.getInNeighbors.foreach { case inNode =>
        val inState = componentState(inNode, state)
        // TODO: add derivation
        val newWeight = weightAccessor.getWeight(inNode, node).get + learningRate * inState * error
        weightAccessor.setWeight(inNode, node, newWeight)
      }
    }
    learningRate = updateLearningRate(learningRate, setting.getLearningAnnealingRate)
  }

  private[learning] def calcBackpropErrors(
    outputComponentErrorMap: Map[TopologicalNode, jl.Double],
    state: ju.List[jl.Double]
  ): Map[TopologicalNode, jl.Double] = {
    val errorMap = MMap[TopologicalNode, jl.Double]()
    errorMap ++= outputComponentErrorMap
    var activeNodes = outputComponentErrorMap.map(_._1)

    // auxiliary function calculating the backprop error for a given node
    def calcBackpropError(node: TopologicalNode) =
      node.getOutNeighbors.map { outNode =>
        val error = errorMap.get(outNode).getOrElse {
          throw new BndMathException("Node " + outNode + " has not been in handled while backpropagating the error. The topology is not layered!")
        }

        val outState = componentState(outNode, state)
        val weight = weightAccessor.getWeight(node, outNode).get
        (error * weight)
      }.sum

    while (!activeNodes.isEmpty)
      activeNodes = activeNodes.map(_.getInNeighbors.map {
        inNode =>
          if (!errorMap.contains(inNode) && !inNode.getInEdges.isEmpty) {
            errorMap(inNode) = calcBackpropError(inNode)
            Some(inNode)
          } else None
        }.flatten
      ).flatten

    errorMap.toMap
  }
}

private class DeltaRuleChemicalNetworkTrainer(
  calcOutputError: (jl.Double, jl.Double) => jl.Double,
  calcErrorMetrics: (Seq[jl.Double], Seq[jl.Double]) => jl.Double,
  updateLearningRate: (Double, Double) => Double)(
  setting: MachineLearningSetting,
  trainingStream: IOStream[jl.Double],
  learner: TimeRunnable with Publisher[StateEvent[jl.Double, ju.List]],
  weightAccessor: WeightAccessible[jl.Double],
  outputNodes: Iterable[TopologicalNode],
  trainOutputNodesOnly: Boolean
) extends DeltaRuleNetworkTrainer(
  calcOutputError,
  calcErrorMetrics,
  updateLearningRate
)(setting, trainingStream, learner, weightAccessor, outputNodes, trainOutputNodesOnly) {

  override def adapt(
    outputComponentDesiredOutputErrorTuples: Iterable[(TopologicalNode, jl.Double, jl.Double)],
    state: ju.List[jl.Double]
  ) = {
    val originalErrorMap = outputComponentDesiredOutputErrorTuples.map { case (node, desiredOutput, error) => (node, error)}.toMap
    val errorMap = if (trainOutputNodesOnly)
      originalErrorMap
    else
      calcBackpropErrors(originalErrorMap, state)

    errorMap.foreach { case (node, error) =>
      val inStateSum = node.getInNeighbors.map(componentState(_, state): Double).sum
      node.getInNeighbors.foreach { case inNode =>
        val inState = componentState(inNode, state)
        val newWeight = weightAccessor.getWeight(inNode, node).get + inState * error / (inStateSum * learningRate)
        weightAccessor.setWeight(inNode, node, if (newWeight < 0) 0d else newWeight)
      }
    }
//    if (currentTrainingTime >= 800) {
//      println(currentTrainingTime + " : " + learningRate)
//    }
    learningRate = updateLearningRate(learningRate, setting.getLearningAnnealingRate)
  }
}

object NetworkTrainer {

  type NetworkTrainer = Trainer[jl.Double, TopologicalNode, ju.List]

  // Creates a new trainer for given network runnable
  def newDeltaRuleNetworkRunnableTrainer(
    setting: MachineLearningSetting,
    trainingStream: IOStream[jl.Double],
    learner: TimeRunnable with Publisher[StateEvent[jl.Double, ju.List]],
    weightAccessor: WeightAccessible[jl.Double],
    outputNodes: Iterable[TopologicalNode],
    trainOutputNodesOnly: Boolean = false
  ): NetworkTrainer = new DeltaRuleNetworkTrainer(
    _ - _,
    meanSquareErrorMetricsFun,
    createLearningRateFun(setting.getLearningAnnealingType)
  )(setting, trainingStream, learner, weightAccessor, outputNodes, trainOutputNodesOnly)

  // Creates a new trainer for given network runnable
  def newLinearRegressionNetworkRunnableTrainer(
    setting: MachineLearningSetting,
    iterationNum: Int,
    weightAdaptationIterationNum: Option[Int],
    trainingStream: IOStream[jl.Double],
    learner: TimeRunnable with Publisher[StateEvent[jl.Double, ju.List]],
    weightAccessor: WeightAccessible[jl.Double],
    outputNodes: Iterable[TopologicalNode]
  ): NetworkTrainer = new LinearRegressionNetworkTrainer(
    _ - _,
    meanSquareErrorMetricsFun
  )(setting, iterationNum, weightAdaptationIterationNum, trainingStream, learner, weightAccessor, outputNodes)

  // Creates a new trainer for given network runnable
  def newDeltaRuleChemicalNetworkRunnableTrainer(
    setting: MachineLearningSetting,
    trainingStream: IOStream[jl.Double],
    learner: TimeRunnable with Publisher[StateEvent[jl.Double, ju.List]],
    weightAccessor: WeightAccessible[jl.Double],
    outputNodes: Iterable[TopologicalNode],
    trainOutputNodesOnly: Boolean = false
  ): NetworkTrainer = new DeltaRuleChemicalNetworkTrainer(
    _ - _,
    meanSquareErrorMetricsFun,
    createLearningRateChemicalFun(setting.getLearningAnnealingType)
  )(setting, trainingStream, learner, weightAccessor, outputNodes, trainOutputNodesOnly)

  // Creates a new trainer for given network
  def newDeltaRuleNetworkTrainer(
    networkRunnableFactory: NetworkRunnableFactory[jl.Double])(
    setting: MachineLearningSetting,
    trainingStream: IOStream[jl.Double],
    network: Network[jl.Double],
    outputNodes: Iterable[TopologicalNode],
    trainOutputNodesOnly: Boolean = false
  ): NetworkTrainer = {
    val networkRunnableWithWeightAccessible = networkRunnableFactory.createInteractiveLayeredWeightAccessible(
      network,
      new NetworkSimulationConfig,
      setting.getInitialDelay: Double,
      setting.getSingleIterationLength: Double,
      trainingStream.inputStream)

    newDeltaRuleNetworkRunnableTrainer(
      setting,
      trainingStream,
      networkRunnableWithWeightAccessible._1,
      networkRunnableWithWeightAccessible._2,
      outputNodes,
      trainOutputNodesOnly)
  }

  // Creates a new trainer for given network
  def newLinearRegressionNetworkTrainerWithWeightAccessible(
    networkRunnableFactory: NetworkRunnableFactory[jl.Double])(
    setting: MachineLearningSetting,
    iterationNum: Int,
    weightAdaptationIterationNum: Int,
    spectralRadius: Double,
    trainingStream: IOStream[jl.Double],
    network: Network[jl.Double],
    outputNodes: Iterable[TopologicalNode]
  ): (NetworkTrainer, WeightAccessible[jl.Double]) = {
    val networkRunnableWithWeightAccessible = networkRunnableFactory.createInteractiveLayeredWeightAccessible(
      network,
      new NetworkSimulationConfig,
      setting.getInitialDelay: Double,
      setting.getSingleIterationLength: Double,
      trainingStream.inputStream)

    // normalize the weights for a given spectral radius
    val reservoir = network.getTopology.getLayers.toSeq(1)
    val weightAccessor = networkRunnableWithWeightAccessible._2
    normalizeWeights(reservoir.getAllNodes, weightAccessor, spectralRadius)

    // create a trainer
    val networkTrainer = newLinearRegressionNetworkRunnableTrainer(
      setting,
      iterationNum,
      Some(weightAdaptationIterationNum),
      trainingStream,
      networkRunnableWithWeightAccessible._1,
      networkRunnableWithWeightAccessible._2,
      outputNodes
    )
    (networkTrainer, weightAccessor)
  }

  def normalizeWeights(
    nodes: Traversable[TopologicalNode],
    weightAccessor: WeightAccessible[jl.Double],
    spectralRadius: Double
  ) = {
    // normalize the weights for a given spectral radius
    val reservoirWeightMatrix : Array[Array[Double]] = nodes.map { node1 =>
      nodes.map { node2 =>
        val weight = weightAccessor.getWeight(node1, node2)
        if (weight.isDefined) weight.get: Double else 0d
      }.toArray
    }.toArray

    val eigenValues = Eigen.eigenvalues(new DoubleMatrix(reservoirWeightMatrix)).data.toSeq.sorted
    val maxEigenValue = eigenValues.last

    for (node1 <- nodes; node2 <- nodes) {
      val weight = weightAccessor.getWeight(node1, node2)
      if (weight.isDefined) weightAccessor.setWeight(node1, node2, spectralRadius * weight.get / maxEigenValue)
    }
  }

  def newPlainLinearRegressionNetworkTrainer(
    networkRunnableFactory: NetworkRunnableFactory[jl.Double])(
    setting: MachineLearningSetting,
    iterationNum: Int,
    trainingStream: IOStream[jl.Double],
    network: Network[jl.Double],
    outputNodes: Iterable[TopologicalNode]
  ): NetworkTrainer = {
    val networkRunnableWithWeightAccessible = networkRunnableFactory.createInteractiveLayeredWeightAccessible(
      network,
      new NetworkSimulationConfig,
      setting.getInitialDelay: Double,
      setting.getSingleIterationLength: Double,
      trainingStream.inputStream)

    newLinearRegressionNetworkRunnableTrainer(
      setting,
      iterationNum,
      None,
      trainingStream,
      networkRunnableWithWeightAccessible._1,
      networkRunnableWithWeightAccessible._2,
      outputNodes)
  }

  // Creates a new trainer for given network featuring the chemical learning
  def newDeltaRuleChemicalNetworkTrainer(
    networkRunnableFactory: NetworkRunnableFactory[jl.Double])(
    setting: MachineLearningSetting,
    trainingStream: IOStream[jl.Double],
    network: Network[jl.Double],
    outputNodes: Iterable[TopologicalNode],
    trainOutputNodesOnly: Boolean = false
  ): NetworkTrainer = {
    val networkRunnableWithWeightAccessible = networkRunnableFactory.createInteractiveLayeredWeightAccessible(
      network,
      new NetworkSimulationConfig,
      setting.getInitialDelay: Double,
      setting.getSingleIterationLength: Double,
      trainingStream.inputStream)

    newDeltaRuleChemicalNetworkRunnableTrainer(
      setting,
      trainingStream,
      networkRunnableWithWeightAccessible._1,
      networkRunnableWithWeightAccessible._2,
      outputNodes,
      trainOutputNodesOnly)
  }

  private def meanSquareErrorMetricsFun(a: Seq[jl.Double], b: Seq[jl.Double]) =
    (a, b).zipped.map((x, y) => (x - y) * (x - y)).sum / a.size

  private def createLearningRateFun(annealingType: LearningRateAnnealingType) =
    annealingType match {
      case LearningRateAnnealingType.Linear => { (learningRate: Double, annealingRate: Double) => math.max(learningRate - annealingRate, 0d)}
      case LearningRateAnnealingType.Exponential => { (learningRate: Double, annealingRate: Double) => learningRate * (1 - annealingRate)}
    }

  private def createLearningRateChemicalFun(annealingType: LearningRateAnnealingType) =
    annealingType match {
      case LearningRateAnnealingType.Linear => { (learningRate: Double, annealingRate: Double) => learningRate + annealingRate}
      case LearningRateAnnealingType.Exponential => { (learningRate: Double, annealingRate: Double) => learningRate * (1 + annealingRate)}
    }
}