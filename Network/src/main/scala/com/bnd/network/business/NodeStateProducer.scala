package com.bnd.network.business

import java.util.Collections
import java.{util => ju}

import com.bnd.core.runnable.{SingleStateProducer, ConstantTimeStepSingleStateProducer}
import com.bnd.function.evaluator.FunctionEvaluator
import com.bnd.network.business.integrator.StatesWeightsIntegratorDef._
import com.bnd.network.domain.TopologicalNode

import scala.collection.JavaConversions._

/**
 * @author © Peter Banda
 * @since 2012  
 */
protected abstract class NodeStateProducer[T: Manifest](
  topologicalNode: TopologicalNode
) extends ConstantTimeStepSingleStateProducer[T, TopologicalNode, ju.List](1d) with Serializable {

  override def listInputComponentsInOrder = topologicalNode.getInNeighbors

  override def outputComponent = topologicalNode

  override def singleton[X: Manifest](element: X) = Collections.singletonList(element)
}

/**
 * @author © Peter Banda
 * @since 2012
 */
protected abstract class WeightNodeStateProducer[T: Manifest](
  topologicalNode: TopologicalNode
) extends NodeStateProducer[T](topologicalNode) with InWeightAccessible[T] with Serializable {

  var inNodesWeights: ju.List[T] = {
    val weights = new ju.ArrayList[T]
    (1 to topologicalNode.getInEdges.size).foreach(_ => weights.add(null.asInstanceOf[T]))
    weights
  }

  val inNodeIndexMap = topologicalNode.getInNeighbors.zipWithIndex.toMap

  override def setWeights(weightIterator: ju.Iterator[T]) =
    topologicalNode.getInEdges.zipWithIndex.foreach { case (edge, index) =>
      inNodesWeights.set(index, weightIterator.next)
    }

  override def setMutableWeights(weightIterator: ju.Iterator[T]) =
    topologicalNode.getInEdges.zipWithIndex.foreach { case (edge, index) =>
      if (!edge.getStart.isBias)
        inNodesWeights.set(index, weightIterator.next)
    }

  override def setImmutableWeights(weightIterator: ju.Iterator[T]) =
    topologicalNode.getInEdges.zipWithIndex.foreach { case (edge, index) =>
      if (edge.getStart.isBias)
        inNodesWeights.set(index, weightIterator.next)
    }

  override def getWeightsNum = inNodesWeights.size

  override def setWeight(start: TopologicalNode, value: T) =
    if (inNodeIndexMap.get(start).isDefined) inNodesWeights.set(inNodeIndexMap.get(start).get, value)

  override def getWeight(start: TopologicalNode): T =
    if (inNodeIndexMap.get(start).isDefined) inNodesWeights.get(inNodeIndexMap.get(start).get) else null.asInstanceOf[T]
}

private final class FunctionNodeStateProducer[T: Manifest](
  topologicalNode: TopologicalNode,
  function: ju.List[T] => T
) extends NodeStateProducer[T](topologicalNode) with Serializable {

  override def nextSingleState(inNodeStates: ju.List[T], timeStep: Option[Double]) = function(inNodeStates)
}

private final class WeightFunctionNodeStateProducer[T: Manifest](
  topologicalNode: TopologicalNode,
  function: (ju.List[T], ju.List[T]) => T
) extends WeightNodeStateProducer[T](topologicalNode) with Serializable {

  override def nextSingleState(inNodeStates: ju.List[T], timeStep: Option[Double]) = function(inNodeStates, inNodesWeights)
}

object NodeStateProducer {

  def apply[T: Manifest](
    topologicalNode: TopologicalNode,
    function: ju.List[T] => T
  ): SingleStateProducer[T, TopologicalNode, ju.List] = new FunctionNodeStateProducer[T](topologicalNode, function)

  def apply[T: Manifest](
    topologicalNode: TopologicalNode,
    functionEvaluator: FunctionEvaluator[T, T]
  ): SingleStateProducer[T, TopologicalNode, ju.List] = new FunctionNodeStateProducer[T](topologicalNode, functionEvaluator.evaluate(_))

  def apply[T: Manifest](
    topologicalNode: TopologicalNode,
    statesWeightsIntegrator: StatesWeightsIntegrator[T]
  ): SingleStateProducer[T, TopologicalNode, ju.List] with InWeightAccessible[T] =
    new WeightFunctionNodeStateProducer[T](topologicalNode, statesWeightsIntegrator)

  def apply[T: Manifest](
    topologicalNode: TopologicalNode,
    inStatesWeightsIntegration: (ju.List[T], ju.List[T]) => T,
    outputFunction: T => T
  ): SingleStateProducer[T, TopologicalNode, ju.List] with InWeightAccessible[T] =
    new WeightFunctionNodeStateProducer[T](topologicalNode, (a: ju.List[T], b: ju.List[T]) => outputFunction(inStatesWeightsIntegration(a, b)))
}