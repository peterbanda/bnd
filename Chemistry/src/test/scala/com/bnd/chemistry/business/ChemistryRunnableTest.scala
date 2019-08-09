package com.bnd.chemistry.business

import java.{lang => jl}

import scala.collection.JavaConversions._
import scala.util.Random
import org.junit.Assert._
import org.junit.BeforeClass
import org.junit.Test
import com.bnd.chemistry.domain._
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible
import com.bnd.core.DoubleConvertible.JavaDoubleAsDoubleConvertible
import com.bnd.core.runnable.DistanceFixedPointDetector
import com.bnd.core.runnable.FixedPointDetector
import com.bnd.core.runnable.TimeStateManager
import com.bnd.plotter.Plotter
import com.bnd.function.domain.ODESolverType
import com.bnd.math.domain.rand.RandomDistribution
import com.bnd.chemistry.business.ChemistryTestDataGenerator._
import java.{lang => jl}

import com.bnd.core.runnable.StateAccessible

import scala.collection.mutable.Publisher
import ChemistryRunnableTest._
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import com.bnd.core.runnable.StateCollector
import com.bnd.core.runnable.StateEvent
import com.bnd.core.runnable.ComponentStateCollector
import com.bnd.core.runnable.SeqIndexAccessible._
import com.bnd.core.runnable.SeqIndexAccessible.Implicits._
import com.bnd.core.runnable.SeqIndexAccessible
import com.bnd.plotter.{Plotter, SeriesPlotSetting}
import com.bnd.core.runnable.{TimeRunnable, TimeStateManager}

object ChemistryRunnableTest {

  val time = 1000
  val repetitions = 2000
  val odeSolverType = ODESolverType.RungeKutta4
  val timeStep = 0.5
  val displayPlot = false

  val simConfig = createSimulationConfig(odeSolverType, timeStep)
  val twoToTwoChemSpec = new AcSymmetricSpec() {
    setSpeciesNum(10)
    setReactionNum(10)
    setReactantsPerReactionNumber(1)
    setProductsPerReactionNumber(2)
    setCatalystsPerReactionNumber(1)
    setInhibitorsPerReactionNumber(0)
    setSpeciesForbiddenRedundancy(AcReactionSpeciesForbiddenRedundancy.None)
    setRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.1, 0.02))
    //        setConstantSpeciesNum(4)
    setInfluxRatio(0d)
    setInfluxRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.002, 0d))
    setOutfluxRatio(0.1)
    setOutfluxRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.001, 0d))
  }

  val chemistryFactory = new ChemistryRunnableFactory(functionEvaluatorFactory)
  val plotter = Plotter.apply
  val acUtil = ArtificialChemistryUtil.getInstance

  var initialStates: Iterable[Seq[Double]] = _
  var compartments: Iterable[AcCompartment] = _
  var fixedPointDetector: FixedPointDetector[jl.Double] = _

  @BeforeClass
  def initialize {
    compartments = for (_ <- 1 to repetitions) yield {
      val reactionSet = reactionSet1(twoToTwoChemSpec)
      new AcCompartment() {
        setReactionSet(reactionSet)
      }
    }

    initialStates = for (compartment <- compartments) yield
      for (i <- 1 to compartment.getSpecies.size) yield Random.nextDouble

    fixedPointDetector = new DistanceFixedPointDetector(0.0001: jl.Double, 50d)
  }
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ChemistryRunnableTest extends ScalaChemistryTest {

  @Test
  def test1 {

  }

  @Test
  def test2Chemistry = (compartments, initialStates).zipped.foreach(runChemistry(_, None)(_, false))

  @Test
  def test3ChemistryFixedPoint = (compartments, initialStates).zipped.foreach(runChemistry(_, Some(fixedPointDetector))(_, false))

  @Test
  def test4ChemistryListener = (compartments, initialStates).zipped.foreach(runChemistry(_, None)(_, true))

  @Test
  def test5ChemistryFixedPointListener = (compartments, initialStates).zipped.foreach(runChemistry(_, Some(fixedPointDetector))(_, true))

  @Test
  def test6ChemistryHierarchical = (compartments, initialStates).zipped.foreach(runHierarchicalChemistry(_, _, false))

  @Test
  def test8Correctness = (compartments, initialStates).zipped.foreach(testCorrectness)

  def testCorrectness(compartment: AcCompartment, is: Seq[Double]) {
    val initialState = is: Seq[jl.Double]

    val chemistryProducer = FlatChemistryProducer.applyArray(compartment, simConfig)
    val chemistryNew = TimeStateManager.stateUpdateableInstance(chemistryProducer)
    chemistryNew.setStates(initialState)
    val timeStateCollector = new StateCollector[jl.Double, Array]
    chemistryNew.subscribe(timeStateCollector)
    chemistryNew.runFor(time)
    val chemistryNewStates = timeStateCollector.collected

    //    	val chemistryOldStates = getStatesOld(compartment, ac.getSimulationConfig, initialState)
    //
    //        chemistryNewStates.foreach(println)
    //        println
    //        chemistryOldStates.foreach(println)
    //        println

    //	   	(chemistryNewStates.map(_._2).flatten, chemistryOldStates.map(_._2).flatten).zipped.map(assertEquals(_,_,0.001))
  }

  def runChemistry(
    compartment: AcCompartment,
    fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None
  ) = {
    val chemistryProducer = FlatChemistryProducer.applyArray(compartment, simConfig)
    runSystem(TimeStateManager.stateUpdateableInstance(chemistryProducer, fixedPointDetector), _: Seq[jl.Double], _: Boolean)
  }

  def runSystem[T: DoubleConvertible, S[X] : SeqIndexAccessible](
    system: TimeRunnable with StateAccessible[T] with Publisher[StateEvent[T, S]],
    initialState: Seq[T],
    collectStates: Boolean = false
  )(implicit m: Manifest[T]) {
    var timeStateCollector = new ComponentStateCollector[T, Any, S]
    if (collectStates || displayPlot) {
      system.subscribe(timeStateCollector)
    }
    system.setStates(initialState)
    system.runFor(time)
    if (displayPlot) {
      val plotSetting = new SeriesPlotSetting()
          .setTitle("System")
          .setTransposed(true)
          .setXAxis(timeStateCollector.collected.map(_._1.toDouble))

      plotter.plotSeries(timeStateCollector.collected.map(_._2.iterator.toIterable), plotSetting)
    }
  }

  def runHierarchicalChemistry(
    compartment: AcCompartment,
    initialState: Seq[jl.Double],
    collectStates: Boolean = false
  ) = {
    val containerWithModules = chemistryFactory.createNonInteractiveWithPublishers(compartment, simConfig, None)

    val containerChemistry = containerWithModules._1
    val compartmentChemistries = containerWithModules._2

    val compartmentStateCollectors = if (collectStates) {
      compartmentChemistries.map {
        case (compartment, chemistry) => {
          val stateCollector = new StateCollector[jl.Double, Array]
          chemistry.subscribe(stateCollector)
          (compartment, stateCollector)
        }
      }
    } else List.empty[(AcCompartment, StateCollector[jl.Double, Array])]

    val initialState = for (i <- 1 to containerChemistry.getStates.size) yield Random.nextDouble: jl.Double
    containerChemistry.setStates(initialState)

    containerChemistry.runFor(time)
  }
}