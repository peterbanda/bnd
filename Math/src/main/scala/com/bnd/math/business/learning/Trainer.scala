package com.bnd.math.business.learning

import java.util.ArrayList

import com.bnd.core.runnable._
import com.bnd.math.BndMathException
import com.bnd.math.domain.learning.MachineLearningSetting
import com.bnd.core.runnable.TimeRunnable
import com.bnd.core.runnable.SeqIndexAccessible.Implicits._

import scala.collection.mutable.{Publisher, Subscriber}

/**
 * Abstract class representing a trainer training underlying TimeRunnable.
 *
 * @author © Peter Banda
 * @since 2015
 */
abstract class Trainer[T: Manifest, C, S[X]: SeqIndexAccessible](
  calcOutputError : (T, T) => T,
  calcErrorMetrics : (Seq[T], Seq[T]) => T)(
  setting : MachineLearningSetting,
  trainingStream : IOStream[T])(
  learner : TimeRunnable with Publisher[StateEvent[T, S]],
  outputComponents : Iterable[C]
) extends Subscriber[StateEvent[T, S], Publisher[StateEvent[T, S]]] {

  // Training errors
  private val _errors = new ArrayList[T]
  private val _outputs = new ArrayList[T]

  // Desired output iterator
  private val desiredOutputIterator = trainingStream.outputStream.iterator

  // Component Indices
  private var componentIndices : Option[Map[C, Int]] = None

  // Training time iterator and current
  val trainingTimeIterator : Iterator[BigDecimal] = {
    val initialDelay : BigDecimal = setting.getInitialDelay : Double
    val singleIterationLength : BigDecimal = setting.getSingleIterationLength : Double
    val outputInterpretationRelativeTime : BigDecimal = setting.getOutputInterpretationRelativeTime : Double
    val initInterpretationTime = initialDelay + outputInterpretationRelativeTime + singleIterationLength * trainingStream.outputShift
    Stream.iterate(initInterpretationTime)(singleIterationLength + _)
  }.iterator

  protected var currentTrainingTime = trainingTimeIterator.next
  protected var currentTrainingIteration = 0

  // The trainer listens to the underlying learner
  learner.subscribe(this)

  def notify(pub: Publisher[StateEvent[T, S]], event: StateEvent[T, S]) =
    event match {
      case StateUpdatedEvent(time : BigDecimal, components : Iterable[C], state : S[T]) =>
        if (time >= currentTrainingTime) {
          if (!componentIndices.isDefined) componentIndices = Some(components.zipWithIndex.toMap)
          train(components, state)
        }
      case _ =>
    }

  def train(iterationNum: Int) : Unit =
    (1 to iterationNum).foreach{iteration =>
      currentTrainingIteration = iteration
      learner.runUntil(currentTrainingTime)
      // hacky solution for an inclusion/exclusion of the input/output time
      learner.runFor(0d)
      currentTrainingTime = trainingTimeIterator.next
    }

  private def train(components : Iterable[C], state : S[T]): Unit = {
//    println("State: " + state)
    if (!desiredOutputIterator.hasNext) {
      throw new BndMathException(s"No more desired outputs available for a trainer at time ${currentTrainingTime.toDouble}.")
    }

    val desiredOutputs = desiredOutputIterator.next
    val outputComponentStatePairs = outputComponents.map(outputComponent =>
        (outputComponent, componentState(outputComponent, state)))

    val outputComponentDesiredOutputErrorTuples = (outputComponentStatePairs, desiredOutputs).zipped.map{
      case ((outputComponent, output), desiredOutput) =>
        (outputComponent, desiredOutput, calcOutputError(desiredOutput, output))}

    val error = calcErrorMetrics(outputComponentStatePairs.map(_._2).toSeq, desiredOutputs)
    _errors.add(error)
    _outputs.add(outputComponentStatePairs.map(_._2).head)

    adapt(outputComponentDesiredOutputErrorTuples, state)
  }

  private[learning] def adapt(outputComponentErrorPairs : Iterable[(C, T, T)], state : S[T])

  protected def componentState(component: C, state: S[T]): T = state(componentIndices.get.get(component).get)

  def errors() = _errors

  def outputs() = _outputs
}