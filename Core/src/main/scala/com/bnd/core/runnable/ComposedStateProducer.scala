package com.bnd.core.runnable

import SeqIndexAccessible.Implicits._
import com.bnd.core.domain.MultiStateUpdateType

import scala.util.Random

trait ComposedStateProducer[T, C, S[X]] extends StateProducer[T, C, S] {

  def listNestedProducers : Iterable[StateProducer[T, C, S]]

  def updateState(inputState : S[T], outputState : S[T], timeStep : Option[Double])

  def setComponentIndexMap(componentIndexMap : Map[C, Int])
}

abstract class AbstractComposedStateProducer[T : Manifest, C, S[X] : SeqIndexAccessible, P <: StateProducer[T, C, S]](
  val stateProducers : Iterable[P],
  uniformStepSizeFlag : Boolean,
  updateType : MultiStateUpdateType
) extends ComposedStateProducer[T, C, S] with Serializable {

  val sia = implicitly[SeqIndexAccessible[S]]

  private[runnable] val inputComponents = stateProducers.foldLeft(List[C]()){(buffer, producer) =>
    buffer ++ producer.listInputComponentsInOrder
  }.distinct

  private[runnable] val outputComponents = stateProducers.foldLeft(List[C]()){(buffer, producer) =>
    buffer ++ producer.listOutputComponentsInOrder
  }.distinct

  private[runnable] val outputComponentsSize = outputComponents.size
  private[runnable] val components = (inputComponents ++ outputComponents).distinct
  private[runnable] var componentIndexMap = components.zipWithIndex.toMap

  private[runnable] val constantTimeStepFlag = stateProducers.forall(_.isConstantTimeStep)
  private[runnable] val constantTimeStep = if (constantTimeStepFlag) Some(newNextTimeStep) else None

  override def listInputComponentsInOrder = inputComponents

  override def listOutputComponentsInOrder = outputComponents

  override def nextState(inputState : S[T], timeStep : Option[Double]) =
    updateType match {
      case MultiStateUpdateType.Sync => nextStateSync(inputState, timeStep)
      case MultiStateUpdateType.AsyncFixedOrder | MultiStateUpdateType.AsyncFixedRandomOrder => nextStateAsync(inputState, timeStep)
      case MultiStateUpdateType.AsyncRandom => nextStateAsyncRandom(inputState, timeStep)
    }

  override def updateState(inputState : S[T], outputState : S[T], timeStep : Option[Double]) =
    updateType match {
      case MultiStateUpdateType.Sync => updateStateSync(inputState, outputState, timeStep)
      case MultiStateUpdateType.AsyncFixedOrder | MultiStateUpdateType.AsyncFixedRandomOrder => updateStateAsync(inputState, outputState, timeStep)
      case MultiStateUpdateType.AsyncRandom => updateStateAsyncRandom(inputState, outputState, timeStep)
    }

  private[runnable] def nextStateSync(currentState : S[T], timeStep : Option[Double]) = {
    val newState = currentState.copy
    //    	val newState = sia.fill(outputComponentsSize, null.asInstanceOf[T])
    updateStateAsync(currentState, newState, timeStep)
    newState
  }

  private[runnable] def nextStateAsync(currentState : S[T], timeStep : Option[Double]) = {
    updateStateAsync(currentState, currentState, timeStep)
    currentState
  }

  private[runnable] def nextStateAsyncRandom(currentState : S[T], timeStep : Option[Double]) = {
    updateStateAsyncRandom(currentState, currentState, timeStep)
    currentState
  }

  private[runnable] def updateStateSync(inputState : S[T], outputState : S[T], timeStep : Option[Double])

  private[runnable] def updateStateAsync(inputState : S[T], outputState : S[T], timeStep : Option[Double])

  private[runnable] def updateStateAsyncRandom(inputState : S[T], outputState : S[T], timeStep : Option[Double])

  private[runnable] def slowestStateProducer = stateProducers.minBy(_.nextTimeStep)

  override def isConstantTimeStep = constantTimeStepFlag

  private[runnable] def newNextTimeStep = if (uniformStepSizeFlag)
    stateProducers.head.nextTimeStep
  else
    slowestStateProducer.nextTimeStep

  override def nextTimeStep = if (constantTimeStepFlag) constantTimeStep.get else newNextTimeStep

  override def listNestedProducers = stateProducers

  override def setComponentIndexMap(componentIndexMap : Map[C, Int]) = this.componentIndexMap = componentIndexMap
}

private final class ComposedNestedMultiOutputStateProducer[T : Manifest, C, S[X] : SeqIndexAccessible](
  stateProducers : Iterable[ComposedStateProducer[T, C, S]],
  uniformStepSizeFlag : Boolean,
  updateType : MultiStateUpdateType
) extends AbstractComposedStateProducer[T, C, S, ComposedStateProducer[T, C, S]](stateProducers, uniformStepSizeFlag, updateType) with Serializable {

  // TODO
  override private[runnable] def updateStateSync(inputStates : S[T], outputStates : S[T], plannedTimeStep : Option[Double]) =
    stateProducers.foreach( _.updateState(inputStates, outputStates, plannedTimeStep))

  override private[runnable] def updateStateAsync(inputStates : S[T], outputStates : S[T], plannedTimeStep : Option[Double]) =
    stateProducers.foreach( _.updateState(inputStates, outputStates, plannedTimeStep))

  override private[runnable] def updateStateAsyncRandom(inputStates : S[T], outputStates : S[T], plannedTimeStep : Option[Double]) =
    Random.shuffle(stateProducers).foreach( _.updateState(inputStates, outputStates, plannedTimeStep))

  override def setComponentIndexMap(componentIndexMap : Map[C, Int]) = {
    super.setComponentIndexMap(componentIndexMap)
    stateProducers.foreach(_.setComponentIndexMap(componentIndexMap))
  }
}

private final class ComposedMultiOutputStateProducer[T : Manifest, C, S[X] : SeqIndexAccessible](
  stateProducers : Iterable[StateProducer[T, C, S]],
  uniformStepSizeFlag : Boolean,
  updateType : MultiStateUpdateType
) extends AbstractComposedStateProducer[T, C, S, StateProducer[T, C, S]](stateProducers, uniformStepSizeFlag, updateType) with Serializable {

  private[runnable] var stateProducersWithInputOutputIndeces = initStateProducersWithInputOutputIndeces

  private[runnable] def initStateProducersWithInputOutputIndeces = stateProducers.map{stateProducer =>
    (stateProducer,
      stateProducer.listInputComponentsInOrder.map(componentIndexMap.get(_).get),
      stateProducer.listOutputComponentsInOrder.map(componentIndexMap.get(_).get))}

  override private[runnable] def updateStateSync(inputState : S[T], outputState : S[T], timeStep : Option[Double]) = {
    val newOutputStatesWithIndeces = stateProducersWithInputOutputIndeces.map{ case (stateProducer, inputIndeces, outputIndeces) => {
      val partialNewStates = stateProducer.nextState(inputState.apply(inputIndeces), timeStep)
      (partialNewStates, outputIndeces)
    }}
    newOutputStatesWithIndeces.foreach{ case (newOutputStates, outputIndeces) => {
      outputIndeces.iterator.zip(newOutputStates.iterator).foreach( t => outputState.update(t._1, t._2))
    }}
  }

  override private[runnable] def updateStateAsync(inputState : S[T], outputState : S[T], timeStep : Option[Double]) =
    stateProducersWithInputOutputIndeces.foreach{ case (stateProducer, inputIndeces, outputIndeces) => {
      val partialNewStates = stateProducer.nextState(inputState.apply(inputIndeces), timeStep)
      outputIndeces.iterator.zip(partialNewStates.iterator).foreach( t => outputState.update(t._1, t._2))
    }}

  override private[runnable] def updateStateAsyncRandom(inputState : S[T], outputState : S[T], timeStep : Option[Double]) =
    Random.shuffle(stateProducersWithInputOutputIndeces).foreach{ case (stateProducer, inputIndeces, outputIndeces) => {
      val partialNewStates = stateProducer.nextState(inputState.apply(inputIndeces), timeStep)
      outputIndeces.iterator.zip(partialNewStates.iterator).foreach( t => outputState.update(t._1, t._2))
    }}

  override def setComponentIndexMap(componentIndexMap : Map[C, Int]) = {
    super.setComponentIndexMap(componentIndexMap)
    stateProducersWithInputOutputIndeces = initStateProducersWithInputOutputIndeces
  }
}

private final class ComposedSingleOutputStateProducer[T : Manifest, C, S[X] : SeqIndexAccessible](
  stateProducers : Iterable[SingleStateProducer[T, C, S]],
  uniformStepSizeFlag : Boolean,
  updateType : MultiStateUpdateType
) extends AbstractComposedStateProducer[T, C, S, SingleStateProducer[T, C, S]](stateProducers, uniformStepSizeFlag, updateType) with Serializable {

  private[runnable] var stateProducersWithInputOutputIndeces = initStateProducersWithInputOuputIndeces

  private[runnable] def initStateProducersWithInputOuputIndeces = stateProducers.map{stateProducer =>
    (stateProducer,
      stateProducer.listInputComponentsInOrder.map(componentIndexMap.get(_).get),
      componentIndexMap.get(stateProducer.outputComponent).get)}

  override private[runnable] def updateStateSync(inputState : S[T], outputState : S[T], timeStep : Option[Double]) = {
    val outputStateOps = infixSeqIndexAccessibleOps(outputState)
    val inputStateOps = infixSeqIndexAccessibleOps(inputState)

    val newOutputStateWithIndeces = stateProducersWithInputOutputIndeces.map{ case (stateProducer, inputIndeces, outputIndex) => {
      val partialNewStates = stateProducer.nextSingleState(inputStateOps.apply(inputIndeces), timeStep)
      (partialNewStates, outputIndex)
    }}
    newOutputStateWithIndeces.foreach{ case (newOutputState, outputIndex) => {
      outputStateOps.update(outputIndex, newOutputState)
    }}
  }

  override private[runnable] def updateStateAsync(inputState : S[T], outputState : S[T], timeStep : Option[Double]) = {
    val outputStateOps = infixSeqIndexAccessibleOps(outputState)
    val inputStateOps = infixSeqIndexAccessibleOps(inputState)
    stateProducersWithInputOutputIndeces.foreach{ case (stateProducer, inputIndeces, outputIndex) =>
      outputStateOps.update(outputIndex, stateProducer.nextSingleState(inputStateOps.apply(inputIndeces), timeStep))
    }
  }

  override private[runnable] def updateStateAsyncRandom(inputState : S[T], outputState : S[T], timeStep : Option[Double]) = {
    val outputStateOps = infixSeqIndexAccessibleOps(outputState)
    val inputStateOps = infixSeqIndexAccessibleOps(inputState)
    Random.shuffle(stateProducersWithInputOutputIndeces).foreach{ case (stateProducer, inputIndeces, outputIndex) =>
      outputStateOps.update(outputIndex, stateProducer.nextSingleState(inputStateOps.apply(inputIndeces), timeStep))
    }
  }

  override def setComponentIndexMap(componentIndexMap : Map[C, Int]) = {
    super.setComponentIndexMap(componentIndexMap)
    stateProducersWithInputOutputIndeces = initStateProducersWithInputOuputIndeces
  }
}

object ComposedStateProducer {

  def multiOutputInstance[T : Manifest, C, S[X] : SeqIndexAccessible](
    stateProducers : Iterable[_ <: StateProducer[T, C, S]],
    updateType : MultiStateUpdateType,
    uniformStepSizeFlag : Boolean
  ): ComposedStateProducer[T, C, S] =
    if (updateType == MultiStateUpdateType.AsyncFixedRandomOrder)
      new ComposedMultiOutputStateProducer[T, C, S](Random.shuffle(stateProducers), uniformStepSizeFlag, updateType)
    else
      new ComposedMultiOutputStateProducer[T, C, S](stateProducers, uniformStepSizeFlag, updateType)

  def nestedMultiOutputInstance[T : Manifest, C, S[X] : SeqIndexAccessible](
    stateProducers : Iterable[_ <: ComposedStateProducer[T, C, S]],
    updateType : MultiStateUpdateType,
    uniformStepSizeFlag : Boolean
  ): ComposedStateProducer[T, C, S] =
    if (updateType == MultiStateUpdateType.AsyncFixedRandomOrder)
      new ComposedNestedMultiOutputStateProducer[T, C, S](Random.shuffle(stateProducers), uniformStepSizeFlag, updateType)
    else
      new ComposedNestedMultiOutputStateProducer[T, C, S](stateProducers, uniformStepSizeFlag, updateType)

  def singleOutputInstance[T : Manifest, C, S[X] : SeqIndexAccessible](
    stateProducers : Iterable[_ <: SingleStateProducer[T, C, S]],
    updateType : MultiStateUpdateType,
    uniformStepSizeFlag : Boolean
  ): ComposedStateProducer[T, C, S] =
    if (updateType == MultiStateUpdateType.AsyncFixedRandomOrder)
      new ComposedSingleOutputStateProducer[T, C, S](Random.shuffle(stateProducers), uniformStepSizeFlag, updateType)
    else
      new ComposedSingleOutputStateProducer[T, C, S](stateProducers, uniformStepSizeFlag, updateType)
}