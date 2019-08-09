package com.bnd.chemistry.business;

import java.util.ArrayList;
import java.util.Collection;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.domain.*;

public abstract class AcReactionSetConverter {

	private final AcReactionSet inputReactionSet;
	private AcReactionSet outputReactionSet;

	protected final ArtificialChemistryUtil acUtil = ArtificialChemistryUtil.getInstance();
	protected final AcReplicator replicator = AcReplicator.getInstance();

	public AcReactionSetConverter(AcReactionSet inputReactionSet) {
		this.inputReactionSet = inputReactionSet;
		validate();
	}

	protected void validate() {
		for (AcReaction inputReaction : inputReactionSet.getReactions()) {
			validate(inputReaction);
		}
	}

	public AcReactionSet convert() {
		// new species set
		AcSpeciesSet newSpeciesSet = new AcSpeciesSet();
		newSpeciesSet.setParentSpeciesSet(inputReactionSet.getSpeciesSet());
		AcParameterSet newParameterSet = new AcParameterSet();
		newSpeciesSet.setParameterSet(newParameterSet);
		newParameterSet.setSpeciesSet(newSpeciesSet);
		newSpeciesSet.initVarSequenceNum();

		// new reaction set - first copy all reactions and groups
		outputReactionSet = replicator.cloneReactionSetWithReactionsAndGroups(inputReactionSet);
		outputReactionSet.setSpeciesSet(newSpeciesSet);

		// then apply conversion if applicable
		Collection<AcReaction> reactionsCopy = new ArrayList<AcReaction>(outputReactionSet.getReactions());
		for (AcReaction reaction : reactionsCopy) {
			if (isConvertible(reaction)) {
				// reaction is convertible, so convert it and add results
				final Collection<AcReaction> newReactions = convert(reaction);
				if (newReactions == null || newReactions.isEmpty()) {
					throw new BndChemistryException("Reaction conversion returned no reactions.");
				}
				addReactions(newReactions, reaction.getGroup());
				addSpecies(newReactions);
				removeReaction(reaction);
			}
		}

		if (!newSpeciesSet.hasOwnVariables()) {
			// no new species has been introduced so set the input species set for the output reaction set
			outputReactionSet.setSpeciesSet(inputReactionSet.getSpeciesSet());			
		}
		return outputReactionSet;
	}

	protected void addReactions(Collection<AcReaction> newReactions, AcReactionGroup originalReactionGroup) {
		for (final AcReaction newReaction : newReactions) {
			if (originalReactionGroup != null) {
				originalReactionGroup.addReaction(newReaction);
			}
			outputReactionSet.addReaction(newReaction);   
			newReaction.setSortOrder(outputReactionSet.getReactionsNum() - 1);
		}
	}

	protected void addSpecies(Collection<AcReaction> newReactions) {
		for (final AcReaction newReaction : newReactions) {
			for (final AcSpeciesReactionAssociation assoc : newReaction.getSpeciesAssociations()) {
				AcSpecies species = assoc.getSpecies();
				if (species.getParentSet() == null) {
					// does not have a parent set must be a new one
					getOutputSpeciesSet().addVariable(species);
					species.setSortOrder(species.getVariableIndex());
				}
			}
		}
	}

	protected void removeReaction(AcReaction reaction) {
		reaction.removeFromGroup();
		reaction.removeFromReactionSet();
	}

	protected abstract void validate(AcReaction reaction);
	
	protected abstract Collection<AcReaction> convert(AcReaction inputReaction);

	protected abstract boolean isConvertible(AcReaction inputReaction);

	protected AcSpeciesSet getOutputSpeciesSet() {
		return outputReactionSet.getSpeciesSet();
	}

	protected AcReactionSet getInputReactionSet() {
		return inputReactionSet;
	}

	protected Collection<AcReaction> getInputReactions() {
		return getInputReactionSet().getReactions();
	}

	protected AcSpeciesSet getInputSpeciesSet() {
		return inputReactionSet.getSpeciesSet();
	}
}