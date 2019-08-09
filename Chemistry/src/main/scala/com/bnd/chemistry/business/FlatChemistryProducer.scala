package com.bnd.chemistry.business

import java.{lang => jl, util => ju}

import scala.collection.JavaConversions._
import com.bnd.core.CollectionElementsConversions._
import com.bnd.chemistry.BndChemistryException
import com.bnd.chemistry.domain.AcParameter
import com.bnd.chemistry.domain.AcSpecies
import com.bnd.chemistry.domain.AcVariable
import com.bnd.chemistry.business.DoubleFunctionEvaluatorWrapper
import java.util.Arrays

import com.bnd.chemistry.domain.AcCompartment
import com.bnd.chemistry.domain.AcSimulationConfig
import com.bnd.chemistry.domain.AcParameterSet
import com.bnd.chemistry.domain.AcSpeciesSet
import com.bnd.chemistry.domain.AcReaction
import com.bnd.function.domain.Expression
import com.bnd.chemistry.domain.AcSpeciesAssociationType
import com.bnd.chemistry.domain.AcCollectiveSpeciesReactionAssociationType
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation

import scala.util.control.Breaks._
import com.bnd.chemistry.business.reactionode.AcReactionODESolverFactory
import java.util.Collections

import com.bnd.chemistry.domain.AcVariable.AcVariableIndexComparator
import com.bnd.core.runnable.StateUpdateable
import com.bnd.chemistry.domain.AcChannelDirection
import com.bnd.core.domain.DomainObject.DomainObjectKeyComparator
import com.bnd.core.domain.DomainObject
import com.bnd.core.domain.DomainObject
import com.bnd.core.dynamics.ODESolver
import com.bnd.core.runnable.{StateProducer, StateUpdateable}

abstract class FlatChemistryProducer[S[X]](spec : FlatChemistryProducerSpec) extends StateProducer[jl.Double, AcVariable[_], S] {

    val nonConstantSpeciesConvertedIndeces : Iterable[Int] = spec.nonConstantSpeciesConvertedIndecesSet.map(x => x : Int)

    // TODO: check if constant time step of ODE solver
    override def isConstantTimeStep = false

    override def listInputComponentsInOrder = {
		val all = new ju.ArrayList[AcVariable[_]]
		all.addAll(spec.speciesInEvaluationOrder)
		for (wrappedParameter <- spec.wrappedParametersInEvaluationOrder) all.add(wrappedParameter.getFunctionHolder())
		all
	}

	override def listOutputComponentsInOrder = listInputComponentsInOrder

	override def nextTimeStep = spec.reactionODESolver.getTimeStep

    
	protected def updateByParams(magnitudes : Array[jl.Double]) {
		for (wrappedParameter <- spec.wrappedParametersInEvaluationOrder)
			magnitudes.update(
			        getConvertedMagnitudeIndex(wrappedParameter.getFunctionHolder()),
			        wrappedParameter.getFunctionEvaluator().evaluate(magnitudes))
	}

	protected def addDiffs(magnitudes : Array[jl.Double], diffs : Array[jl.Double]) {
		for (index <- nonConstantSpeciesConvertedIndeces) magnitudes.update(index, magnitudes(index) + diffs(index))
	}
		
	protected def getConvertedMagnitudeIndex(index : jl.Integer) : jl.Integer = spec.magnitudeIndexConversionMap.get(index)

	protected def getConvertedMagnitudeIndex(magnitude : AcVariable[_]) : jl.Integer = getConvertedMagnitudeIndex(magnitude.getVariableIndex)
}

private class FlatChemistryProducerSpec(
    reactionODESolver : ODESolver,
	speciesInEvaluationOrder : ju.List[AcSpecies],
	wrappedParametersInEvaluationOrder : ju.List[DoubleFunctionEvaluatorWrapper[AcParameter]],
	limitHandler : AcConcentrationLimitHandler,
	speciesCount : Int,
	magnitudeIndexConversionMap : ju.Map[jl.Integer, jl.Integer],
	nonConstantSpeciesConvertedIndecesSet : ju.Set[jl.Integer]) {

    def reactionODESolver() : ODESolver = reactionODESolver
    def speciesInEvaluationOrder() : ju.List[AcSpecies] = speciesInEvaluationOrder
    def wrappedParametersInEvaluationOrder() : ju.List[DoubleFunctionEvaluatorWrapper[AcParameter]] = wrappedParametersInEvaluationOrder
    def limitHandler() : AcConcentrationLimitHandler = limitHandler 
    def speciesCount() : Int = speciesCount
    def magnitudeIndexConversionMap() : ju.Map[jl.Integer, jl.Integer] = magnitudeIndexConversionMap
	def nonConstantSpeciesConvertedIndecesSet() : ju.Set[jl.Integer] = nonConstantSpeciesConvertedIndecesSet
}

final private class ListFlatChemistryProducer(spec : FlatChemistryProducerSpec) extends FlatChemistryProducer[ju.List](spec) {

	override def nextState(currentStates : ju.List[jl.Double], timeStep : Option[Double]) : ju.List[jl.Double] = {
		val magnitudes : Array[jl.Double] = currentStates

		// update by parameters
		updateByParams(magnitudes)

		// update by reactions (non constant species only)
		val diffs = if (timeStep.isDefined)
		    spec.reactionODESolver.getApproxDiffs(magnitudes, timeStep.get)
		else
		    spec.reactionODESolver.getApproxDiffs(magnitudes)

		addDiffs(magnitudes, diffs)

		spec.limitHandler.handle(magnitudes)

		toJavaList(magnitudes)
	}

	private def toJavaList(array : Array[jl.Double]) = {
	    val list = new ju.ArrayList[jl.Double]
	    for (e <- array) list.add(e)
	    list
	}
}

final private class ArrayFlatChemistryProducer(spec : FlatChemistryProducerSpec) extends FlatChemistryProducer[Array](spec) with StateUpdateable[jl.Double, Array] {
 
 	override def nextState(magnitudes : Array[jl.Double], timeStep : Option[Double]) : Array[jl.Double] = {
        val newMagnitudes = magnitudes.clone
		updateState(newMagnitudes, timeStep)
		newMagnitudes
	}

    override def updateState(magnitudes : Array[jl.Double], timeStep : Option[Double]) {
		// update by parameters
		updateByParams(magnitudes)

		// update by reactions (non constant species only)
		val diffs = if (timeStep.isDefined)
		    spec.reactionODESolver.getApproxDiffs(magnitudes, timeStep.get)
		else
		    spec.reactionODESolver.getApproxDiffs(magnitudes)

		addDiffs(magnitudes, diffs)

		spec.limitHandler.handle(magnitudes)
    }
}

object FlatChemistryProducer {

	val acUtil = ArtificialChemistryUtil.getInstance
	val replicator = AcReplicator.getInstance

	def apply(
		compartment : AcCompartment,
		simConfig : AcSimulationConfig,
		explicitSpecies : Option[Iterable[AcSpecies]] = None,
		immutableSpecies : Option[Iterable[AcSpecies]] = None
	) : StateProducer[jl.Double, AcVariable[_], ju.List] = apply(compartment, simConfig, new ChemistryRunSetting, explicitSpecies, immutableSpecies)

	def applyArray(
		compartment : AcCompartment,
		simConfig : AcSimulationConfig,
		explicitSpecies : Option[Iterable[AcSpecies]] = None,
		immutableSpecies : Option[Iterable[AcSpecies]] = None
	) : StateProducer[jl.Double, AcVariable[_], Array] with StateUpdateable[jl.Double, Array] =
	    applyArray(compartment, simConfig, new ChemistryRunSetting, explicitSpecies, immutableSpecies)

	def apply = applyGeneric[ju.List, StateProducer[jl.Double, AcVariable[_], ju.List]](
		{spec : FlatChemistryProducerSpec => new ListFlatChemistryProducer(spec)})_

	def applyArray = applyGeneric[Array, StateProducer[jl.Double, AcVariable[_], Array] with StateUpdateable[jl.Double, Array]](
		{spec : FlatChemistryProducerSpec => new ArrayFlatChemistryProducer(spec)})_

	private def applyGeneric[S[X], SP <: StateProducer[jl.Double, AcVariable[_], S]](
	    crateNewProducer : FlatChemistryProducerSpec => SP)(
		compartment : AcCompartment,
		simConfig : AcSimulationConfig,
		setting : ChemistryRunSetting,
		explicitSpecies : Option[Iterable[AcSpecies]] = None,
		immutableSpecies : Option[Iterable[AcSpecies]] = None
	) : SP = {
	    // init reactions
		val reactionSet = replicator.cloneReactionSetWithReactionsAndGroups(compartment.getReactionSet())
		val reactions = reactionSet.getReactions.filter(_.isEnabled)
		reactions.addAll(createReverseReactionsAsForward(reactions, replicator))
		sortReactionsByIdAndSetIndex(reactions)
		if (reactions.isEmpty) throw new BndChemistryException("No enabled reactions for AC run.")

	    // init species & parameters
	    val speciesSet = compartment.getSpeciesSet
		val species = if (explicitSpecies.isDefined) explicitSpecies.get : ju.Collection[AcSpecies] else speciesSet.getOwnAndInheritedVariables
		val orderedSpecies = new ju.ArrayList[AcSpecies](species)
		Collections.sort(orderedSpecies, new AcVariableIndexComparator[AcSpecies])
		val speciesCount = species.size

		// TODO: check where virtual sum parameters are used
//		val parameterSet = copyParameterSet(speciesSet.getParameterSet)
//		addSumExpressionVirtualParameters(parameterSet, reactions)

		val params = speciesSet.getParameterSet.getVariables

		val magnitudeIndexConversionMap = createMagnitudeIndexConversionMap(species, params)
		val wrappedParametersInEvaluationOrder = createWrappedParametersInEvaluationOrder(params, speciesCount, magnitudeIndexConversionMap)
		val nonConstantSpeciesConvertedIndeces : ju.Set[jl.Integer] = if (immutableSpecies.isDefined) 
		    createMutableSpeciesConvertedIndeces(species, immutableSpecies.get, magnitudeIndexConversionMap)
		else
		    new ju.HashSet[jl.Integer]

		val reactionODESolver = AcReactionODESolverFactory.createInstance(
			simConfig.getOdeSolverType,
			simConfig.getTimeStep,
			simConfig.getTolerance,
			reactions,
			speciesCount,
			params,
			magnitudeIndexConversionMap)

		val limitHandler = new AcConcentrationLimitHandler(
			setting.upperThresholdViolationHandling,
			setting.zeroThresholdViolationHandling,
			setting.notANumberConcentrationHandling,
			simConfig.getUpperThreshold,
			simConfig.getLowerThreshold,
			speciesCount
		)

		crateNewProducer(new FlatChemistryProducerSpec(
		    reactionODESolver,
			orderedSpecies,
			wrappedParametersInEvaluationOrder,
			limitHandler,
			speciesCount,
			magnitudeIndexConversionMap,
			nonConstantSpeciesConvertedIndeces))
	}

	private def createMagnitudeIndexConversionMap(
	     species : ju.Collection[AcSpecies],
	     parameters : ju.Collection[AcParameter]
	) : ju.Map[jl.Integer, jl.Integer] = {
		 val magnitudeIndexConversionMap = new ju.HashMap[jl.Integer, jl.Integer]
		 // First sort species
		 val speciesSorted = species.toList.sortBy(_.getVariableIndex)
		 // Then parameters
		 val paramsSorted = parameters.toList.sortBy(_.getVariableIndex)

		 val indexConversionMap = (speciesSorted ++ paramsSorted).zipWithIndex.map{ case(s,i) => (s.getVariableIndex, i : jl.Integer)}.toMap
		 indexConversionMap
	}

	// TODO remove and rewrite in Scala
	private def sortReactionsByIdAndSetIndex(reactions : ju.List[AcReaction]) {
		Collections.sort(reactions.asInstanceOf[ju.List[DomainObject[jl.Long]]], new DomainObjectKeyComparator[jl.Long])
		var index = 0
		for (reaction <- reactions) {
			reaction.setIndex(index);
			index += 1;
		}
	}

	private def copyParameterSet(originalParameterSet : AcParameterSet) = {
	    val originalSpeciesSet = originalParameterSet.getSpeciesSet
		val speciesSet = new AcSpeciesSet
		speciesSet.setVarSequenceNum(originalSpeciesSet.getVarSequenceNum)
		// species will not be modified hence the original species can be set 
		speciesSet.setVariables(originalSpeciesSet.getVariables)
		speciesSet.setParentSpeciesSet(originalSpeciesSet.getParentSpeciesSet)
		// species groups will not be modified hence the original species can be set
		speciesSet.setSpeciesGroupMap(originalSpeciesSet.getSpeciesGroupMap)

		val parameterSet = new AcParameterSet
		for (originalParameter <- originalParameterSet.getVariables) {
			val parameter = new AcParameter
			parameter.setId(originalParameter.getId)
			parameter.setVariableIndex(originalParameter.getVariableIndex)
			parameter.setLabel(originalParameter.getLabel)
			parameter.setEvolFunction(originalParameter.getEvolFunction)
			parameterSet.addVariable(parameter)
		}
		speciesSet.setParameterSet(parameterSet)
		parameterSet.setSpeciesSet(speciesSet)
		parameterSet
	}

	private def addSumExpressionVirtualParameters(
		parameterSet : AcParameterSet,
		reactions : ju.Collection[AcReaction] 
	) {
		val virtualSumExpressionMultiplicityMap = new ju.HashMap[String, jl.Integer] 
		for (reaction <- reactions)
			// TODO: What about reverse reactions
			if (reaction.getForwardRateFunction == null) {
			  	if (reaction.getCollectiveCatalysisType() == AcCollectiveSpeciesReactionAssociationType.OR) {
			  		val catalystAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Catalyst);
			  		if (catalystAssocs.size() > 1) {
			  			addToVirtualSumExpressionMultiplicityMap(virtualSumExpressionMultiplicityMap, catalystAssocs)
			  		}
			  	}
			  	if (reaction.getCollectiveCatalysisType() == AcCollectiveSpeciesReactionAssociationType.OR) {
			  		val inhibitorAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Inhibitor);
			  		if (inhibitorAssocs.size() > 1) {
			  			addToVirtualSumExpressionMultiplicityMap(virtualSumExpressionMultiplicityMap, inhibitorAssocs)
			  		}
			  	}
			}

		for (virtualSumExpressioWithMultiplicity <- virtualSumExpressionMultiplicityMap.entrySet)
			if (virtualSumExpressioWithMultiplicity.getValue > 1) {
				// makes sense to introduce parameter since the expression is used at > 1 places
				val virtualParameter = new AcParameter
				val expression = virtualSumExpressioWithMultiplicity.getKey
				virtualParameter.setEvolFunction(new Expression[jl.Double, jl.Double](expression))
				parameterSet.addVariable(virtualParameter)
			}

	}

	private def createReverseReactionsAsForward(
		reactions : ju.Collection[AcReaction],
		replicator : AcReplicator 
	) = {
		val reverseReactions = new ju.ArrayList[AcReaction]
		for (reaction <- reactions)
			if (reaction.hasReverseRateConstants || reaction.hasReverseRateFunction) {
				val reverseReaction = replicator.cloneReaction(reaction)
				reverseReactions.add(reverseReaction)
				reverseReaction.setForwardRateConstants(reverseReaction.getReverseRateConstants)
				reverseReaction.setForwardRateFunction(reverseReaction.getReverseRateFunction)
				reverseReaction.setReverseRateConstants(null)
				reverseReaction.setReverseRateFunction(null)

				val reactantAssocs = reverseReaction.getSpeciesAssociations(AcSpeciesAssociationType.Product)
				val productAssocs = reverseReaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant)
				reverseReaction.initSpeciesAssociations

				reverseReaction.addSpeciesAssociations(reactantAssocs, AcSpeciesAssociationType.Reactant)				
				reverseReaction.addSpeciesAssociations(productAssocs, AcSpeciesAssociationType.Product)
				reaction.getReactionSet.addReaction(reverseReaction)
				// TODO: what about catalysts and inhibitors		
			}

		reverseReactions
	}

	private def addToVirtualSumExpressionMultiplicityMap(
		virtualSumExpressionMultiplicityMap : ju.Map[String, jl.Integer],
		ORCollectiveSpecies : ju.List[AcSpeciesReactionAssociation] 
	) {
		val virtualSumExpression = acUtil.createFoldExpression(ORCollectiveSpecies, AcCollectiveSpeciesReactionAssociationType.OR, false)
		val multiplicity = virtualSumExpressionMultiplicityMap.get(virtualSumExpression)
		if (multiplicity == null)
			virtualSumExpressionMultiplicityMap.put(virtualSumExpression, 1)
		else
			virtualSumExpressionMultiplicityMap.put(virtualSumExpression, multiplicity + 1)
	}

	private def createWrappedParametersInEvaluationOrder(
		parameters : ju.Collection[AcParameter],
		speciesCount : Int,
		magnitudeIndexConversionMap : ju.Map[jl.Integer, jl.Integer] 
	) : ju.List[DoubleFunctionEvaluatorWrapper[AcParameter]] = {
		val parameterIndeces = new ju.HashSet[jl.Integer]
		for (parameter <- parameters) parameterIndeces.add(parameter.getVariableIndex)
		val wrappedParametersInEvaluatorOrder = new ju.ArrayList[DoubleFunctionEvaluatorWrapper[AcParameter]]
//		ac.getSkinCompartment().sortParametersByIdAndSetIndex()
		val unresolvedParamDependenciesMap = new ju.HashMap[AcParameter, ju.Set[jl.Integer]]

		for (parameter <- parameters) {
			val referencedVariableIndeces = parameter.getEvolFunction.getReferencedVariableIndeces
			val referencedParameters = new ju.HashSet[jl.Integer]
			unresolvedParamDependenciesMap.put(parameter, referencedParameters)
			for (referencedVariableIndex <- referencedVariableIndeces)
				if (parameterIndeces.contains(referencedVariableIndex)) referencedParameters.add(referencedVariableIndex)
		}

		while (!unresolvedParamDependenciesMap.isEmpty) {
			var parameterWithoutDependecies : AcParameter = null
			breakable {
			    for (parameter <- unresolvedParamDependenciesMap.keySet) {
			    	val unresolvedParamDependencies = unresolvedParamDependenciesMap.get(parameter)
			    	if (unresolvedParamDependencies.isEmpty()) {
			    		parameterWithoutDependecies = parameter
			    		break
			    	}
			    }
			}
			if (parameterWithoutDependecies == null) {
				throw new BndChemistryException("Cyclic referenced between AC parameters detected.")
			}
			wrappedParametersInEvaluatorOrder.add(new DoubleFunctionEvaluatorWrapper[AcParameter](parameterWithoutDependecies, magnitudeIndexConversionMap))
			unresolvedParamDependenciesMap.remove(parameterWithoutDependecies)
			for (dependecies <- unresolvedParamDependenciesMap.values.iterator)
				dependecies.remove(parameterWithoutDependecies.getVariableIndex)

		}
		wrappedParametersInEvaluatorOrder
	}

	private def createMutableSpeciesConvertedIndeces(
		species : ju.Collection[AcSpecies],
		immutableSpecies : Iterable[AcSpecies],
		magnitudeIndexConversionMap : ju.Map[jl.Integer, jl.Integer] 
	) = {
		val speciesCount = species.size
		val mutableSpeciesConvertedIndeces = new ju.HashSet[jl.Integer]
		for (speciesIndex <- 0 until speciesCount) mutableSpeciesConvertedIndeces.add(speciesIndex)
		if (immutableSpecies != null)
			for (oneSpecies <- immutableSpecies) {
				val convertedIndex = magnitudeIndexConversionMap.get(oneSpecies.getVariableIndex())
				mutableSpeciesConvertedIndeces.remove(convertedIndex)
			}
		mutableSpeciesConvertedIndeces
	}
}