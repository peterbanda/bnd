package com.bnd.chemistry.business

import java.io.Serializable
import java.{lang => jl, util => ju}

import com.bnd.chemistry.domain.{AcSimulationConfig, _}
import com.bnd.core.domain.ComponentHistory
import com.bnd.core.dynamics._
import com.bnd.core.runnable.SeqIndexAccessible.Implicits._
import com.bnd.core.runnable.{AlteredStateCollector, ComponentStateCollector, ContinuousCrossModuleTransport, DistanceFixedPointDetector, DummyTraceTimeRunnable, FullStateAccessible, RunTraceHolder, SeqIndexAccessible, StateAccessible, StateEvent, TimeStateManager}
import com.bnd.function.business.ScalaFunctionEvaluatorConversions._
import com.bnd.function.business.ode.ODESolverFactory
import com.bnd.function.domain.ODESolverType
import com.bnd.function.evaluator.FunctionEvaluatorFactory
import com.bnd.core.domain.{ComponentHistory, ComponentRunTrace}
import com.bnd.core.dynamics.ODESolver
import com.bnd.core.runnable.{ContinuousCrossModuleTransport, DummyTraceTimeRunnable, TimeRunnable, TimeStateManager}

import scala.collection.JavaConversions._
import scala.collection.mutable.Publisher

class ChemistryRunnableFactory(private val functionEvaluatorFactory : FunctionEvaluatorFactory) extends Serializable {

    // handy type expansions

    type Chemistry[C] = TimeRunnable with FullStateAccessible[jl.Double, C]
    type FlatChemistry = Chemistry[AcVariable[_]]
    type HierarchicalChemistry = Chemistry[(AcCompartment, AcVariable[_])]
    type StatePublisher = Publisher[StateEvent[jl.Double, Array]]

    private val acScriptFactory = new AcScriptFactory(functionEvaluatorFactory)
    private val beforeAlternationTimeDiff = Some(BigDecimal.apply(0.01))

    // flat 

    private def createFlat(
		compartment : AcCompartment, 
		simConfig : AcSimulationConfig,
		setting : Option[ChemistryRunSetting]
	) : FlatChemistry with StatePublisher = {
        val fpDetector = fixedPointDetector(simConfig)
        val refSpecies = getReferencedSpecies(compartment)
	    val producer = chemistryProducer(compartment, simConfig, setting, Some(refSpecies))
        TimeStateManager.stateUpdateableInstance(producer, fpDetector, Some(0d : jl.Double))
	}

    private def createFlatInteractive(
        compartment : AcCompartment,
		simConfig : AcSimulationConfig,
		actionSeries : AcInteractionSeries,
		setting : Option[ChemistryRunSetting]
	) : FlatChemistry with StatePublisher = {
        val fpDetector = fixedPointDetector(simConfig)
        val refSpecies = getReferencedSpecies(compartment) ++ getReferencedSpecies(actionSeries)
	    val producer = chemistryProducer(compartment, simConfig, setting, Some(refSpecies), Some(actionSeries.getImmutableSpecies))
	    val script = acScriptFactory.apply(simConfig)(actionSeries)
	    TimeStateManager.stateUpdateableInteractiveInstance(producer, script, fpDetector, Some(0 : jl.Double), beforeAlternationTimeDiff)
    }

    private def createFlatInteractive(
        compartment : AcCompartment,
		simConfig : AcSimulationConfig,
		evaluatedActionSeries : AcEvaluatedActionSeries,
		setting : Option[ChemistryRunSetting]
	) : FlatChemistry with StatePublisher = {
        val fpDetector = fixedPointDetector(simConfig)
        val actionSeries = evaluatedActionSeries.getActionSeries
        val refSpecies = getReferencedSpecies(compartment) ++ getReferencedSpecies(actionSeries)
	    val producer = chemistryProducer(compartment, simConfig, setting, Some(refSpecies), Some(actionSeries.getImmutableSpecies))
	    val script = acScriptFactory.apply(evaluatedActionSeries)
	    TimeStateManager.stateUpdateableInteractiveInstance(producer, script, fpDetector, Some(0 : jl.Double), beforeAlternationTimeDiff)
    }

    private def chemistryProducer(
        compartment : AcCompartment,
		simConfig : AcSimulationConfig,
		setting : Option[ChemistryRunSetting] = None,
		explicitSpecies : Option[Iterable[AcSpecies]] = None,
		immutableSpecies : Option[Iterable[AcSpecies]] = None
    ) = if (setting.isDefined)
        	FlatChemistryProducer.applyArray(compartment, simConfig, setting.get, explicitSpecies, immutableSpecies)
        else 
            FlatChemistryProducer.applyArray(compartment, simConfig, explicitSpecies, immutableSpecies)

	private def getReferencedSpecies(interactionSeries : AcInteractionSeries) : Set[AcSpecies] =
        interactionSeries.getActions.map(_.getSpeciesActions.map(_.getSpecies)).flatten.toSet

	private def getReferencedSpecies(compartment : AcCompartment) : Set[AcSpecies] = {
		val channelSpecies = compartment.getChannels.map{ channel => if (channel.getDirection() == AcChannelDirection.In) channel.getTargetSpecies() else channel.getSourceSpecies() }
		val subChannelSpecies = compartment.getSubCompartments().map{ compartment => 
			compartment.getChannels().map{ channel =>
				if (channel.getDirection() == AcChannelDirection.Out) channel.getTargetSpecies() else channel.getSourceSpecies()
			}
		}.flatten
		val reactionSpecies = compartment.getReactionSet().getReactions().map(_.getSpeciesAssociations().map(_.getSpecies())).flatten
		(channelSpecies ++ subChannelSpecies ++ reactionSpecies).toSet 
	}

    // Hierarchical

    private def createHierarchical(
	    skin : AcCompartment,
		simConfig : AcSimulationConfig,
		setting : Option[ChemistryRunSetting]
	) : HierarchicalChemistry = createHierarchicalWithModules(skin, simConfig, setting)._1

    private def createHierarchicalWithModules(
	    skin : AcCompartment,
		simConfig : AcSimulationConfig,
		setting : Option[ChemistryRunSetting]
	) : (HierarchicalChemistry, Iterable[(AcCompartment, FlatChemistry with StatePublisher)]) = {
	    def createChemistryRecursively(compartment : AcCompartment) : List[(AcCompartment, FlatChemistry with StatePublisher)] = {
	    	val chemistry = createFlat(compartment, simConfig, setting)
	    	(compartment, chemistry) :: compartment.getSubCompartments.map(createChemistryRecursively).flatten.toList
	    }

	    val compartmentChemistries = createChemistryRecursively(skin)
	    createContainerWithModules(compartmentChemistries, simConfig)
    }

    private def createHierarchicalInteractive = createHierarchicalInteractiveWithModules(
    	_ : AcCompartment,
        _ : AcSimulationConfig,
        _ : AcInteractionSeries,
        _ : Option[ChemistryRunSetting])._1

    private def createHierarchicalInteractiveWithModules( 
	    skin : AcCompartment,
		simConfig : AcSimulationConfig,
	    skinActionSeries : AcInteractionSeries,
	    setting : Option[ChemistryRunSetting]
	) : (HierarchicalChemistry, Iterable[(AcCompartment, FlatChemistry with StatePublisher)]) = {
        // aux function to collect sub interaction series of given size, if the given number of interaction series is not available, Nones will be returned
        def fillSubActionSeries(actionSeries : Option[AcInteractionSeries], size : Int) = {
            val subActionSeries : Iterable[AcInteractionSeries] = if (actionSeries.isDefined)
                actionSeries.get.getSubActionSeries take size
            else
                List.empty[AcInteractionSeries]
            val subSize = subActionSeries.size
            subActionSeries.map(Some(_)) ++ List.fill(size - subSize)(None)
        }

	    def createChemistryRecursively(
	        compartment : AcCompartment,
	        actionSeries : Option[AcInteractionSeries]
	    ) : List[(AcCompartment, FlatChemistry with StatePublisher)] = {
	    	val chemistry = if (actionSeries.isDefined)
	    	    createFlatInteractive(compartment, simConfig, actionSeries.get, setting)
	    	else
	    	    createFlat(compartment, simConfig, setting)
	    	val subCompartments = compartment.getSubCompartments
	    	val subActionSeries = fillSubActionSeries(actionSeries, subCompartments.size)
	    	(compartment, chemistry) :: (subCompartments, subActionSeries).zipped.map(createChemistryRecursively).flatten.toList
	    }

	    val compartmentChemistries = createChemistryRecursively(skin, Some(skinActionSeries))
	    createContainerWithModules(compartmentChemistries, simConfig)
	}

    // general

    def createNonInteractive(
		skinCompartment : AcCompartment,
		simConfig : AcSimulationConfig,
		setting : Option[ChemistryRunSetting]
	) : TimeRunnable with StateAccessible[jl.Double] =
        if (skinCompartment.getSubCompartments.size == 0)
            createFlat(skinCompartment, simConfig, setting)
        else 
            createHierarchical(skinCompartment, simConfig, setting)

    def createNonInteractiveWithPublishers(
		skinCompartment : AcCompartment,
		simConfig : AcSimulationConfig,
		setting : Option[ChemistryRunSetting]
	) : (TimeRunnable with StateAccessible[jl.Double], Iterable[(AcCompartment, StatePublisher)]) =
        if (skinCompartment.getSubCompartments.isEmpty) {
            val chemistryRunnable = createFlat(skinCompartment, simConfig, setting)
            (chemistryRunnable, List((skinCompartment, chemistryRunnable)))
        } else 
            createHierarchicalWithModules(skinCompartment, simConfig, setting)

    def createInteractive(
		skinCompartment : AcCompartment, 
		simConfig : AcSimulationConfig,
		actionSeries : AcInteractionSeries,
		setting : Option[ChemistryRunSetting]
	) : TimeRunnable with StateAccessible[jl.Double] =
        if (skinCompartment.getSubCompartments.isEmpty)
            createFlatInteractive(skinCompartment, simConfig, actionSeries, setting)
        else
        	createHierarchicalInteractive(skinCompartment, simConfig, actionSeries, setting)   

    def createInteractiveWithPublishers(
		skinCompartment : AcCompartment, 
		simConfig : AcSimulationConfig,
		actionSeries : AcInteractionSeries,
		setting : Option[ChemistryRunSetting]
	) : (TimeRunnable with StateAccessible[jl.Double], Iterable[(AcCompartment, StatePublisher)]) =
        if (skinCompartment.getSubCompartments.isEmpty) {
            val chemistryRunnable = createFlatInteractive(skinCompartment, simConfig, actionSeries, setting)
            (chemistryRunnable, List((skinCompartment, chemistryRunnable)))
        } else
        	createHierarchicalInteractiveWithModules(skinCompartment, simConfig, actionSeries, setting)            

    // run holder

    @Deprecated
    def createInteractiveWithTraceAndPublishers(
		skinCompartment : AcCompartment, 
		simConfig : AcSimulationConfig,
		actionSeries : AcInteractionSeries,
		setting : Option[ChemistryRunSetting]
	) : (TimeRunnable with RunTraceHolder[jl.Double, (AcCompartment, AcSpecies)], Iterable[(AcCompartment, StatePublisher)]) =
        if (skinCompartment.getSubCompartments.size == 0) {
            val chemistry = createFlatInteractive(skinCompartment, simConfig, actionSeries, setting)
            val chemistryBO : TimeRunnable with RunTraceHolder[jl.Double, (AcCompartment, AcSpecies)] = new FlatChemistryTraceTimeRunnable(chemistry, skinCompartment)
            (chemistryBO, List((skinCompartment, chemistry)))
        } else {
        	val chemistryWithModules = createHierarchicalInteractiveWithModules(skinCompartment, simConfig, actionSeries, setting)
        	val acRunHolderChemistry : TimeRunnable with RunTraceHolder[jl.Double, (AcCompartment, AcSpecies)] =  new HierarchicalChemistryTraceTimeRunnable(chemistryWithModules._1, chemistryWithModules._2)
        	(acRunHolderChemistry, chemistryWithModules._2)            
        }

    def createInteractiveWithTrace(
		skinCompartment : AcCompartment, 
		simConfig : AcSimulationConfig,
		actionSeries : AcInteractionSeries,
		setting : Option[ChemistryRunSetting]
	) : TimeRunnable with RunTraceHolder[jl.Double, (AcCompartment, AcSpecies)] =
        if (skinCompartment.getSubCompartments.size == 0) {
            val chemistry = createFlatInteractive(skinCompartment, simConfig, actionSeries, setting)
            new FlatChemistryTraceTimeRunnable(chemistry, skinCompartment)
        } else {
        	val chemistryWithModules = createHierarchicalInteractiveWithModules(skinCompartment, simConfig, actionSeries, setting)
        	new HierarchicalChemistryTraceTimeRunnable(chemistryWithModules._1, chemistryWithModules._2)            
        }

    // helper functions

    private def createContainerWithModules(
        compartmentChemistries : Iterable[(AcCompartment, FlatChemistry with StatePublisher)],
        simConfig : AcSimulationConfig
    ) = {
	    val alternations = createAlternations(compartmentChemistries.map(_._1), simConfig)
	    val hierarchicalChemistry = TimeStateManager.syncContainerInstance[jl.Double, AcVariable[_], AcCompartment](compartmentChemistries, alternations)
	    (hierarchicalChemistry, compartmentChemistries)
    }

    private def createAlternations(
        compartments : Iterable[AcCompartment],
        simConfig : AcSimulationConfig
    ) = {
        val lowerValue = 0 : jl.Double
        val lowerBound = if (simConfig.getLowerThreshold != null) Some(simConfig.getLowerThreshold) else Some(0 : jl.Double)
	    def odeSolver = createPermeabilityODESolver(simConfig.getOdeSolverType, simConfig.getTimeStep, simConfig.getTolerance)_
        val alternations = for (compartment <- compartments) yield
        	for (subCompartment <- compartment.getSubCompartments) yield
        		for (channel <- subCompartment.getChannels()) yield {
        			val alternation = ContinuousCrossModuleTransport.odeSolverInstance[jl.Double, AcVariable[_], AcCompartment](
        			        odeSolver(channel.getPermeability), lowerValue, lowerBound)_ 
        			channel.getDirection() match {
						case AcChannelDirection.In =>
							alternation((compartment, channel.getSourceSpecies),(subCompartment, channel.getTargetSpecies))
						case AcChannelDirection.Out =>
							alternation((subCompartment, channel.getSourceSpecies),(compartment, channel.getTargetSpecies))
        			}
        		}

	    alternations.flatten.flatten
    }

    private def fixedPointDetector(simConfig : AcSimulationConfig) =
        if (simConfig.getFixedPointDetectionPrecision != null && simConfig.getFixedPointDetectionPeriodicity != null)
            Some(new DistanceFixedPointDetector[jl.Double](simConfig.getFixedPointDetectionPrecision, simConfig.getFixedPointDetectionPeriodicity))
        else None

	private def createPermeabilityODESolver(
	    typ: ODESolverType,
        timeStep : jl.Double,
        tolerance : jl.Double)(
        permeability: jl.Double
    ) : ODESolver = {
	    def permeabilityFun(x: jl.Iterable[jl.Double]): Array[jl.Double] = Array(permeability * x.iterator().next())
	    ODESolverFactory.createInstance(scalaFunctionToFunctionEvaluator(permeabilityFun, 1), typ, timeStep, tolerance)
	}
}

// helper classes

private class FlatChemistryTraceTimeRunnable[S[X] : SeqIndexAccessible](
		chemistryRunnable : TimeRunnable with Publisher[StateEvent[jl.Double, S]],
		skinCompartment : AcCompartment
	) extends DummyTraceTimeRunnable[jl.Double, (AcCompartment, AcSpecies)](chemistryRunnable) {

    val updatedStateCollector = new ComponentStateCollector[jl.Double, AcSpecies, S]
    val alteredStateCollector = new AlteredStateCollector[jl.Double, AcSpecies, S]

    chemistryRunnable.subscribe(updatedStateCollector)
//    chemistry.subscribe(alteredStateCollector)

    override def getRunTrace = {
        // run for 0 time to get final state
    	runFor(0 : BigDecimal)
    	val runTrace = new ComponentRunTrace[jl.Double, (AcCompartment, AcSpecies)]
    	runTrace.runTime(currentTime.doubleValue)
    	if (!updatedStateCollector.collected.isEmpty) {
    		runTrace.timeSteps(updatedStateCollector.collected.view.map(_._1.doubleValue : jl.Double))
    		val components: Iterable[_] = updatedStateCollector.components
    		val histories = List.fill(components.size)(new ju.ArrayList[jl.Double])
    		updatedStateCollector.collected.foreach{ timeState =>
    			(histories, timeState._2.iterator.toIterable).zipped.foreach{ case (history, state) => history.add(state)}}

    		runTrace.componentHistories((components, histories).zipped.map{
    		    case (component, history) =>
              // TODO: return param traces as well?
              if (component.isInstanceOf[AcSpecies])
                Some(new ComponentHistory((skinCompartment, component.asInstanceOf[AcSpecies]), history))
              else
                None
            }.flatten.toList)
    	}
    	if (!alteredStateCollector.collected.isEmpty) {
    	    // TODO
    	}
    	runTrace
    }
}

private class HierarchicalChemistryTraceTimeRunnable[S[X] : SeqIndexAccessible](
		chemistryRunnable : TimeRunnable,
		compartmentPublishers : Iterable[(AcCompartment, Publisher[StateEvent[jl.Double, S]])]
	) extends DummyTraceTimeRunnable[jl.Double, (AcCompartment, AcSpecies)](chemistryRunnable) {

    val compartmentUpdatedStateCollectors = compartmentPublishers.map{
        		case (compartment, publisher) => {
        			val stateCollector = new ComponentStateCollector[jl.Double, AcSpecies, S]
        			publisher.subscribe(stateCollector)
        			(compartment, stateCollector)
        		}}

//    val compartmentAlteredStateCollectors =
//        if (storeOption.shouldStoreEvaluatedActions)
//            Some(compartmentPublishers.map {
//                case (compartment, publisher) => {
//                    val stateCollector = new AlteredStateCollector[jl.Double, AcSpecies, S]
//                    publisher.subscribe(stateCollector)
//                    (compartment, stateCollector)
//                }
//            })
//    	else None

    override def getRunTrace = {
    	runFor(0 : BigDecimal)
    	val runTrace = new ComponentRunTrace[jl.Double, (AcCompartment, AcSpecies)]
    	runTrace.runTime(currentTime.doubleValue)
    	// assume all collectors have the same time steps
    	runTrace.timeSteps(compartmentUpdatedStateCollectors.head._2.collected.map(_._1.doubleValue : jl.Double))
    	runTrace.componentHistories(compartmentUpdatedStateCollectors.map{
    	    case (compartment, collector) => {
    	    	val components = collector.components
    	    	val histories = collector.collected.view.map(_._2.toSeq).transpose
    	    	(components, histories).zipped.map{
    		    	case (component, history) => new ComponentHistory((compartment, component), history)}
    	    }
    	}.flatten.toList)
    	runTrace
    }
}