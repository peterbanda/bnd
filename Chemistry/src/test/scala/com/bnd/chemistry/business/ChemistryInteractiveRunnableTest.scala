package com.bnd.chemistry.business

import java.{lang => jl, util => ju}

import com.bnd.chemistry.business.ChemistryTestDataGenerator._
import com.bnd.chemistry.business.ChemistryInteractiveRunnableTest._
import com.bnd.chemistry.domain.{AcInteraction, AcInteractionSeries, AcSpecies, AcSpeciesInteraction, AcSymmetricSpec, _}
import com.bnd.core.CollectionElementsConversions._
import com.bnd.core.DoubleConvertible.JavaDoubleAsDoubleConvertible
import com.bnd.plotter.Plotter
import com.bnd.core.runnable.SeqIndexAccessible._
import com.bnd.core.runnable._
import com.bnd.core.util.RandomUtil
import com.bnd.function.domain.ODESolverType
import com.bnd.math.domain.rand.RandomDistribution
import com.bnd.plotter.{Plotter, SeriesPlotSetting}
import com.bnd.core.dynamics.StateAlternationType
import com.bnd.core.runnable.TimeStateManager
import com.bnd.core.util.RandomUtil
import org.junit.Assert._
import org.junit.runners.MethodSorters
import org.junit.{BeforeClass, FixMethodOrder, Test}

import scala.collection.JavaConversions._
import scala.collection.Map
import scala.collection.mutable.ListBuffer
import scala.util.Random

object ChemistryInteractiveRunnableTest {

  val time = 1500
  val repetitions = 2
  val odeSolverType = ODESolverType.RungeKutta4
  val timeStep = 0.1
  val displayPlot = true

  val simConfig = createSimulationConfig(odeSolverType, timeStep)

  val twoToTwoChemSpec = new AcSymmetricSpec() {
    setSpeciesNum(4)
    setReactionNum(4)
    setReactantsPerReactionNumber(2)
    setProductsPerReactionNumber(2)
    setCatalystsPerReactionNumber(0)
    setInhibitorsPerReactionNumber(0)
    setSpeciesForbiddenRedundancy(AcReactionSpeciesForbiddenRedundancy.None)
    setRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.1, 0.02))
    //        setConstantSpeciesNum(4)
    setInfluxRatio(0)
    setInfluxRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.002, 0d))
    setOutfluxRatio(0.1)
    setOutfluxRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.001, 0d))
  }

  val acScriptFactory = new AcScriptFactory(functionEvaluatorFactory)
  val acInterpretationFactory = new AcInterpretationFactory(functionEvaluatorFactory)
  val acUtil = ArtificialChemistryUtil.getInstance
  val plotter = Plotter.apply

  var initialStates: Iterable[Seq[Double]] = _
  var compartments: Iterable[AcCompartment] = _
  var fixedPointDetector: FixedPointDetector[jl.Double] = _
  var actionSeries: Iterable[AcInteractionSeries] = _
  var translationSeries: Iterable[AcTranslationSeries] = _
  var interpretations: Iterable[Stream[StateInterpretation[jl.Double, AcSpecies, AcTranslationVariable, _]]] = _

  @BeforeClass
  def initialize {
    compartments = for (_ <- 1 to repetitions) yield {
      val reactionSet = reactionSet1(twoToTwoChemSpec)
      new AcCompartment() {
        setReactionSet(reactionSet)
      }
    }

    initialStates = for (compartment <- compartments) yield for (i <- 1 to compartment.getSpecies().size()) yield Random.nextDouble

    actionSeries = for (compartment <- compartments) yield {
      val actionSeries = new AcInteractionSeries
      actionSeries.setSpeciesSet(compartment.getSpeciesSet())
      actionSeries.setPeriodicity(400)
      actionSeries.setRepeatFromElement(1)

      val species = new ju.ArrayList(compartment.getSpecies): Seq[AcSpecies]

      val variable1 = new AcInteractionVariable() {
        setLabel("XX")
      }
      val variable2 = new AcInteractionVariable() {
        setLabel("YY")
      }
      actionSeries.addVariable(variable1)
      actionSeries.addVariable(variable2)

      var time = 2
      for (_ <- 1 to 4) {
        val action = new AcInteraction
        actionSeries.addAction(action)
        action.setStartTime(time)
        action.setAlternationType(StateAlternationType.Replacement)
        action.setTimeLength(1)

        if (time == 2) {
          val assignment1 = new AcInteractionVariableAssignment
          assignment1.setVariable(variable1)
          action.addVariableAssignment(assignment1)
          acUtil.setSettingFunctionFromString(species(0).getLabel, assignment1)

          val assignment2 = new AcInteractionVariableAssignment
          assignment2.setVariable(variable2)
          action.addVariableAssignment(assignment2)
          acUtil.setSettingFunctionFromString("0.2 + " + species(1).getLabel, assignment2)
        }

        val effectedSpecies = RandomUtil.nextElementsWithRepetitions(compartment.getSpecies(), 2)
        effectedSpecies.foreach { s => {
          val speciesAction = new AcSpeciesInteraction
          action.addToSpeciesActions(speciesAction)
          speciesAction.setSpecies(s)
          acUtil.setSettingFunctionFromString("0.1 + " + compartment.getSpecies.head.getLabel + " + " + variable1.getLabel, speciesAction)
        }
        }
        time += 1 + RandomUtil.nextInt(50)
      }
      actionSeries
    }

    interpretations = for (compartment <- compartments) yield {
      var time = -100
      (for (_ <- 1 to 4) yield {
        time += 100
        val species = new ju.ArrayList(compartment.getSpecies): Seq[AcSpecies]
        val items = for (i <- 0 to 1) yield
          new RangeStateInterpretationItem[jl.Double, AcSpecies, AcTranslationVariable](
            new AcTranslationVariable() {
              setLabel(species(i).getLabel)
            },
            List(species(i)), { (speciesStateMap: Map[AcSpecies, Seq[jl.Double]], sadas: Map[AcTranslationVariable, jl.Double]) => {
              val states = speciesStateMap.get(species(i)).get
              (states.foldLeft(0d)(_ + _) / states.size): jl.Double
            }
            })
        new RangeStateInterpretation(time, 50, items)
      }).toStream
    }

    translationSeries = for (compartment <- compartments) yield {
      val translationSeries = new AcTranslationSeries
      translationSeries.setSpeciesSet(compartment.getSpeciesSet())
      translationSeries.setPeriodicity(1000)
      translationSeries.setRepeatFromElement(1)
      var time = 2
      for (_ <- 1 to 4) {
        val rangeTranslation = new AcTranslation
        translationSeries.addTranslation(rangeTranslation)
        rangeTranslation.setFromTime(time)
        rangeTranslation.setToTime(time + 100)

        compartment.getSpecies.foreach { s => {
          val translationItem = new AcTranslationItem
          rangeTranslation.addTranslationItem(translationItem)
          translationItem.setVariable(new AcTranslationVariable {
            setLabel(s.getLabel)
          })
          acUtil.setTranslationFunctionFromString("max(" + s.getLabel + ")", translationItem)
        }
        }
        time += 100 + RandomUtil.nextInt(100)
      }
      translationSeries
    }

    fixedPointDetector = new DistanceFixedPointDetector(0.0001: jl.Double, 50d)
  }
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ChemistryInteractiveRunnableTest extends ScalaChemistryTest {

  @Test
  def test1 {

  }

  @Test
  def test1ChemistryWithTranslationSeries = runChemistryForAllWithTranslationSeries()

  @Test
  def test2Chemistry = runChemistryForAll()

  @Test
  def test2NoActions {
    val compartment = compartments.head
    val as = actionSeries.head
    val initialState = initialStates.head: Seq[jl.Double]
    val asNoActions = new AcInteractionSeries {
      setSpeciesSet(compartment.getSpeciesSet())
    }

    val chemistryStates = getStatesNew(compartment, initialState, None)
    val chemistryScriptedPlainStates = getStatesNew(compartment, initialState, Some(asNoActions))
    assertNotEmpty(chemistryStates)
    assertNotEmpty(chemistryScriptedPlainStates)

    (chemistryStates.map(_._2).flatten, chemistryScriptedPlainStates.map(_._2).flatten).zipped.map(assertEquals(_, _, 0.00000000001))
  }

  @Test
  def test3NoActions {
    val compartment = compartments.head
    val initialState = initialStates.head: Seq[jl.Double]
    val asNoActions = new AcInteractionSeries {
      setSpeciesSet(compartment.getSpeciesSet)
    }

    val chemistryScriptedPlainStates = getStatesNew(compartment, initialState, Some(asNoActions))
    //	   	val chemistryOldNoActions = getStatesOld(compartment, ac.getSimulationConfig, initialState)

    assertNotEmpty(chemistryScriptedPlainStates)
    //        assertNotEmpty(chemistryOldNoActions)

    //	   	(chemistryScriptedPlainStates.map(_._2).flatten, chemistryOldNoActions.map(_._2).flatten).zipped.map(assertEquals(_,_,0.00000000001))
  }

  @Test
  def test5Correctness {
    val compartment = compartments.head
    val as = actionSeries.head
    val initialState = initialStates.head: Seq[jl.Double]

    val chemistryNewStates = getStatesNew(compartment, initialState, Some(as))
    //    	val chemistryOldStates = getStatesOld(compartment, ac.getSimulationConfig, initialState, as)

    assertNotEmpty(chemistryNewStates)
    //	   	assertNotEmpty(chemistryOldStates)

    if (displayPlot) {
      plotter.plotSeries(
        chemistryNewStates.map(_._2: Seq[jl.Double]),
        new SeriesPlotSetting()
          .setTitle("New")
          .setTransposed(true)
      )
      //	    	plotter.plotTimeSeriesTransposed(chemistryOldStates.map(_._2), "Old")
    }

    //	   	(chemistryNewStates.map(_._2).flatten, chemistryOldStates.map(_._2).flatten).zipped.map(assertEquals(_,_,0.000001))
  }

  @Test
  def testChemistryFixedPoint = runChemistryForAll(Some(fixedPointDetector))

  @Test
  def testChemistryListener = runChemistryForAll(None, true)

  @Test
  def testChemistryFixedPointListener = runChemistryForAll(Some(fixedPointDetector), true)

  def runChemistryForAll(
    fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None,
    collectStates: Boolean = false
  ) = (compartments, actionSeries, initialStates).zipped.foreach(runChemistry(_, _, _)(fixedPointDetector, collectStates))

  def runChemistryForAllWithInterpretations(
    fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None
  ) = ((compartments, actionSeries, interpretations).zipped, initialStates).zipped.foreach { (a, a4) =>
    runChemistryWithInterpretations(a._1, a._2, a._3, a4)(fixedPointDetector)
  }

  def runChemistryForAllWithTranslationSeries(
    fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None
  ) = ((compartments, actionSeries, translationSeries).zipped, initialStates).zipped.foreach { (a, a4) =>
    runChemistryWithTranslationSeries(a._1, a._2, a._3, a4)(fixedPointDetector)
  }

  def getStatesNew(
    compartment: AcCompartment,
    initialState: Seq[jl.Double],
    as: Option[AcInteractionSeries]
  ) = {
    val chemistryProducer = FlatChemistryProducer.applyArray(compartment, simConfig)

    val chemistrySystem = if (as.isDefined)
      TimeStateManager.stateUpdateableInteractiveInstance(chemistryProducer, acScriptFactory.apply(simConfig)(as.get))
    else
      TimeStateManager.stateUpdateableInstance(chemistryProducer)

    val timeStateCollector = new StateCollector[jl.Double, Array]
    chemistrySystem.subscribe(timeStateCollector)

    chemistrySystem.setStates(initialState)
    chemistrySystem.runFor(time)

    timeStateCollector.collected
  }

  def runChemistry(
    compartment: AcCompartment,
    actionSeries: AcInteractionSeries,
    initialState: Seq[jl.Double])(
    fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None,
    collectStates: Boolean = false
  ) = {
    val chemistryProducer = FlatChemistryProducer.applyArray(compartment, simConfig)
    val script = acScriptFactory.apply(simConfig)(actionSeries)
    val system = TimeStateManager.stateUpdateableInteractiveInstance(chemistryProducer, script, fixedPointDetector)

    var timeStateCollector = new StateCollector[jl.Double, Array]
    if (collectStates || displayPlot) {
      system.subscribe(timeStateCollector)
    }
    system.setStates(initialState)
    system.runFor(time)
    if (displayPlot) {
      val plotSetting = new SeriesPlotSetting()
        .setTitle("System")
        .setTransposed(true)
        .setXAxis(timeStateCollector.collected.map(_._1.doubleValue))

      plotter.plotSeries(timeStateCollector.collected.map(_._2.iterator.toIterable), plotSetting)
    }
  }

  def runChemistryWithTranslationSeries(
    compartment: AcCompartment,
    actionSeries: AcInteractionSeries,
    translationSeries: AcTranslationSeries,
    initialState: Seq[jl.Double])(
    fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None
  ) = runChemistryWithInterpretations(compartment, actionSeries, acInterpretationFactory.apply(translationSeries), initialState)(fixedPointDetector)

  def runChemistryWithInterpretations(
    compartment: AcCompartment,
    actionSeries: AcInteractionSeries,
    interpretations: Stream[StateInterpretation[jl.Double, AcSpecies, AcTranslationVariable, _]],
    initialState: Seq[jl.Double])(
    fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None
  ) = {
    val chemistryProducer = FlatChemistryProducer.applyArray(compartment, simConfig)
    val script = acScriptFactory.apply(simConfig)(actionSeries)
    val system = TimeStateManager.stateUpdateableInteractiveInstance(chemistryProducer, script, fixedPointDetector)

    val interpreter = StateInterpreter(system, interpretations, None)
    var timeStateCollector = new StateCollector[jl.Double, Array]
    system.subscribe(timeStateCollector)

    system.setStates(initialState)
    interpreter.runFor(time)
    if (displayPlot) {
      val plotSetting = new SeriesPlotSetting()
        .setTitle("System")
        .setTransposed(true)
        .setXAxis(timeStateCollector.collected.map(_._1.doubleValue))

      plotter.plotSeries(timeStateCollector.collected.map(_._2.iterator.toIterable), plotSetting)

      //        	val interpreterXAxis = interpreter.results.foldRight(List[Double]()){
      //        	    (result, xAxisBuffer) => {
      //        	    	val startTime = result.interpretation.startTime.doubleValue
      //        	    	xAxisBuffer ::: List(startTime, startTime + result.interpretation.timeLength.doubleValue)}}

      val interpretationCollector = new StateInterpretationResultCollector[jl.Double, AcSpecies, AcTranslationVariable]
      interpreter.subscribe(interpretationCollector)

      val interpreterXAxis = interpretationCollector.collected.map(_.interpretation.startTime.doubleValue)
      val interpreterValues = interpretationCollector.collected.foldRight(ListBuffer[Iterable[Double]]()) {
        (result, valuesBuffer) => valuesBuffer += result.items.map(_.interpretedValue)
      }

      println(interpreterXAxis)
      println(interpreterValues)

      val interpreterPlotSetting = new SeriesPlotSetting()
        .setTitle("Interpreter")
        .setTransposed(true)
        .setXAxis(interpreterXAxis)

      plotter.plotSeries(interpreterValues, interpreterPlotSetting)

    }
  }
}