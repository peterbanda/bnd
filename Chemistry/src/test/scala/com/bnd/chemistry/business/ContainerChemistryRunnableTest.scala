package com.bnd.chemistry.business

import java.{lang => jl, util => ju}

import com.bnd.chemistry.business.ChemistryTestDataGenerator._
import com.bnd.chemistry.business.ContainerChemistryRunnableTest._
import com.bnd.chemistry.domain._
import com.bnd.core.DoubleConvertible.JavaDoubleAsDoubleConvertible
import com.bnd.plotter.Plotter
import com.bnd.core.runnable.SeqIndexAccessible._
import com.bnd.core.runnable.{DistanceFixedPointDetector, FixedPointDetector, StateCollector}
import com.bnd.core.util.{FileUtil, RandomUtil}
import com.bnd.function.domain.ODESolverType
import com.bnd.plotter.{Plotter, SeriesPlotSetting}
import com.bnd.core.dynamics.StateAlternationType
import com.bnd.core.util.{FileUtil, RandomUtil}
import org.junit.Assert._
import org.junit.runners.MethodSorters
import org.junit.{BeforeClass, FixMethodOrder, Test}

import scala.collection.JavaConversions._
import scala.util.Random

object ContainerChemistryRunnableTest {

    val time = 5000
    val displayPlot = true
    val mergePlots = false
    val runTrace = false

    val parentSpeciesNum = 3
    val childSpeciesNum = 2

    val acSimConfig = new AcSimulationConfig {
        setTimeStep(0.5)
        setOdeSolverType(ODESolverType.RungeKuttaFehlberg)
        setTolerance(0.0001)
        setFixedPointDetectionPrecision(0.00001)
        setFixedPointDetectionPeriodicity(50d)
    }

    val chemistryRunnableFactory = new ChemistryRunnableFactory(functionEvaluatorFactory)
    val plotter = Plotter("svg")
    val acUtil = ArtificialChemistryUtil.getInstance

    var compartment: AcCompartment = _
    var fixedPointDetector: FixedPointDetector[jl.Double] = _
    var actionSeries: AcInteractionSeries = _
    var emptyActionSeries: AcInteractionSeries = _

    def sortSpecies(compartment: AcCompartment) =
        (compartment.getSpeciesSet().getVariables(): Iterable[AcSpecies]).toList.sortBy(_.getVariableIndex())

    @BeforeClass
    def initialize {
        initCompartment

        initActionSeries

        emptyActionSeries = new AcInteractionSeries
        emptyActionSeries.setSpeciesSet(compartment.getSpeciesSet)

        fixedPointDetector = new DistanceFixedPointDetector(0.0001: jl.Double, 50d)
    }

    def initActionSeries = {
        val subCompartments = (compartment.getSubCompartments: Iterable[AcCompartment]).toSeq
        actionSeries = createActionSeries(compartment)
        val subActionSeries1 = createActionSeries(subCompartments(0))
        val subActionSeries2 = createActionSeries(subCompartments(1))
        actionSeries.addSubActionSeries(subActionSeries1)
        actionSeries.addSubActionSeries(subActionSeries2)
    }

    def createActionSeries(compartment: AcCompartment) = {
        actionSeries.setSpeciesSet(compartment.getSpeciesSet())
        actionSeries.setPeriodicity(800)
        actionSeries.setRepeatFromElement(1)
        var time = 2
        for (_ <- 1 to 4) {
            val action = new AcInteraction
            actionSeries.addAction(action)
            action.setStartTime(time)
            action.setAlternationType(StateAlternationType.Replacement)
            action.setTimeLength(0d)
            println(time)

            val effectedSpecies = RandomUtil.nextElementsWithRepetitions(compartment.getSpecies(), 2)
            effectedSpecies.foreach { s =>
                {
                    val speciesAction = new AcSpeciesInteraction
                    action.addToSpeciesActions(speciesAction)
                    speciesAction.setSpecies(s)
                    acUtil.setSettingFunctionFromString("0.1 + " + compartment.getSpecies().head.getLabel(), speciesAction)
                }
            }
            time += RandomUtil.nextInt(50)
        }
        actionSeries
    }

    def createCompartment(reactionSet: AcReactionSet, label: String) = new AcCompartment() {
        setReactionSet(reactionSet)
        setLabel(label)
    }

    def initCompartment = {
        compartment = createCompartment(parentReactionSet, "Skin")
        val child1Compartment = createCompartment(childReactionSet("C1"), "Sub 1")
        val child2Compartment = createCompartment(childReactionSet("C2"), "Sub 2")
        compartment.addSubCompartment(child1Compartment)
        compartment.addSubCompartment(child2Compartment)

        val skinSpecies = sortSpecies(compartment)
        val child1Species = sortSpecies(child1Compartment)
        val child2Species = sortSpecies(child2Compartment)

        val species = skinSpecies ++ child1Species ++ child2Species

        // channels
        child1Compartment.addChannel(new AcCompartmentChannel() {
            setSourceSpecies(skinSpecies.head)
            setTargetSpecies(child1Species.head)
            setDirection(AcChannelDirection.In)
            setPermeability(0.01)
        })

        child2Compartment.addChannel(new AcCompartmentChannel() {
            setSourceSpecies(skinSpecies.tail.head)
            setTargetSpecies(child2Species.head)
            setDirection(AcChannelDirection.In)
            setPermeability(0.01)
        })
    }

    val parentReactionSet = {
        val speciesSet = createSpeciesSetWithPrefix(parentSpeciesNum, "P")
        val selectedSpecies: Seq[AcSpecies] = new ju.ArrayList[AcSpecies](RandomUtil.nextElementsWithRepetitions(speciesSet.getVariables(), 4))

        val reactionSet = new AcReactionSet
        reactionSet setSpeciesSet (speciesSet)
        reactionSet addReaction (createReaction(selectedSpecies(0), selectedSpecies(1), RandomUtil.nextDouble(0.05)))
        reactionSet addReaction (createReaction(selectedSpecies(2), selectedSpecies(3), RandomUtil.nextDouble(0.05)))
        reactionSet
    }

    def childReactionSet(prefix: String) = {
        val speciesSet = createSpeciesSetWithPrefix(childSpeciesNum, prefix)
        val selectedSpecies = new ju.ArrayList[AcSpecies](speciesSet.getVariables): Seq[AcSpecies]

        val reactionSet = new AcReactionSet
        reactionSet setSpeciesSet (speciesSet)
        reactionSet addReaction (createReaction(selectedSpecies(0), selectedSpecies(1), RandomUtil.nextDouble(0.05)))
        reactionSet
    }

    private def createReaction(
        reactant: AcSpecies,
        product: AcSpecies,
        forwardRateConstant: jl.Double): AcReaction = {
        val reaction = new AcReaction
        reaction addAssociationForSpecies (reactant, AcSpeciesAssociationType.Reactant)
        reaction addAssociationForSpecies (product, AcSpeciesAssociationType.Product)
        reaction setForwardRateConstant (forwardRateConstant)
        reaction
    }
}

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ContainerChemistryRunnableTest extends ScalaChemistryTest {

    @Test
    def test1 {

    }

    // For naming the plots
    val merged = if (mergePlots) "Merged " else ""

//    @Test
//    def test2Chemistry = runChemistry(compartment, merged + "Vanilla")
//
//    @Test
//    def test3ChemistryWithFPDetector = runChemistry(compartment, merged + "Fixed Point", Some(fixedPointDetector))
//
//    @Test
//    def test4ChemistryWithEmptyActionSeries = runChemistry(compartment, merged + "Empty AS", None, Some(emptyActionSeries))
//
    @Test
    def test5ChemistryWithActionSeries = runChemistry(compartment, merged + "Interaction Series", Some(actionSeries))
//
//    @Test
//    def test5ChemistryWithActionSeriesAndFPDetector = runChemistry(compartment, merged + "Interaction Series and FPD", Some(fixedPointDetector), Some(actionSeries))

    @Test
    def test6CompareHierFlat = compareHierFlat(None)
    
    @Test
    def test6CompareHierFlatAS = compareHierFlat(Some(actionSeries))
    
//    @Test
//    def test7CompartmentSynch = checkSynch(2)
//    
//	  
//    
//    def checkSych (subcompartments : Int) = {
//        val skinCompartment = createCompartment(parentReactionSet, "Skin")
//
//        val initialStates = for (i <- 1 to parentSpeciesNum) yield Random.nextDouble: jl.Double
//
//        for (i <- 1 to subcompartments){
//            skinCompartment.addSubCompartment(
//                createCompartment(parentReactionSet, "sub" + i))
//        }
//        
//        val chem = new ArtificialChemistry {
//            setSkinCompartment(skinCompartment)
//            setSimulationConfig(acSimConfig)
//        }
//                
//        val containerWithModules = chemistryRunnableFactory.createHierarchical(chem.getSkinCompartment(), chemistryRunSetting)
//
//        val containerChemistry = containerWithModules._1
//        val compartmentChemistries = containerWithModules._2
//        
//        val compartmentStateCollectors =
//            compartmentChemistries.map {
//                case (compartment, chemistry) => {
//                    val stateCollector = new StateCollector[jl.Double]
//                    chemistry.subscribe(stateCollector)
//                    (compartment, stateCollector)
//                }
//            }
//
//        containerChemistry.setStates(initialStates)
//        compartmentChemistries.foreach(_._2.setStates(initialStates))
//        
//        containerChemistry.runFor(time)
//
//        compartmentStateCollectors
//        
//        for (i <- 1 to subcompartments){
//            (collector.head.collected, collector.tail.head.collected).zipped.foreach {
//                case ((timeStep1, states1), (timeStep2, states2)) => {
//                assertEquals(timeStep1, timeStep2)
//                (states1,states2).zipped.foreach(assertEquals)
//                }
//            }
//        }
//    }
        
        
//        val container = chemistryRunnableFactory.createHierarchical(chem.getSkinCompartment(), chemistryRunSetting)
//
//        val containerChem = container._1
//        val compartmentChems = container._2
//        
//        val compartmentStateCollectors = compartmentChems.map {
//            case (compartment, chemistry) => {
//                val stateCollector = new StateCollector[jl.Double]
//                chemistry.subscribe(stateCollector)
//                (compartment, stateCollector)
//            }
//        }
//        
//        containerChem.setStates(initialCopies)
//        containerChem.runFor(time)

    
    def compareHierFlat(
        actionSeries: Option[AcInteractionSeries] = None
    ) = {
        val skinCompartment = createCompartment(parentReactionSet, "Skin")
        val initialStates = for (i <- 1 to parentSpeciesNum) yield Random.nextDouble: jl.Double

        val hierarchicalCollectors = collectHierarchicalStates(compartment, initialStates, actionSeries).map(_._2)
        val flatCollector = collectFlatStates(compartment, initialStates, actionSeries)

        assertEquals(hierarchicalCollectors.size, 1)
        (hierarchicalCollectors.head.collected, flatCollector.collected).zipped.foreach {
            case ((timeStep1, states1), (timeStep2, states2)) => {
                assertEquals(timeStep1, timeStep2)
                (states1,states2).zipped.foreach(assertEquals)
            }
        }
    }

    def runChemistry(
        compartment: AcCompartment,
        plotName: String,
        actionSeries: Option[AcInteractionSeries] = None
    ) = {
        val containerWithModules = if (actionSeries.isDefined)
            chemistryRunnableFactory.createInteractiveWithPublishers(compartment, acSimConfig, actionSeries.get, None)
        else
            chemistryRunnableFactory.createNonInteractiveWithPublishers(compartment, acSimConfig, None)

        val containerChemistry = containerWithModules._1
        val compartmentChemistries = containerWithModules._2

        val compartmentStateCollectors = if (displayPlot) {
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

        // displaying
        if (displayPlot)
            if (mergePlots) displayMergedPlot(compartmentStateCollectors, plotName)
            else displayCompartmentPlots(compartmentStateCollectors, plotName)
    }
    
        
    
    //    def compareCompartments(
    //        fixedPointDetector: Option[FixedPointDetector[jl.Double]] = None,
    //        actionSeries: Option[AcInteractionSeries] = None) = {
    //
    //        def sortSpecies(compartment: AcCompartment) =
    //            (compartment.getSpeciesSet().getVariables(): Iterable[AcSpecies]).toList.sortBy(_.getVariableIndex())
    //
    //        val skinCompartment = createCompartment(parentReactionSet, "Skin")
    //        val childCompartment = createCompartment(childReactionSet("C1"), "Sub 1")
    //        skinCompartment.addSubCompartment(childCompartment)
    //
    //        val skinSpecies = sortSpecies(skinCompartment)
    //        val childSpecies = sortSpecies(childCompartment)
    //        val species = skinSpecies ++ childSpecies
    //
    //        val noCompart = new ArtificialChemistry() {
    //            setSkinCompartment(skinCompartment)
    //            setSimulationConfig(acSimConfig)
    //        }
    //        val compart = new ArtificialChemistry {
    //            setSkinCompartment(childCompartment)
    //        }
    //    }

    def collectHierarchicalStates(
        compartment: AcCompartment,
        initialState: Seq[jl.Double],
        actionSeries: Option[AcInteractionSeries] = None
    ) = {
        val containerWithModules = if (actionSeries.isDefined)
            chemistryRunnableFactory.createInteractiveWithPublishers(compartment, acSimConfig, actionSeries.get, None)
        else
            chemistryRunnableFactory.createNonInteractiveWithPublishers(compartment, acSimConfig, None)

        val containerChemistry = containerWithModules._1
        val compartmentChemistries = containerWithModules._2

        val compartmentStateCollectors =
            compartmentChemistries.map {
                case (compartment, chemistry) => {
                    val stateCollector = new StateCollector[jl.Double, Array]
                    chemistry.subscribe(stateCollector)
                    (compartment, stateCollector)
                }
            }

        containerChemistry.setStates(initialState)
        containerChemistry.runFor(time)

        compartmentStateCollectors
    }

    def collectFlatStates(
        compartment: AcCompartment,
        initialState: Seq[jl.Double],
        actionSeries: Option[AcInteractionSeries] = None
    ) = {
        val chemistryWithPublishers = if (actionSeries.isDefined)
        	if (runTrace)
        	    chemistryRunnableFactory.createInteractiveWithTraceAndPublishers(compartment, acSimConfig, actionSeries.get, None)
        	else
        	    chemistryRunnableFactory.createInteractiveWithPublishers(compartment, acSimConfig, actionSeries.get, None)
        else {
        	val chem = chemistryRunnableFactory.createNonInteractiveWithPublishers(compartment, acSimConfig, None)
        	chem._1.setStates(initialState)
        	chem
        }

        val chemistry = chemistryWithPublishers._1
        val firstPublisher = chemistryWithPublishers._2.head._2
            
        val stateCollector = new StateCollector[jl.Double, Array]
        firstPublisher.subscribe(stateCollector)

        chemistry.runFor(time)

        stateCollector
    }

    private def displayMergedPlot(
        compartmentStateCollectors: Iterable[(AcCompartment, StateCollector[jl.Double, Array])],
        plotName: String
    ) = {
        val xAxisLines = compartmentStateCollectors.map {
            case (compartment, stateCollector) => {
                val timeSteps = stateCollector.collected.map(_._1.toDouble)
                val data = stateCollector.collected.map(_._2).transpose
                println(timeSteps)
                (timeSteps, data)
            }
        }
        val data = xAxisLines.map(_._2).flatten
        val plotSetting = new SeriesPlotSetting()
              .setTitle(plotName)
              .setTransposed(false)
              .setXAxes(xAxisLines.flatMap { case (xAxis, lines) => List.fill(lines.size)(xAxis) })

        val output = plotter.plotSeries(data, plotSetting)
        FileUtil.getInstance().overwriteStringToFileSafe(output, plotName + ".svg")
    }

    private def displayCompartmentPlots(
        compartmentStateCollectors: Iterable[(AcCompartment, StateCollector[jl.Double, Array])],
        plotName: String
    ) =
        compartmentStateCollectors.foreach {
            case (compartment, stateCollector) => {
                val data = stateCollector.collected.map(_._2 : Seq[jl.Double])
                val times = stateCollector.collected.map(_._1.toDouble)
                println(times)

                val plotSetting = new SeriesPlotSetting()
                      .setTitle(plotName + " " + compartment.getLabel)
                      .setTransposed(true)
                      .setXAxis(times)

                val output = plotter.plotSeries(data, plotSetting)
                FileUtil.getInstance.overwriteStringToFileSafe(output, plotName + " " + compartment.getLabel + ".svg")
            }
        }
}