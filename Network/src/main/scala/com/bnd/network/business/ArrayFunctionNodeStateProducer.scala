package com.bnd.network.business

import com.bnd.core.runnable.ConstantTimeStepSingleStateProducer

import java.{util => ju}
import com.bnd.function.evaluator.FunctionEvaluator
import com.bnd.network.business.integrator.StatesWeightsIntegratorDef.StatesWeightsIntegrator
import com.bnd.network.domain.TopologicalNode

import scala.jdk.CollectionConverters._

/**
 * @author © Peter Banda
 * @since 2012  
 */
@Deprecated
final class ArrayFunctionNodeStateProducer[T : Manifest](
	topologicalNode : TopologicalNode,
	functionEvaluator : FunctionEvaluator[T, T],
	statesWeightsIntegrator : Option[StatesWeightsIntegrator[T]]
) extends ConstantTimeStepSingleStateProducer[T, TopologicalNode, Array](1d) {

	var inNodesWeights : ju.List[T] = new ju.ArrayList[T]()

//	private def checkFunctionEvaluatorArity() {
//		val functionArity = functionEvaluator.getArity
//		var expectedFunctionArity = topologicalNode.getInEdges.size
//		if (statesWeightsIntegrator.isDefined) {
//			expectedFunctionArity = statesWeightsIntegrator.get.getOutputArity(expectedFunctionArity)
//		}
//		if (functionArity != null) {
//			if (!ObjectUtil.areObjectsEqual(expectedFunctionArity, functionArity)) {
//				throw new BndNetworkException("Node '" + topologicalNode.getId() + "' -  with the expected arity '" + expectedFunctionArity + "' differs from the arity of associated function: " + functionArity)
//			}
//		}
//	}

	protected def setInNodesWeights(inNodesWeights : ju.List[T]) : Unit = this.inNodesWeights = inNodesWeights

	override def nextSingleState(inNodeStates : Array[T], timeStep : Option[Double]) = {
		var functionInputs = inNodeStates : Seq[T]
		if (statesWeightsIntegrator.isDefined)
			statesWeightsIntegrator.get(functionInputs.asJava, inNodesWeights)
		else
			functionEvaluator.evaluate(functionInputs.asJava)
	}

	override def listInputComponentsInOrder =
		topologicalNode.getInNeighbors.asScala

	override def outputComponent = topologicalNode

	override def singleton[X : Manifest](element : X) = Array.apply(element)
}