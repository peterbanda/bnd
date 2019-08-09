package com.bnd.chemistry.business

import java.{lang => jl, util => ju}

import com.bnd.chemistry.business.ChemistryBenchmarkODETest._
import com.bnd.chemistry.business.ChemistryTestDataGenerator._
import com.bnd.chemistry.domain._
import com.bnd.core.DoubleConvertible
import com.bnd.plotter.Plotter
import com.bnd.core.runnable.SeqIndexAccessible.Implicits._
import com.bnd.core.runnable.{DistanceFixedPointDetector, FixedPointDetector, SeqIndexAccessible, StateAccessible, StateCollector, StateEvent, TimeStateManager, TimeStepUndefinedException}
import com.bnd.function.domain.ODESolverType
import com.bnd.math.domain.rand.RandomDistribution
import org.junit.{BeforeClass, FixMethodOrder, Test}
import org.junit.runners.MethodSorters
import com.bnd.core.CollectionElementsConversions._
import com.bnd.plotter.{Plotter, SeriesPlotSetting}
import com.bnd.core.runnable.{TimeRunnable, TimeStateManager, TimeStepUndefinedException}
import com.bnd.core.util.FileUtil

import scala.collection.JavaConversions._
import scala.collection.mutable.Publisher
import scala.util.Random

object ChemistryBenchmarkODETest {

    val time = 500
    val repetitions = 10000
    val displayPlot = false

    val acSimConfig = new AcSimulationConfig {
        setTimeStep(0.5)
        //        setOdeSolverType(ODESolverType.RungeKuttaCashKarp)
        setTolerance(0.001)
        setFixedPointDetectionPrecision(0.00001)
        setFixedPointDetectionPeriodicity(50d)
        //      setLowerThreshold(0.00)
    }

    val twoToTwoChemSpec = new AcSymmetricSpec() {
        setSpeciesNum(10)
        setReactionNum(10)
        setReactantsPerReactionNumber(2)
        setProductsPerReactionNumber(2)
        setCatalystsPerReactionNumber(0)
        setInhibitorsPerReactionNumber(0)
        setSpeciesForbiddenRedundancy(AcReactionSpeciesForbiddenRedundancy.None)
        setRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.1, 0.02))
        //        setConstantSpeciesNum(4)
        setInfluxRatio(0.2)
        setInfluxRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.001, 0d))
        setOutfluxRatio(0.1)
        setOutfluxRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.002, 0d))
    }

    val plotter = Plotter("svg")
    val acUtil = ArtificialChemistryUtil.getInstance

    var initialStates: Iterable[Seq[Double]] = _
    var compartments: Iterable[AcCompartment] = _
    var fixedPointDetector: FixedPointDetector[jl.Double] = _

    @BeforeClass
    def initialize {
        compartments = for (_ <- 0 to repetitions) yield {
            val reactionSet = reactionSet1(twoToTwoChemSpec)
            new AcCompartment() { setReactionSet(reactionSet) }
        }

        initialStates = for (compartment <- compartments) yield for (i <- 1 to compartment.getSpecies.size()) yield Random.nextDouble

        fixedPointDetector = new DistanceFixedPointDetector(acSimConfig.getFixedPointDetectionPrecision, acSimConfig.getFixedPointDetectionPeriodicity)
    }
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ChemistryBenchmarkODETest extends ScalaChemistryTest {

    @Test
    def test1 {

    }

    @Test
    def testChemistry1RK4 = runChemistryForAll(ODESolverType.RungeKutta4, None, false)

    @Test
    def testChemistry1RKF = runChemistryForAll(ODESolverType.RungeKuttaFehlberg, None, false)

    @Test
    def testChemistry1RKFDP = runChemistryForAll(ODESolverType.RungeKuttaDormandPrince, None, false)

    @Test
    def testChemistry1RKCK4 = runChemistryForAll(ODESolverType.RungeKuttaCashKarp4, None, false)

    @Test
    def testChemistry3RKCK = runChemistryForAll(ODESolverType.RungeKuttaCashKarp, None, false)

    @Test
    def testChemistry2FixedPointRK4 = runChemistryForAll(ODESolverType.RungeKutta4, Some(fixedPointDetector), false)

    @Test
    def testChemistry4FixedPointRKCK = runChemistryForAll(ODESolverType.RungeKuttaCashKarp, Some(fixedPointDetector), false)

    @Test
    def testChemistry2FixedPointRKF = runChemistryForAll(ODESolverType.RungeKuttaFehlberg, Some(fixedPointDetector), false)

    @Test
    def testChemistry2FixedPointRKFDP = runChemistryForAll(ODESolverType.RungeKuttaDormandPrince, Some(fixedPointDetector), false)

    @Test
    def testChemistry3ListenerRK4 = runChemistryForAll(ODESolverType.RungeKutta4, None, true)

    @Test
    def testChemistry3ListenerRKCK = runChemistryForAll(ODESolverType.RungeKuttaCashKarp, None, true)

    @Test
    def testChemistry4FixedPointListenerRK4 = runChemistryForAll(ODESolverType.RungeKutta4, Some(fixedPointDetector), true)

    @Test
    def testChemistry4FixedPointListenerRKCK = runChemistryForAll(ODESolverType.RungeKuttaCashKarp, Some(fixedPointDetector), true)


    def runChemistryForAll(
        odeType: ODESolverType,
        fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None,
        collectStates: Boolean = false
    ) = {
        acSimConfig.setOdeSolverType(odeType)
        (compartments, initialStates).zipped.foreach(runChemistry(acSimConfig,_, fixedPointDetector)(_, collectStates))

    }

    def runChemistry(
        simConfig: AcSimulationConfig,
        compartment: AcCompartment,
        fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None
    ) = {
        val chemistryProducer = FlatChemistryProducer(compartment, acSimConfig)
        runSystem(TimeStateManager(chemistryProducer, fixedPointDetector), _: Seq[jl.Double], _: Boolean, simConfig.getOdeSolverType)
    }

    def runSystem[T : DoubleConvertible, S[X] : SeqIndexAccessible](
        system: TimeRunnable with StateAccessible[T] with Publisher[StateEvent[T, S]],
        initialState: Seq[T],
        collectStates: Boolean = false,
        typ: ODESolverType)(
        implicit m : Manifest[T]) {
        var timeStateCollector = new StateCollector[T, S]
        if (collectStates || displayPlot) {
            system.subscribe(timeStateCollector)
        }
        system.setStates(initialState)
        //val startTime = System.nanoTime()
        try {
            system.runFor(time)
        } catch {
            case e: NumberFormatException => println("Number format exception! Run terminated.")
            case e: TimeStepUndefinedException => println("Time step undefined: " + e.getMessage + "! Run terminated.")
        }
        // val runtime = (System.nanoTime() - startTime)*math.pow(10, -9)
        if (displayPlot) {
            //if (runtime > 1) {
            val plotSetting = new SeriesPlotSetting()
              .setTitle("System")
              .setTransposed(true)
              .setXAxis(timeStateCollector.collected.map(_._1.doubleValue))
              .setShowLegend(false)

            val output = plotter.plotSeries(timeStateCollector.collected.map(_._2.iterator.toIterable), plotSetting)
            FileUtil.getInstance().overwriteStringToFileSafe(output, typ+"chemicals.svg");

        }
    }
}