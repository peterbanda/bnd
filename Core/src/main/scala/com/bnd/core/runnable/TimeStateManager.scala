package com.bnd.core.runnable

import java.math.{MathContext, RoundingMode}
import java.{lang => jl, util => ju}

import com.bnd.core.{BndRuntimeException, DoubleConvertible}
import com.bnd.core.dynamics.ODESolver
import com.bnd.core.runnable.SeqIndexAccessible.Implicits._
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible.Implicits.minusAsDouble

import scala.collection.JavaConversions._
import scala.collection.{Map, mutable}
import scala.collection.mutable.{ListBuffer, Publisher, Map => MMap}
import scala.math.BigDecimal
import scala.math.BigDecimal._
import scala.math.Numeric.Implicits._

/**
  * @author © Peter Banda
  * @since 2013
  */
trait AbstractTimeStateManager[T, C, S[X]] extends TimeRunnable with StateHolder[T, C, S] with mutable.Publisher[StateEvent[T, S]] {

  private[runnable] def fixedPointDetector: Option[FixedPointDetector[T]]

  val zero: BigDecimal = 0
  val mathContext = new MathContext(20, RoundingMode.HALF_EVEN)

  private[runnable] var fixedPointEvalTime = BigDecimal(0d)
  private[runnable] var time = BigDecimal(0d)
  private[runnable] var fixedPoint = false

  def isFixedPoint = fixedPoint

  override def runUntil(finalTime: scala.math.BigDecimal) =
    if (finalTime >= time) runUntilSafe(finalTime)

  override def runFor(timeDiff: scala.math.BigDecimal) =
    if (timeDiff >= zero) runForSafe(timeDiff)

  private[runnable] def runForSafe(timeDiff: BigDecimal) = runUntilSafe(timeDiff + time)

  private[runnable] def isFixedPoint(
    newStates: S[T],
    newTimeStep: Double
  ): Boolean

  private[runnable] def runUntilSafe(finalTime: BigDecimal) {
    // publish initial state
    publishStatesNow

    if (fixedPoint)
      time = finalTime
    else while (time < finalTime) {
      if (fixedPointDetector.isDefined && fixedPointEvalTime <= time) {
        val (newTimeStep, newStates) = newTimeStepAndState(finalTime - time)
        fixedPoint = isFixedPoint(newStates, newTimeStep.doubleValue)
        fixedPointEvalTime = time + fixedPointDetector.get.waitTime
        states = newStates
        if (fixedPoint) time = finalTime else time += newTimeStep
      } else
        updateTimeAndState(finalTime - time)

      if (time < finalTime) publishStatesNow else time = finalTime
    }
  }

  private[runnable] def toBigDecimal(timeStep: Double) =
    try
      BigDecimal(timeStep, mathContext)
    catch {
      case e: NumberFormatException =>
        throw new TimeStepUndefinedException(timeStep + " is not a number")
    }

  override def currentTime = time

  private[runnable] def updateTimeAndState(maxTimeStep: BigDecimal) = {
    val (newTimeStep, newStates) = newTimeStepAndState(maxTimeStep)
    states = newStates
    time += newTimeStep
  }

  private[runnable] def newTimeStepAndState(maxTimeStep: BigDecimal): (BigDecimal, S[T])

  private[runnable] def nonFixedPointTimeStep: Double

  override def nextTimeStepSize = if (fixedPoint) BigDecimal.valueOf(Long.MaxValue) else toBigDecimal(nonFixedPointTimeStep)

  private[runnable] def publishStatesNow: Unit = publishStates(time)

  private[runnable] def publishStates(time: BigDecimal): Unit
}

/**
  * @author © Peter Banda
  * @since 2013
  */
private abstract class TimeStateManager[T: Manifest, C, S[X] : SeqIndexAccessible](
  components_ : Iterable[C],
  fixedPointDetector_ : Option[FixedPointDetector[T]],
  defaultState: Option[T] = None)(
  implicit jlc: JavaListConvertible[S]
) extends SeqIndexAccessibleStateHolder[T, C, S](components_) with AbstractTimeStateManager[T, C, S] with Serializable {

  if (defaultState.isDefined) fillStates(components_.size, defaultState.get)

  override private[runnable] def fixedPointDetector = fixedPointDetector_

  override def setStates(newStates: ju.List[T]) = {
    if (fixedPointDetector.isDefined)
      fixedPoint &&= fixedPointDetector.get.isFixedPoint(states.toSeq, newStates, nonFixedPointTimeStep)
    super.setStates(newStates)
  }

  override def setState(
    component: C,
    newState: T
  ) = {
    if (fixedPointDetector.isDefined) {
      val oldState = getState(component)
      fixedPoint &&= fixedPointDetector.get.isFixedPoint(List(oldState), List(newState), nonFixedPointTimeStep)
    }
    super.setState(component, newState)
  }

  override private[runnable] def isFixedPoint(
    newStates: S[T],
    newTimeStep: Double
  ) =
    fixedPointDetector.get.isFixedPoint(states.toSeq, newStates.toSeq, newTimeStep)

  override private[runnable] def publishStates(time: BigDecimal) = publish(new StateUpdatedEvent(time, components, states))
}

private class SingleTimeStateManager[T: Manifest, C, S[X] : SeqIndexAccessible](
  stateProducer: StateProducer[T, C, S],
  fixedPointDetector_ : Option[FixedPointDetector[T]],
  defaultState: Option[T] = None)(
  implicit jlc: JavaListConvertible[S]
) extends TimeStateManager[T, C, S](
  (stateProducer.listInputComponentsInOrder ++ stateProducer.listOutputComponentsInOrder).toList.distinct,
  fixedPointDetector_, defaultState) with Serializable {

  protected val initialTimeStep = toBigDecimal(stateProducer.nextTimeStep)
  protected val initialTimeStepDouble = stateProducer.nextTimeStep

  override private[runnable] def runUntilSafe(finalTime: BigDecimal) =
    if (stateProducer.isConstantTimeStep)
      runUntilSafeConstantTimeStep(finalTime)
    else
      super.runUntilSafe(finalTime)

  private[runnable] def runUntilSafeConstantTimeStep(finalTime: BigDecimal) {
    // publish initial state
    publishStatesNow

    val repetitions = ((finalTime - time) / initialTimeStep).intValue

    if (fixedPoint)
      time = finalTime
    else {
      var iteration = 0;
      while (!fixedPoint && iteration < repetitions) {
        if (fixedPointDetector.isDefined && fixedPointEvalTime <= time) {
          val newStates = stateProducer.nextState(states, None)
          fixedPoint = isFixedPoint(newStates, initialTimeStepDouble)
          fixedPointEvalTime = time + fixedPointDetector.get.waitTime
          states = newStates
          if (fixedPoint) time = finalTime else time += initialTimeStep
        } else
          updateConstantTimeAndState

        publishStatesNow
        iteration += 1
      }
    }
    if (time < finalTime) updateTimeAndState(finalTime - time)
  }

  override def newTimeStepAndState(maxTimeStep: BigDecimal) = {
    val plannedTimeStep = toBigDecimal(stateProducer.nextTimeStep)
    if (plannedTimeStep > maxTimeStep)
      (maxTimeStep, stateProducer.nextState(states, Some(maxTimeStep.doubleValue)))
    else
      (plannedTimeStep, stateProducer.nextState(states, None))
  }

  private[runnable] def updateConstantTimeAndState = {
    states = stateProducer.nextState(states, None)
    time += initialTimeStep
  }

  override private[runnable] def nonFixedPointTimeStep =
    if (stateProducer.isConstantTimeStep) initialTimeStepDouble else stateProducer.nextTimeStep
}

/**
  * @author © Peter Banda
  * @since 2013
  */
private class StateUpdateableSingleTimeStateManager[T: Manifest, C, S[X] : SeqIndexAccessible](
  stateProducer: StateProducer[T, C, S] with StateUpdateable[T, S],
  fixedPointDetector_ : Option[FixedPointDetector[T]],
  defaultState: Option[T] = None
)
  (
    implicit jlc: JavaListConvertible[S]
  ) extends SingleTimeStateManager[T, C, S](stateProducer, fixedPointDetector_, defaultState) {

  // StateUpdateable optimization, clean up a bit
  override def updateTimeAndState(maxTimeStep: BigDecimal) {
    val plannedTimeStep = toBigDecimal(stateProducer.nextTimeStep)
    if (plannedTimeStep > maxTimeStep) {
      stateProducer.updateState(states, Some(maxTimeStep.doubleValue))
      time += maxTimeStep
    } else {
      stateProducer.updateState(states, None)
      time += plannedTimeStep
    }
  }

  override private[runnable] def updateConstantTimeAndState = {
    stateProducer.updateState(states, None)
    time += initialTimeStep
  }
}

private class InteractiveTimeStateManager[T: Manifest, C, H, S[X] : SeqIndexAccessible](
  stateProducer: StateProducer[T, C, S],
  fixedPointDetector: Option[FixedPointDetector[T]],
  defaultState: Option[T] = None,
  publishBeforeAlterTimeStep: Option[BigDecimal] = None,
  private var script: Stream[_ <: StateAlternation[T, C, H]]
)
  (
    implicit jlc: JavaListConvertible[S]
  ) extends SingleTimeStateManager[T, C, S](stateProducer, fixedPointDetector, defaultState) {

  private var activeAlternations = new ListBuffer[StateAlternation[T, C, H]]
  private val cache = MMap[H, T]()

  override private[runnable] def runUntilSafe(finalTime: BigDecimal) {
    var applicableAlternations = script.takeWhile(_.applyStartTime < finalTime).toList
    script = script.dropWhile(_.applyStartTime < finalTime)

    def handleAlternations = {
      // remove expired state alternators
      activeAlternations = activeAlternations.filter(s => time < s.applyStartTime + s.timeLength)

      // add new active state alternators
      val pair = splitWhile[StateAlternation[T, C, H]](s => s.applyStartTime <= time)(applicableAlternations)
      activeAlternations ++= pair._1
      applicableAlternations = pair._2

      val stateMap: Map[C, T] = componentStates.toMap

      // cache writes of new alternations
      val newCacheValues =
        for (newAlternation <- pair._1; cacheWrite <- newAlternation.cacheWrites) yield {
          val newCacheValue = cacheWrite.fun(stateMap, cache)
          (cacheWrite.variable, newCacheValue)
        }
      newCacheValues.foreach { case (variable, value) => cache.put(variable, value) }

      val timeStep = Some(nextTimeStepSize.toDouble)

      // alter states
      activeAlternations.map(activeStateAlternation => {
        val newComponentStates = activeStateAlternation.items.map(item => {
          val newState = item.fun(stateMap, cache, timeStep)
          setState(item.component, newState)
          (item.component, newState)
        })
        publish(new StateAlteredEvent(currentTime, newComponentStates))
      })
    }

    // calc next time to alternation
    def nextTimeToAlternation = {
      val maxTime = if (applicableAlternations.isEmpty) finalTime else {
        val nextAlternationStartTime = applicableAlternations.head.applyStartTime
        if (nextAlternationStartTime > time && nextAlternationStartTime < finalTime)
          if (publishBeforeAlterTimeStep.isDefined) {
            val beforeAlternationTime = nextAlternationStartTime - publishBeforeAlterTimeStep.get
            if (beforeAlternationTime > time) beforeAlternationTime else nextAlternationStartTime
          } else nextAlternationStartTime
        else finalTime
      }
      maxTime - time
    }

    if (time == finalTime)
      publishStatesNow
    else while (time < finalTime) {
      // main loop
      handleAlternations
      val timeToAlternation = nextTimeToAlternation
      if (activeAlternations.isEmpty)
        super.runUntilSafe(timeToAlternation + time)
      else {
        publishStatesNow
        updateTimeAndState(timeToAlternation)
      }
    }
  }

  override def nextTimeStepSize: BigDecimal = {
    val nfpTimeStep = toBigDecimal(nonFixedPointTimeStep)

    val timeStep = if (!activeAlternations.isEmpty || (!script.isEmpty && script.head.applyStartTime == time))
      nfpTimeStep
    else if (fixedPoint) BigDecimal.valueOf(Long.MaxValue) else nfpTimeStep

    val nextAlternationTimeStep = {
      val nextAlternation = script.find(_.applyStartTime > time)
      if (nextAlternation.isDefined) Some(nextAlternation.get.applyStartTime - time) else None
    }
    if (!nextAlternationTimeStep.isDefined || nextAlternationTimeStep.get > timeStep)
      timeStep
    else if (publishBeforeAlterTimeStep.isDefined && nextAlternationTimeStep.get > publishBeforeAlterTimeStep.get)
      nextAlternationTimeStep.get - publishBeforeAlterTimeStep.get
    else
      nextAlternationTimeStep.get
  }
}

private class StateUpdateableInteractiveTimeStateManager[T: Manifest, C, H, S[X] : SeqIndexAccessible](
  stateProducer: StateProducer[T, C, S] with StateUpdateable[T, S],
  fixedPointDetector: Option[FixedPointDetector[T]],
  defaultState: Option[T] = None,
  publishBeforeAlterTimeStep: Option[BigDecimal] = None,
  private var script: Stream[_ <: StateAlternation[T, C, H]])(
  implicit jlc: JavaListConvertible[S]
) extends StateUpdateableSingleTimeStateManager[T, C, S](stateProducer, fixedPointDetector, defaultState) {

  private var activeAlternations = new ListBuffer[StateAlternation[T, C, H]]
  private val cache = MMap[H, T]()

  override private[runnable] def runUntilSafe(finalTime: BigDecimal) {
    var applicableAlternations = script.takeWhile(_.applyStartTime < finalTime).toList
    script = script.dropWhile(_.applyStartTime < finalTime)

    def handleAlternations = {
      // remove expired state alternators
      activeAlternations = activeAlternations.filter(s => time < s.applyStartTime + s.timeLength)

      // add new active state alternators
      val pair = splitWhile[StateAlternation[T, C, H]](s => s.applyStartTime <= time)(applicableAlternations)
      activeAlternations ++= pair._1
      applicableAlternations = pair._2

      val stateMap: Map[C, T] = componentStates.toMap

      // cache writes of new alternations
      val newCacheValues =
        for (newAlternation <- pair._1; cacheWrite <- newAlternation.cacheWrites) yield {
          val newCacheValue = cacheWrite.fun(stateMap, cache)
          (cacheWrite.variable, newCacheValue)
        }
      newCacheValues.foreach { case (variable, value) => cache.put(variable, value) }

      val timeStep = Some(nextTimeStepSize.toDouble)

      // alter states
      activeAlternations.map(activeStateAlternation => {
        val newComponentStates = activeStateAlternation.items.map(item => {
          val newState = item.fun(stateMap, cache, timeStep)
          setState(item.component, newState)
          (item.component, newState)
        })
        publish(new StateAlteredEvent(currentTime, newComponentStates))
      })
    }

    // calc next time to alternation
    def nextTimeToAlternation = {
      val maxTime = if (applicableAlternations.isEmpty) finalTime else {
        val nextAlternationStartTime = applicableAlternations.head.applyStartTime
        if (nextAlternationStartTime > time && nextAlternationStartTime < finalTime)
          if (publishBeforeAlterTimeStep.isDefined) {
            val beforeAlternationTime = nextAlternationStartTime - publishBeforeAlterTimeStep.get
            if (beforeAlternationTime > time) beforeAlternationTime else nextAlternationStartTime
          } else nextAlternationStartTime
        else finalTime
      }
      maxTime - time
    }

    if (time == finalTime)
      publishStatesNow
    else while (time < finalTime) {
      // main loop
      handleAlternations
      val timeToAlternation = nextTimeToAlternation
      if (activeAlternations.isEmpty)
        super.runUntilSafe(timeToAlternation + time)
      else {
        publishStatesNow
        updateTimeAndState(timeToAlternation)
      }
    }
  }

  override def nextTimeStepSize: BigDecimal = {
    val nfpTimeStep = toBigDecimal(nonFixedPointTimeStep)

    val timeStep = if (!activeAlternations.isEmpty || (!script.isEmpty && script.head.applyStartTime == time))
      nfpTimeStep
    else if (fixedPoint) BigDecimal.valueOf(Long.MaxValue) else nfpTimeStep

    val nextAlternationTimeStep = {
      val nextAlternation = script.find(_.applyStartTime > time)
      if (nextAlternation.isDefined) Some(nextAlternation.get.applyStartTime - time) else None
    }
    if (!nextAlternationTimeStep.isDefined || nextAlternationTimeStep.get > timeStep)
      timeStep
    else if (publishBeforeAlterTimeStep.isDefined && nextAlternationTimeStep.get > publishBeforeAlterTimeStep.get)
      nextAlternationTimeStep.get - publishBeforeAlterTimeStep.get
    else
      nextAlternationTimeStep.get
  }
}

class ContinuousCrossModuleAlternation[T, C, M](
  val inputComponents: Iterable[(M, C)],
  val outputComponents: Iterable[(M, C)],
  val timeDiffFun: (Iterable[T], Option[Double]) => Iterable[T],
  val addFun: (T, T) => T
)

class ContinuousCrossModuleTransport[T, C, M](
  minus: T => T,
  add: (T, T) => T)(
  singleTimeDiffFun: (T, Option[Double]) => T)(
  source: (M, C),
  target: (M, C)
) extends ContinuousCrossModuleAlternation[T, C, M](
  List(source),
  List(source, target),
  { (inputs: Iterable[T], timeStep: Option[Double]) =>
    val sourceState = inputs.head
    val diff = singleTimeDiffFun(sourceState, timeStep)
    List(minus(diff), diff)
  },
  add
)

object ContinuousCrossModuleTransport {

  def numericInstance[T: Numeric, C, M] = new ContinuousCrossModuleTransport[T, C, M](-_, _ + _)(_: (T, Option[Double]) => T)(_: (M, C), _: (M, C))

  private def addAsDoubleBounded[T](
    lowerValue: T,
    lowerBound: Option[T])(
    a: T,
    b: T)(
    implicit d: DoubleConvertible[T]
  ) = {
    val value = d.toDouble(a) + d.toDouble(b)
    if (lowerBound.isDefined && value < d.toDouble(lowerBound.get)) lowerValue else d.fromDouble(value)
  }

  def doubleBoundedInstance[T, C, M](
    lowerValue: T,
    lowerBound: Option[T])(
    implicit d: DoubleConvertible[T]
  ) = {
    val add = addAsDoubleBounded(lowerValue, lowerBound) _
    new ContinuousCrossModuleTransport[T, C, M](minusAsDouble, add)(_: (T, Option[Double]) => T)(_: (M, C), _: (M, C))
  }

  def odeSolverInstance[T, C, M](
    odeSolver: ODESolver,
    lowerValue: T,
    lowerBound: Option[T])(
    implicit d: DoubleConvertible[T],
    m: ClassManifest[T]
  ) =
    doubleBoundedInstance[T, C, M](lowerValue, lowerBound)(d)(
    { (input: T, timeDiff: Option[Double]) =>
      // TODO
      val result = if (timeDiff.isDefined) {
        if (timeDiff.get < odeSolver.getTimeStep())
          odeSolver.getApproxDiffs(Array[jl.Double](d.toDouble(input)), timeDiff.get)
        else
          odeSolver.getApproxDiffs(Array[jl.Double](d.toDouble(input)))

        //                val finalTime = timeDiff.get
        //                var currentTime = 0d
        //                while (currentTime < finalTime) {
        //                    val step = finalTime - currentTime
        //                	if (step < odeSolver.getTimeStep()) {
        //                		odeSolver.getApproxDiffs(Array[jl.Double](d.toDouble(input)), step)
        //                		currentTime += step
        //                	} else {
        //                		odeSolver.getApproxDiffs(Array[jl.Double](d.toDouble(input)))
        //                		currentTime += odeSolver.getTimeStep()
        //                	}
        //                }
      } else
        odeSolver.getApproxDiffs(Array[jl.Double](d.toDouble(input)))

      d.fromDouble(result(0))
    }, _: (M, C), _: (M, C))
}

private abstract class ContainerTimeStateManager[T, C, M](
  private val moduleRunnables: Iterable[(M, TimeRunnable with FullStateAccessible[T, C])],
  private val crossModuleAlternations: Iterable[ContinuousCrossModuleAlternation[T, C, M]]
) extends TimeRunnable with FullStateAccessible[T, (M, C)] {

  val zero: BigDecimal = 0

  type TimeStateRunnable = TimeRunnable with FullStateAccessible[T, C]

  private[runnable] val moduleRunnablesMap = moduleRunnables.toMap

  private[runnable] val runnables = moduleRunnables.map(_._2)

  private[runnable] val stateCounts = runnables.map(_.getStates.size)

  private[runnable] def states = runnables.map(_.getStates).flatten.toSeq

  // state functions

  override def getStates = states

  override def setStates(states: ju.List[T]) {
    var remainingStates = states
    (runnables, stateCounts).zipped.map {
      (runnable, stateCount) =>
        val pair = remainingStates.splitAt(stateCount)
        runnable.setStates(pair._1)
        remainingStates = pair._2
    }
  }

  override def setState(
    moduleComponent: (M, C),
    state: T
  ) {
    val runnable = moduleRunnablesMap.get(moduleComponent._1)
    if (runnable.isDefined)
      runnable.get.setState(moduleComponent._2, state)
  }

  override def getState(moduleComponent: (M, C)) = {
    val runnable = moduleRunnablesMap.get(moduleComponent._1)
    if (runnable.isDefined)
      runnable.get.getState(moduleComponent._2)
    else throw new BndRuntimeException("State for module " + moduleComponent._1 + " not found.")
  }

  override def componentStates =
    moduleRunnables.map {
      case (module, runnable) => runnable.componentStates.map {
        case (component, state) => ((module, component), state)
      }
    }.flatten

  // run time functions

  override def nextTimeStepSize = minTimeRunnable.nextTimeStepSize

  override def currentTime = minTimeRunnable.currentTime

  override def runUntil(finalTime: scala.math.BigDecimal) =
    if (finalTime >= currentTime) runUntilSafe(finalTime)

  override def runFor(timeDiff: scala.math.BigDecimal) =
    if (timeDiff >= zero) runForSafe(timeDiff)

  private[runnable] def runUntilSafe(finalTime: BigDecimal): Unit

  private[runnable] def runForSafe(timeDiff: BigDecimal) = runUntilSafe(timeDiff + currentTime)

  private[runnable] def execAlternation(
    alternation: ContinuousCrossModuleAlternation[T, C, M],
    timeDiff: Option[Double],
    inStates: Iterable[T]
  ) {
    val outDiffs = alternation.timeDiffFun(inStates, timeDiff)
    (alternation.outputComponents, outDiffs).zipped.foreach((
    mc,
    diff
    ) => setState(mc, alternation.addFun(getState(mc), diff)))
  }

  private[runnable] def execAlternations(
    alternations: Iterable[ContinuousCrossModuleAlternation[T, C, M]]
  ) =
    alternations.foreach(alternation => execAlternation(alternation, None, alternation.inputComponents.map(getState)))

  // helper functions

  private[runnable] def minTimeRunnable = runnables.minBy(_.currentTime)

  private[runnable] def minTimeModuleRunnable = moduleRunnables.minBy(_._2.currentTime)

  private[runnable] def maxTimeRunnable = runnables.maxBy(_.currentTime)

  private[runnable] def maxTimeModuleRunnable = moduleRunnables.maxBy(_._2.currentTime)
}

private class AsyncContainerTimeStateManager[T, C, M](
  moduleRunnables: Iterable[(M, TimeRunnable with FullStateAccessible[T, C])],
  crossModuleAlternations: Iterable[ContinuousCrossModuleAlternation[T, C, M]]
) extends ContainerTimeStateManager[T, C, M](moduleRunnables, crossModuleAlternations) {

  override def nextTimeStepSize = minTimeRunnable.nextTimeStepSize

  override private[runnable] def runUntilSafe(finalTime: BigDecimal) {
    while (maxTimeRunnable.currentTime < finalTime) {
      // get module and its runnable with least time
      val leastModuleRunnable = minTimeModuleRunnable
      val module = leastModuleRunnable._1
      val runnable = leastModuleRunnable._2

      // collect alternations where current module's state is effected
      val activeAlternations = crossModuleAlternations.filter(alternation =>
        alternation.outputComponents.exists(_._1 == module))

      // get input states
      val inStates = activeAlternations.map(_.inputComponents.map(getState))

      // run least runnable and get its time step
      val startTime = runnable.currentTime
      runnable.runFor(runnable.nextTimeStepSize)
      val timeStep = runnable.currentTime - startTime

      // execute active alternations
      (activeAlternations, inStates).zipped.foreach { case (alternation, states) =>
        val outputModulesCount = alternation.outputComponents.map(_._1).toSet.size
        val alternationTimeStep = (timeStep / outputModulesCount).toDouble
        execAlternation(alternation, Some(alternationTimeStep), states)
      }
    }
  }
}

private class SyncContainerTimeStateManager[T, C, M](
  moduleRunnables: Iterable[(M, TimeRunnable with FullStateAccessible[T, C])],
  crossModuleAlternations: Iterable[ContinuousCrossModuleAlternation[T, C, M]]
) extends ContainerTimeStateManager[T, C, M](moduleRunnables, crossModuleAlternations) {

  override def nextTimeStepSize = minTimeRunnable.nextTimeStepSize

  override private[runnable] def runUntilSafe(finalTime: BigDecimal) {
    while (minTimeRunnable.currentTime < finalTime) {
      // get input states
      val inStates = crossModuleAlternations.map(_.inputComponents.map(getState))

      //			debug
      //		    print("Time ")
      //		    println(currentTime)
      //		    println("Next time step")
      //		    runnables.foreach{r => print(r.nextTimeStepSize.toString + ",")}
      //		    println
      //
      //		    println("Before States")
      //		    runnables.foreach{r => println(r.getStates)}

      // get the slowest one
      val slowestRunnable = runnables.minBy(_.nextTimeStepSize)

      val nextTimeStep = slowestRunnable.nextTimeStepSize.min(finalTime - slowestRunnable.currentTime)
      // run the slowest
      slowestRunnable.runFor(nextTimeStep)
      val toTime = slowestRunnable.currentTime
      // run the rest
      var nextRunnable = minTimeRunnable
      while (nextRunnable.currentTime != toTime) {
        // get module and its runnable with least time
        nextRunnable.runUntil(toTime)
        nextRunnable = minTimeRunnable
      }

      val timeStep = nextTimeStep.doubleValue

      // execute alternations
      (crossModuleAlternations, inStates).zipped.foreach { case (alternation, states) => execAlternation(alternation, Some(timeStep), states) }

      //			debug
      //		    println("After States")
      //		    runnables.foreach{r => println(r.getStates)}
      //		    println
    }
  }
}

object TimeStateManager {

  type TimeStateRunnable[T] = TimeRunnable with StateAccessible[T]
  type TimeFullStateRunnable[T, C] = TimeRunnable with FullStateAccessible[T, C]
  type StatePublisher[T, S[X]] = Publisher[StateEvent[T, S]]

  def apply[T: Manifest, C, S[X] : SeqIndexAccessible](
    stateProducer: StateProducer[T, C, S],
    fixedPointDetector: Option[FixedPointDetector[T]] = None,
    defaultState: Option[T] = None)(
    implicit jlc: JavaListConvertible[S]
  ): TimeFullStateRunnable[T, C] with StatePublisher[T, S] = new SingleTimeStateManager(stateProducer, fixedPointDetector, defaultState)

  def stateUpdateableInstance[T: Manifest, C, S[X] : SeqIndexAccessible](
    stateProducer: StateProducer[T, C, S] with StateUpdateable[T, S],
    fixedPointDetector: Option[FixedPointDetector[T]] = None,
    defaultState: Option[T] = None)(
    implicit jlc: JavaListConvertible[S]
  ): TimeFullStateRunnable[T, C] with StatePublisher[T, S] = new StateUpdateableSingleTimeStateManager(stateProducer, fixedPointDetector, defaultState)

  def interactiveInstance[T: Manifest, C, H, S[X] : SeqIndexAccessible](
    stateProducer: StateProducer[T, C, S],
    script: Stream[_ <: StateAlternation[T, C, H]],
    fixedPointDetector: Option[FixedPointDetector[T]] = None,
    defaultState: Option[T] = None,
    publishBeforeAlterTimeDiff: Option[BigDecimal] = None)(
    implicit jlc: JavaListConvertible[S]
  ): TimeFullStateRunnable[T, C] with StatePublisher[T, S] = new InteractiveTimeStateManager(stateProducer, fixedPointDetector, defaultState, publishBeforeAlterTimeDiff, script)

  def stateUpdateableInteractiveInstance[T: Manifest, C, H, S[X] : SeqIndexAccessible](
    stateProducer: StateProducer[T, C, S] with StateUpdateable[T, S],
    script: Stream[_ <: StateAlternation[T, C, H]],
    fixedPointDetector: Option[FixedPointDetector[T]] = None,
    defaultState: Option[T] = None,
    publishBeforeAlterTimeDiff: Option[BigDecimal] = None)(
    implicit jlc: JavaListConvertible[S]
  ): TimeFullStateRunnable[T, C] with StatePublisher[T, S] = new StateUpdateableInteractiveTimeStateManager(stateProducer, fixedPointDetector, defaultState, publishBeforeAlterTimeDiff, script)

  def asyncContainerInstance[T, C, M](
    moduleRunnables: Iterable[(M, TimeRunnable with FullStateAccessible[T, C])],
    crossModuleAlternations: Iterable[ContinuousCrossModuleAlternation[T, C, M]]
  ): TimeFullStateRunnable[T, (M, C)] = new AsyncContainerTimeStateManager(moduleRunnables, crossModuleAlternations)

  def syncContainerInstance[T, C, M](
    moduleRunnables: Iterable[(M, TimeRunnable with FullStateAccessible[T, C])],
    crossModuleAlternations: Iterable[ContinuousCrossModuleAlternation[T, C, M]]
  ): TimeFullStateRunnable[T, (M, C)] = new SyncContainerTimeStateManager(moduleRunnables, crossModuleAlternations)
}