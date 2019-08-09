package com.bnd.function.business

import java.{lang => jl, util => ju}

import com.bnd.core.ClassUtil
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.dynamics.{ODESolver, StateAlternationType}
import com.bnd.core.runnable.FullStateAccessible
import com.bnd.function.business.ode.ODESolverFactory
import com.bnd.function.domain.ODESolverType
import com.bnd.core.dynamics.{ODESolver, StateAlternationType}
import com.bnd.core.util.ConversionUtil

import scala.collection.JavaConversions._

class NumberStateAlternator[T <: Number, C](
  alternationType: StateAlternationType,
  stateAccesible: FullStateAccessible[T, C],
  stateAlternations: ju.List[T],
  components: ju.List[C],
  startTime: jl.Double,
  applicableTimeLength: jl.Double,
  private val influxODESolverType: ODESolverType,
  private val influxTimeStepLength: jl.Double,
  private val influxScale: jl.Double)(
  implicit m: Manifest[T]) extends StateAlternator[T, C](
  alternationType, stateAccesible, stateAlternations, components, startTime, applicableTimeLength) {

  private val clazz = ClassUtil.extract[T]
  private val inputInfluxODESolver: ODESolver = {
    val influxScalaSafe: jl.Double = if (influxScale != null) influxScale else 1D
    val scaledInfluces: Array[jl.Double] = stateAlternations.map {
      _.doubleValue() * influxScalaSafe: jl.Double
    }
    val inputInfluxFun = new ConstantFunctionEvaluator[jl.Double, Array[jl.Double]](scaledInfluces)
    ODESolverFactory.createInstance(inputInfluxFun, influxODESolverType, influxTimeStepLength)
  }

  override protected def addToStates(stateDiffs: ju.List[T]) {
    val states = stateAccesible.getStates
    (components, stateDiffs).zipped.map { (component, stateDiff) =>
      val newValue = ConversionUtil.convert(stateAccesible.getState(component).doubleValue() + stateDiff.doubleValue(), clazz)
      stateAccesible.setState(component, newValue)
    }
    stateAccesible.setStates(states)
  }

  override protected def handleInflux {
    val filteredStates: Array[jl.Double] = components.map(stateAccesible.getState(_).doubleValue(): jl.Double)
    val diffs = inputInfluxODESolver.getApproxDiffs(filteredStates)
    val diffSeq = diffs.map {
      ConversionUtil.convert(_, clazz)
    }: Seq[T]
    addToStates(diffSeq)
  }
}