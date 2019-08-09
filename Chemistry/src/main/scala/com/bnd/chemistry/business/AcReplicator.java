package com.bnd.chemistry.business;

import java.util.*;

import com.bnd.core.reflection.ReflectionProvider;
import com.bnd.core.reflection.SpringReflectionProviderImpl;

import com.bnd.chemistry.domain.*;
import com.bnd.core.util.ObjectUtil;
import com.bnd.function.domain.AbstractFunction;
import com.bnd.function.domain.Function;
import com.bnd.math.domain.dynamics.MultiRunAnalysisSpec;
import com.bnd.math.domain.dynamics.SingleRunAnalysisSpec;
import com.bnd.math.domain.rand.RandomDistribution;

public class AcReplicator {

	private static AcReplicator instance = new AcReplicator();

	private AcReplicator() {
		// nothing to do
	}

	public static AcReplicator getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	private <T> T clone(T object) {
	    if (object == null)
	        return null;

        ReflectionProvider<T> rf = new SpringReflectionProviderImpl<T>((Class<T>) object.getClass());
        return rf.clone(object);
	}

    private <T> T clone(T object, Class<T> clazz) {
	    if (object == null)
	        return null;

        ReflectionProvider<T> rf = new SpringReflectionProviderImpl<T>(clazz);
        T copiedObject = rf.createNewInstance();
        rf.copy(object, copiedObject);
        return copiedObject;
	}

	public void nullIdAndVersionRecursively(AcReactionSet reactionSet) {
		ObjectUtil.nullIdAndVersion(reactionSet);
		for (AcReaction reaction : reactionSet.getReactions()) {
			nullIdAndVersion(reaction);
		}
		ObjectUtil.nullIdAndVersion(reactionSet.getGroups());
	}

	public void nullIdAndVersion(AcReaction reaction) {
		ObjectUtil.nullIdAndVersion(reaction);
		ObjectUtil.nullIdAndVersion((AbstractFunction<Double, Double>) reaction.getForwardRateFunction());
		ObjectUtil.nullIdAndVersion((AbstractFunction<Double, Double>) reaction.getReverseRateFunction());
		ObjectUtil.nullIdAndVersion(reaction.getSpeciesAssociations());
	}

	public AcCompartment cloneCompartmentWithReactionSet(AcCompartment compartment) {
		AcCompartment compartmentClone = clone(compartment);
		compartmentClone.setReactionSet(cloneReactionSetWithReactionsAndGroups(compartment.getReactionSet()));
		return compartmentClone;
	}

	public AcCompartment cloneCompartmentWithChannelsRecursively(
		AcCompartment compartment,
		Map<AcCompartment, AcCompartment> compartmentCloneMap
	) {
		Map<AcCompartmentChannel, AcCompartmentChannel> channelCloneMap = new HashMap<AcCompartmentChannel, AcCompartmentChannel>();
		return cloneCompartmentWithChannelsRecursively(compartment, compartmentCloneMap, channelCloneMap);
	}

	public AcCompartment cloneCompartmentWithChannelsRecursively(
		AcCompartment compartment,
		Map<AcCompartment, AcCompartment> compartmentCloneMap,
		Map<AcCompartmentChannel, AcCompartmentChannel> channelCloneMap
	) {
		AcCompartment compartmentClone = clone(compartment);
		cloneChannels(compartment, compartmentClone, channelCloneMap);
		compartmentClone.setSubCompartmentAssociations(new ArrayList<AcCompartmentAssociation>());
		compartmentClone.setParentCompartmentAssociations(new ArrayList<AcCompartmentAssociation>());

		compartmentCloneMap.put(compartment, compartmentClone);
		for (AcCompartmentAssociation subCompartmentAssoc : compartment.getSubCompartmentAssociations()) {
			final AcCompartment subCompartmentClone = cloneCompartmentWithChannelsRecursively(subCompartmentAssoc.getSubCompartment(), compartmentCloneMap);
			compartmentClone.addSubCompartment(subCompartmentClone);
		}

		cloneSubChannelGroups(compartment, compartmentClone, channelCloneMap);

		return compartmentClone;
	}

	public void cloneChannels(
		AcCompartment compartment,
		AcCompartment compartmentClone,
		Map<AcCompartmentChannel, AcCompartmentChannel> channelCloneMap
	) {
		compartmentClone.setChannels(new ArrayList<AcCompartmentChannel>());
		for (AcCompartmentChannel channel : compartment.getChannels()) {
			AcCompartmentChannel channelClone = clone(channel);
			channelCloneMap.put(channel, channelClone);
			compartmentClone.addChannel(channelClone);			
		}
	}

	public void cloneSubChannelGroups(
		AcCompartment compartment,
		AcCompartment compartmentClone,
		Map<AcCompartmentChannel, AcCompartmentChannel> channelCloneMap
	) {
		compartmentClone.setSubChannelGroups(new ArrayList<AcCompartmentChannelGroup>());
		for (AcCompartmentChannelGroup channelGroup : compartment.getSubChannelGroups()) {
			AcCompartmentChannelGroup channelGroupClone = clone(channelGroup);
			channelGroupClone.setChannels(new ArrayList<AcCompartmentChannel>());
			for (AcCompartmentChannel channel : channelGroup.getChannels()) {
				channelGroupClone.addChannel(channelCloneMap.get(channel));
			}
			compartmentClone.addSubChannelGroup(channelGroupClone);			
		}
	}

	public AcReactionSet cloneReactionSetWithReactionsAndGroups(AcReactionSet reactionSet) {
		AcReactionSet reactionSetClone = clone(reactionSet);
		reactionSetClone.initReactions();
		reactionSetClone.initGroups();
		for (AcReactionGroup reactionGroup : reactionSet.getGroups()) {
			AcReactionGroup reactionGroupClone = clone(reactionGroup);
			reactionGroupClone.initReactions();
			reactionSetClone.addGroup(reactionGroupClone);
			// assuming each reaction is part of max one group
			for (AcReaction reaction : reactionGroup.getReactions()) {
				AcReaction reactionClone = cloneReaction(reaction);
				reactionGroupClone.addReaction(reactionClone);
				reactionSetClone.addReaction(reactionClone);
			}
		}
		for (AcReaction reaction : reactionSet.getReactions()) {
			if (!reaction.hasGroup()) {
				reactionSetClone.addReaction(cloneReaction(reaction));
			}
		}
		return reactionSetClone;
	}

	public AcReaction cloneReaction(AcReaction reaction) {
		AcReaction reactionClone = clone(reaction);
		if (reaction.getForwardRateConstants() != null) {
			reactionClone.setForwardRateConstants(reaction.getForwardRateConstants().clone());
		}
		if (reaction.getReverseRateConstants() != null) {
			reactionClone.setReverseRateConstants(reaction.getReverseRateConstants().clone());
		}
		reactionClone.setForwardRateFunction(clone(reaction.getForwardRateFunction()));
		reactionClone.setReverseRateFunction(clone(reaction.getReverseRateFunction()));
		reactionClone.initSpeciesAssociations();
		for (AcSpeciesReactionAssociation speciesAssoc : reaction.getSpeciesAssociations()) {
			reactionClone.addSpeciesAssociation(clone(speciesAssoc));			
		}
		return reactionClone;
	}

	public AcReactionGroup cloneReactionGroup(AcReactionGroup reactionGroup) {
		return clone(reactionGroup);
	}

	public <IN, OUT> Function<IN, OUT> cloneFunction(Function<IN, OUT> function) {
		return clone(function);
	}

    public AcEvaluation cloneEvaluation(AcEvaluation evaluation) {
        AcEvaluation evaluationClone = clone(evaluation);
        evaluationClone.setEvalFunction(clone(evaluationClone.getEvalFunction()));
        return evaluationClone;
    }

	public void nullIdAndVersionRecursively(AcInteractionSeries actionSeries) {
		ObjectUtil.nullIdAndVersion(actionSeries);
		ObjectUtil.nullIdAndVersion(actionSeries.getVariables());
		for (AcInteraction action : actionSeries.getActions()) {
			action.setId(null);
			action.setVersion(new Long(1));
			ObjectUtil.nullIdAndVersion(action.getSpeciesActions());
			for (AcSpeciesInteraction speciesAction : action.getSpeciesActions()) {
				ObjectUtil.nullIdAndVersion((AbstractFunction<Double, Double>) speciesAction.getSettingFunction());
			}
			ObjectUtil.nullIdAndVersion(action.getVariableAssignments());
			for (AcInteractionVariableAssignment assignment : action.getVariableAssignments()) {
				ObjectUtil.nullIdAndVersion((AbstractFunction<Double, Double>) assignment.getSettingFunction());
			}
		}
	}

	public void nullIdAndVersionRecursively(AcCompartment compartment) {
		ObjectUtil.nullIdAndVersion(compartment);
		ObjectUtil.nullIdAndVersion(compartment.getChannels());
		for (AcCompartmentChannelGroup channelGroup : compartment.getSubChannelGroups()) {
			channelGroup.setId(null);
			channelGroup.setVersion(new Long(1));
		}
		for (AcCompartment subCompartment : compartment.getSubCompartments()) {
			nullIdAndVersionRecursively(subCompartment);
		}
	}

	public void nullIdAndVersion(AcTranslationSeries translationSeries) {
		ObjectUtil.nullIdAndVersion(translationSeries);
		ObjectUtil.nullIdAndVersion(translationSeries.getVariables());
		ObjectUtil.nullIdAndVersion(translationSeries.getTranslations());

		for (AcTranslation rangeTranslation : translationSeries.getTranslations()) {
			ObjectUtil.nullIdAndVersion(rangeTranslation.getTranslationItems());
			for (AcTranslationItem translationItem : rangeTranslation.getTranslationItems()) {
				ObjectUtil.nullIdAndVersion((AbstractFunction<Object, Double>) translationItem.getTranslationFunction());
			}
		}
	}

	public AcInteractionSeries cloneActionSeriesWithActionsRecursively(AcInteractionSeries actionSeries) {
		return cloneActionSeriesWithActionsRecursively(actionSeries, new HashMap<AcInteractionSeries, AcInteractionSeries>());
	}

	public AcInteractionSeries cloneActionSeriesWithActionsRecursively(
		AcInteractionSeries actionSeries,
		Map<AcInteractionSeries, AcInteractionSeries> actionSeriesCloneMap
	) {
		AcInteractionSeries actionSeriesClone = cloneActionSeriesWithActions(actionSeries);
		actionSeriesCloneMap.put(actionSeries, actionSeriesClone);
		for (AcInteractionSeries subActionSeries : actionSeries.getSubActionSeries()) {
			final AcInteractionSeries subActionSeriesClone = cloneActionSeriesWithActionsRecursively(subActionSeries, actionSeriesCloneMap);
			actionSeriesClone.addSubActionSeries(subActionSeriesClone);
		}
		return actionSeriesClone;
	}

	public AcInteractionSeries cloneActionSeriesWithActions(AcInteractionSeries actionSeries) {
		// TODO: class is passed here to fix java assist proxying... resolve more generally
		AcInteractionSeries actionSeriesClone = clone(actionSeries, AcInteractionSeries.class);

		// variables
		Map<AcInteractionVariable, AcInteractionVariable> variableCloneMap = new HashMap<AcInteractionVariable, AcInteractionVariable>();
		actionSeriesClone.setVariables(new HashSet<AcInteractionVariable>());
		for (AcInteractionVariable variable : actionSeries.getVariables()) {
			final AcInteractionVariable variableClone = clone(variable); 
			actionSeriesClone.addVariable(variableClone);
			variableCloneMap.put(variable, variableClone);
		}

		actionSeriesClone.setImmutableSpecies(new ArrayList<AcSpecies>());
		for (AcSpecies species : actionSeries.getImmutableSpecies()) {
			actionSeriesClone.addImmutableSpecies(species);
		}

		// actions
		actionSeriesClone.initActions();
		for (AcInteraction action : actionSeries.getActions()) {
			actionSeriesClone.addAction(cloneActionWithSpeciesActions(action, variableCloneMap));			
		}
		actionSeriesClone.setSubActionSeries(new ArrayList<AcInteractionSeries>());
		actionSeriesClone.setParent(null);

		return actionSeriesClone;
	}

	public AcInteraction cloneActionWithSpeciesActions(AcInteraction action) {
		AcInteraction actionClone = clone(action);
		actionClone.initSpeciesActions();
		for (AcSpeciesInteraction speciesAction : action.getSpeciesActions()) {
			actionClone.addToSpeciesActions(cloneSpeciesAction(speciesAction));			
		}

		actionClone.setVariableAssignments(new HashSet<AcInteractionVariableAssignment>());
		for (AcInteractionVariableAssignment assignment : action.getVariableAssignments()) {
			actionClone.addVariableAssignment(cloneInteractionVariableAssignment(assignment));			
		}
		return actionClone;
	}

	public AcInteraction cloneActionWithSpeciesActions(AcInteraction action, Map<AcInteractionVariable, AcInteractionVariable> variableCloneMap) {
		AcInteraction actionClone = clone(action);
		actionClone.initSpeciesActions();
		for (AcSpeciesInteraction speciesAction : action.getSpeciesActions()) {
			actionClone.addToSpeciesActions(cloneSpeciesAction(speciesAction));			
		}

		actionClone.setVariableAssignments(new HashSet<AcInteractionVariableAssignment>());
		for (AcInteractionVariableAssignment assignment : action.getVariableAssignments()) {
			final AcInteractionVariableAssignment assignmentClone = cloneInteractionVariableAssignment(assignment);
			assignmentClone.setVariable(variableCloneMap.get(assignment.getVariable()));
			actionClone.addVariableAssignment(assignmentClone);			
		}

		return actionClone;
	}

	public AcSpeciesInteraction cloneSpeciesAction(AcSpeciesInteraction speciesAction) {
		AcSpeciesInteraction speciesActionClone = clone(speciesAction);
		speciesActionClone.setSettingFunction(clone(speciesAction.getFunction()));
		return speciesActionClone;
	}

	public AcInteractionVariableAssignment cloneInteractionVariableAssignment(AcInteractionVariableAssignment assignment) {
		AcInteractionVariableAssignment assignmentClone = clone(assignment);
		assignmentClone.setSettingFunction(clone(assignment.getFunction()));
		return assignmentClone;
	}

	public AcSpeciesSet cloneSpeciesSetWithParameterSet(AcSpeciesSet speciesSet) {
		AcSpeciesSet speciesSetClone = clone(speciesSet);
		speciesSetClone.initVariables();
		for (AcSpecies species : speciesSet.getVariables()) {
			speciesSetClone.addVariable(clone(species));			
		}
		AcParameterSet parameterSet = speciesSet.getParameterSet();
		AcParameterSet parameterSetClone = clone(parameterSet);
		parameterSetClone.initVariables();
		for (final AcParameter parameter : parameterSet.getVariables()) {
			final AcParameter parameterClone = clone(parameter);
			parameterClone.setEvolFunction(clone(parameter.getFunction()));
			parameterSetClone.addVariable(parameterClone);			
		}
		speciesSetClone.setParameterSet(parameterSetClone);
		parameterSetClone.setSpeciesSet(speciesSetClone);
		return speciesSetClone;
	}

	public SingleRunAnalysisSpec cloneSingleRunAnalysisSpec(SingleRunAnalysisSpec singleRunAnalysisSpec) {
		SingleRunAnalysisSpec singleRunAnalysisSpecClone = clone(singleRunAnalysisSpec);
		singleRunAnalysisSpecClone.setId(null);
		singleRunAnalysisSpecClone.setVersion(1l);

		return singleRunAnalysisSpecClone;
	}

	public <T> MultiRunAnalysisSpec<T> cloneMultiRunAnalysisSpec(MultiRunAnalysisSpec<T> multiRunAnalysisSpec) {
		MultiRunAnalysisSpec<T> multiRunAnalysisSpecClone = clone(multiRunAnalysisSpec);
		multiRunAnalysisSpecClone.setId(null);
		multiRunAnalysisSpecClone.setVersion(1l);

		RandomDistribution<T> initialStateDistributionClone = clone(multiRunAnalysisSpec.getInitialStateDistribution());
		initialStateDistributionClone.setId(null);
		initialStateDistributionClone.setVersion(1l);		
		multiRunAnalysisSpecClone.setInitialStateDistribution(initialStateDistributionClone);
		
		return multiRunAnalysisSpecClone;
	}

	public ArtificialChemistrySpec cloneArtificialChemistrySpec(ArtificialChemistrySpec acReactionSetSpec) {
		ArtificialChemistrySpec acSpecClone = clone(acReactionSetSpec);
		acSpecClone.setId(null);
		acSpecClone.setVersion(1l);
		acSpecClone.setAcs(null);

		return acSpecClone;
	}

	public AcTranslationSeries cloneTranslationSeriesWithTranslations(AcTranslationSeries translationSeries) {
		AcTranslationSeries translationSeriesClone = clone(translationSeries);

		Map<AcTranslationVariable, AcTranslationVariable> variableCloneMap = new HashMap<AcTranslationVariable, AcTranslationVariable>();
		translationSeriesClone.setVariables(new HashSet<AcTranslationVariable>());
		for (AcTranslationVariable variable : translationSeries.getVariables()) {
			final AcTranslationVariable variableClone = clone(variable); 
			translationSeriesClone.addVariable(variableClone);
			variableCloneMap.put(variable, variableClone);
		}

		translationSeriesClone.setTranslations(new HashSet<AcTranslation>());
		for (AcTranslation rangeTranslation : translationSeries.getTranslations()) {
			translationSeriesClone.addTranslation(cloneTranslationWithItems(rangeTranslation, variableCloneMap));			
		}

		return translationSeriesClone;
	}

	public AcTranslation cloneTranslationWithItems(AcTranslation translation) {
		AcTranslation translationClone = clone(translation);
		translationClone.setTranslationItems(new HashSet<AcTranslationItem>());
		for (AcTranslationItem translationItem : translation.getTranslationItems()) {
			translationClone.addTranslationItem(cloneTranslationItem(translationItem));			
		}
		return translationClone;
	}

	public AcTranslation cloneTranslationWithItems(
		AcTranslation translation,
		Map<AcTranslationVariable, AcTranslationVariable> variableCloneMap
	) {
		AcTranslation rangeTranslationClone = clone(translation);
		rangeTranslationClone.setTranslationItems(new HashSet<AcTranslationItem>());
		for (AcTranslationItem translationItem : translation.getTranslationItems()) {
			final AcTranslationItem translationItemClone = cloneTranslationItem(translationItem);
			translationItemClone.setVariable(variableCloneMap.get(translationItem.getVariable()));
			rangeTranslationClone.addTranslationItem(translationItemClone);
		}

		return rangeTranslationClone;
	}

	public AcTranslationItem cloneTranslationItem(AcTranslationItem translationItem) {
		AcTranslationItem translationItemClone = clone(translationItem);
		translationItemClone.setTranslationFunction(clone(translationItem.getFunction()));
		return translationItemClone;
	}
}