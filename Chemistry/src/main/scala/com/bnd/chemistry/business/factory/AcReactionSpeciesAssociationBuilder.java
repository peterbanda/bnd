package com.bnd.chemistry.business.factory;

import java.util.*;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.domain.*;
import com.bnd.core.util.RandomUtil;

class AcReactionSpeciesAssociationBuilder {

	private final Collection<AcReaction> reactions;
	private final Collection<AcSpecies> species;
	private final Map<AcSpeciesAssociationType, Collection<AcSpecies>> assocTypeAllowedSpeciesMap;
	private final Map<AcSpeciesAssociation, Set<AcReaction>> speciesAssocReactionMap;
	private final AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy;

	protected AcReactionSpeciesAssociationBuilder(
		Collection<AcReaction> reactions,
		Collection<AcSpecies> species,
		AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy
	) {
		this(reactions, species, null, speciesForbiddenRedundancy);
	}

	protected AcReactionSpeciesAssociationBuilder(
		Collection<AcReaction> reactions,
		Collection<AcSpecies> species,
		Map<AcSpeciesAssociationType, Collection<AcSpecies>> assocTypeAllowedSpeciesMap,
		AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy
	) {
		this.reactions = reactions;
		this.species = species;
		this.assocTypeAllowedSpeciesMap = assocTypeAllowedSpeciesMap;
		this.speciesForbiddenRedundancy = speciesForbiddenRedundancy;
		this.speciesAssocReactionMap = createSpeciesAssocReactionMap(species);
	}

	private static Map<AcSpeciesAssociation, Set<AcReaction>> createSpeciesAssocReactionMap(Collection<AcSpecies> species) {
		Map<AcSpeciesAssociation, Set<AcReaction>> speciesAssocReactionMap = new HashMap<AcSpeciesAssociation, Set<AcReaction>>();
		for (AcSpecies oneSpecies : species) {
			for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
				speciesAssocReactionMap.put(new AcSpeciesAssociation(oneSpecies, assocType), new HashSet<AcReaction>());
			}
		}
		return speciesAssocReactionMap;
	}
	
	protected void linkReactionAndSpecies(
		int speciesAssocsNumber,
		AcSpeciesAssociationType assocType
	) {
		AcSpeciesAssociationType[] restrictedAssocTypes = new AcSpeciesAssociationType[] {assocType};
		switch (speciesForbiddenRedundancy) {
			case All:
				restrictedAssocTypes = AcSpeciesAssociationType.values();
			case SameAssocTypeAndReactantProduct:
				if (assocType == AcSpeciesAssociationType.Reactant || assocType == AcSpeciesAssociationType.Product) {
					restrictedAssocTypes = new AcSpeciesAssociationType[] {AcSpeciesAssociationType.Reactant, AcSpeciesAssociationType.Product, assocType};
				}
			case SameAssocType:
				for (int i = 0; i < speciesAssocsNumber; i++) {
					Collection<AcSpecies> speciesToSelect = getSpeciesNotAssociatedInAllReactions(restrictedAssocTypes);
					if (speciesToSelect.isEmpty()) {
						throw new BndChemistryException("No species to choose from.");
					}
					AcSpecies oneSelectedSpecies = RandomUtil.nextElement(speciesToSelect.toArray(new AcSpecies[0]));
					Set<AcReaction> availableReactions = getNonAssociatedReactions(oneSelectedSpecies, restrictedAssocTypes);
					AcReaction selectedReaction = RandomUtil.nextElement(availableReactions.toArray(new AcReaction[0]));
					createSpeciesAssoc(selectedReaction, oneSelectedSpecies, assocType);
				}
				break;
			case None:
				Collection<AcSpecies> selectedSpecies = RandomUtil.nextElementsWithRepetitions(getAllowedSpecies(assocType).toArray(new AcSpecies[0]), speciesAssocsNumber);
				Collection<AcReaction> selectedReactions = RandomUtil.nextElementsWithRepetitions(reactions.toArray(new AcReaction[0]), speciesAssocsNumber);
				Iterator<AcSpecies> selectedSpeciesInterator = selectedSpecies.iterator();
				for (AcReaction selectedReaction : selectedReactions) {
					createSpeciesAssoc(selectedReaction, selectedSpeciesInterator.next(), assocType);
				}				
				break;
		}
	}

	protected void linkReactionToSpecies(
		AcReaction reaction,
		int speciesAssocsNumberPerReaction,
		AcSpeciesAssociationType assocType
	) {
		linkReactionToSpecies(reaction, speciesAssocsNumberPerReaction, assocType, getAllowedSpecies(assocType));
	}

	private void linkReactionToSpecies(
		AcReaction reaction,
		int speciesAssocsNumberPerReaction,
		AcSpeciesAssociationType assocType,
		Collection<AcSpecies> allowedSpecies
	) {
		Collection<AcSpecies> selectedSpecies = null;
		Collection<AcSpecies> availableSpecies = null;
		switch (speciesForbiddenRedundancy) {
			case None:
				selectedSpecies = RandomUtil.nextElementsWithRepetitions(allowedSpecies.toArray(new AcSpecies[0]), speciesAssocsNumberPerReaction);
				break;
			case All:
				availableSpecies = getNonAssociatedSpecies(reaction, null, allowedSpecies);
				selectedSpecies = RandomUtil.nextElementsWithRepetitions(availableSpecies.toArray(new AcSpecies[0]), speciesAssocsNumberPerReaction);
				break;
			case SameAssocType:
				selectedSpecies = RandomUtil.nextElementsWithoutRepetitions(allowedSpecies, speciesAssocsNumberPerReaction);
				break;
			case SameAssocTypeAndReactantProduct:
				switch (assocType) {
					case Reactant:
						availableSpecies = getNonAssociatedSpecies(reaction, AcSpeciesAssociationType.Product, allowedSpecies);
						break;
					case Product:
						availableSpecies = getNonAssociatedSpecies(reaction, AcSpeciesAssociationType.Reactant, allowedSpecies);
						break;
					case Catalyst:
					case Inhibitor:
						availableSpecies = allowedSpecies;
						break;
				}
				selectedSpecies = RandomUtil.nextElementsWithoutRepetitions(availableSpecies, speciesAssocsNumberPerReaction);
				break;
		}
		if (selectedSpecies.size() != speciesAssocsNumberPerReaction) {
			throw new BndChemistryException("Species assocs. number per reaction '" + speciesAssocsNumberPerReaction + "' does not match '" + selectedSpecies.size() + "'.");
		}
		createSpeciesAssocs(reaction, selectedSpecies, assocType);
	}

	protected void linkReactionsToSpecies(
		Integer speciesAssocsNumberPerReaction,
		Integer maxReactionsNumPerSpecies,
		AcSpeciesAssociationType assocType
	) {
		if (speciesAssocsNumberPerReaction == null) {
			return;
		}
		Set<AcSpecies> nonsaturatedSpecies = new HashSet<AcSpecies>();
		nonsaturatedSpecies.addAll(getAllowedSpecies(assocType));
		for (AcReaction reaction : reactions) { 
			if (nonsaturatedSpecies.size() < speciesAssocsNumberPerReaction) {
				throw new BndChemistryException("It's not possible to fulfill the reaction-to-species generation requirement: Not enough species for association type '" + assocType + "', species assocs. per reaction '" + speciesAssocsNumberPerReaction + "', current allowed species num '" + nonsaturatedSpecies.size() + "'.");
			}
			linkReactionToSpecies(reaction, speciesAssocsNumberPerReaction, assocType, nonsaturatedSpecies);
			if (maxReactionsNumPerSpecies != null) {
				updateNonsaturatedSpeciesSet(nonsaturatedSpecies, assocType, maxReactionsNumPerSpecies);
			}
		}
	}

	protected void linkSpeciesToReactions(
		Integer maxSpeciesAssocsNumPerReaction,
		Integer targetReactionsNumPerSpecies,
		AcSpeciesAssociationType assocType
	) {
		if (targetReactionsNumPerSpecies == null) {
			return;
		}
		Set<AcReaction> nonsaturatedReactions = new HashSet<AcReaction>();
		nonsaturatedReactions.addAll(reactions);
		for (AcSpecies oneSpecies : getAllowedSpecies(assocType)) {
			linkSpeciesToReactions(oneSpecies, targetReactionsNumPerSpecies, nonsaturatedReactions, assocType);
			if (maxSpeciesAssocsNumPerReaction != null) {
				updateNonsaturatedReactionSet(nonsaturatedReactions, maxSpeciesAssocsNumPerReaction, assocType);
			}
		}
	}

	private void updateNonsaturatedSpeciesSet(
		Set<AcSpecies> nonsaturatedSpecies,
		AcSpeciesAssociationType assocType,
		int maxReactionsNumPerSpecies
	) {
		Set<AcSpecies> nonsaturatedSpeciesCopy = new HashSet<AcSpecies>();
		nonsaturatedSpeciesCopy.addAll(nonsaturatedSpecies);
		for (AcSpecies oneNonsaturatedSpecies : nonsaturatedSpeciesCopy) {
			if (!acceptsMoreReactions(oneNonsaturatedSpecies, maxReactionsNumPerSpecies, assocType)) {
				nonsaturatedSpecies.remove(oneNonsaturatedSpecies);
			}
		}
	}

	protected void linkSpeciesToReactions(
		AcSpecies selectedSpecies,
		int targetReactionsNum,
		Set<AcReaction> allowedReactions,
		AcSpeciesAssociationType assocType
	) {
		final int currentReactionsNum = getAssociatedReactionsNum(selectedSpecies, assocType);
		final int remainingReactionsNum = targetReactionsNum - currentReactionsNum;
		if (remainingReactionsNum < 0) {
			throw new BndChemistryException("Current number of reactions per species '" + currentReactionsNum + "' is greater than the target number reactions per species + '" + targetReactionsNum + "' for assoc. type '" + assocType + "'");
		} else if (remainingReactionsNum == 0) {
			return;
		}
		Set<AcReaction> availableReactions = allowedReactions;
		switch (speciesForbiddenRedundancy) {
			case All:
				// TODO
			case None:
				// TODO
			case SameAssocTypeAndReactantProduct:
				switch (assocType) {
				case Reactant:
					availableReactions = getNonAssociatedReactions(selectedSpecies, AcSpeciesAssociationType.Product, availableReactions);
					break;
				case Product:
					availableReactions = getNonAssociatedReactions(selectedSpecies, AcSpeciesAssociationType.Reactant, availableReactions);
					break;
				case Catalyst:
				case Inhibitor:
					break;
			}
			case SameAssocType:
				availableReactions = getNonAssociatedReactions(selectedSpecies, assocType, availableReactions);
				final Collection<AcReaction> selectedReactions = RandomUtil.nextElementsWithoutRepetitions(availableReactions, remainingReactionsNum);
				for (AcReaction reaction : selectedReactions) {
					createSpeciesAssoc(reaction, selectedSpecies, assocType);
				}
			break;
		}
	}

	private void updateNonsaturatedReactionSet(
		Set<AcReaction> nonsaturatedReaction,
		int maxSpeciesAssocsNumPerReaction,
		AcSpeciesAssociationType assocType
	) {
		Set<AcReaction> nonsaturatedReactionCopy = new HashSet<AcReaction>();
		nonsaturatedReactionCopy.addAll(nonsaturatedReaction);
		for (final AcReaction oneNonsaturatedReaction : nonsaturatedReactionCopy) {
			if (!acceptsMoreSpeciesAssocs(oneNonsaturatedReaction, maxSpeciesAssocsNumPerReaction, assocType)) {
				nonsaturatedReaction.remove(oneNonsaturatedReaction);
			}
		}
	}

	private boolean acceptsMoreSpeciesAssocs(
		AcReaction reaction,
		int maxSpeciesAssocsNumPerReaction,
		AcSpeciesAssociationType assocType
	) {
		final Integer currentAssocsNum = reaction.getSpeciesAssociations(assocType).size();
		return maxSpeciesAssocsNumPerReaction > currentAssocsNum;
	}

	private boolean acceptsMoreReactions(
		AcSpecies species,
		int maxReactionsNumPerSpecies,
		AcSpeciesAssociationType assocType		
	) {
		final int currentReactionsNum = getAssociatedReactionsNum(species, assocType);
		return maxReactionsNumPerSpecies > currentReactionsNum;
	}

	public Set<AcSpecies> getAssociatedSpecies(
		AcReaction reaction,
		AcSpeciesAssociationType assocType
	) {
		Set<AcSpecies> associatedSpecies = new HashSet<AcSpecies>();
		Set<AcSpeciesReactionAssociation> filteredAssocs = AcSpeciesReactionAssociation.filterType(reaction.getSpeciesAssociations(), assocType);
		for (AcSpeciesReactionAssociation speciesAssoc : filteredAssocs) {
			associatedSpecies.add(speciesAssoc.getSpecies());
		}
		return associatedSpecies;
	}

	private Set<AcSpecies> getNonAssociatedSpecies(
		AcReaction reaction,
		AcSpeciesAssociationType assocType
	) {
		return getNonAssociatedSpecies(reaction, assocType, getAllowedSpecies(assocType));
	}

	private Set<AcSpecies> getNonAssociatedSpecies(
		AcReaction reaction,
		AcSpeciesAssociationType assocType,
		Collection<AcSpecies> allowedSpecies
	) {
		Set<AcSpecies> nonAssociatedSpecies = new HashSet<AcSpecies>();
		nonAssociatedSpecies.addAll(allowedSpecies);
		nonAssociatedSpecies.removeAll(getAssociatedSpecies(reaction, assocType));
		return nonAssociatedSpecies;
	}

	public Set<AcSpecies> getSpeciesNotAssociatedInAllReactions(
		AcSpeciesAssociationType[] assocTypes
	) {
		Set<AcSpecies> allowedSpecies = new HashSet<AcSpecies>();
		for (AcSpeciesAssociationType assocType : assocTypes) {
			allowedSpecies.addAll(getAllowedSpecies(assocType));
		}
		Set<AcSpecies> allReactionAssociatedSpecies = new HashSet<AcSpecies>();
		allReactionAssociatedSpecies.addAll(allowedSpecies);
		for (AcReaction reaction : reactions) {
			for (AcSpeciesAssociationType assocType : assocTypes) {
				allReactionAssociatedSpecies.retainAll(getAssociatedSpecies(reaction, assocType));
			}
		}
		allowedSpecies.removeAll(allReactionAssociatedSpecies);
		return allowedSpecies;
	}

	private Set<AcReaction> getAssociatedReactions(
		AcSpecies selectedSpecies,
		AcSpeciesAssociationType assocType
	) {
		return speciesAssocReactionMap.get(new AcSpeciesAssociation(selectedSpecies, assocType));
	}

	private int getAssociatedReactionsNum(
		AcSpecies selectedSpecies,
		AcSpeciesAssociationType assocType
	) {
		return getAssociatedReactions(selectedSpecies, assocType).size();
	}

	private Set<AcReaction> getNonAssociatedReactions(
		AcSpecies selectedSpecies,
		AcSpeciesAssociationType assocType
	) {
		return getNonAssociatedReactions(selectedSpecies, assocType, reactions);
	}

	private Set<AcReaction> getNonAssociatedReactions(
		AcSpecies selectedSpecies,
		AcSpeciesAssociationType[] assocTypes
	) {
		Set<AcReaction> nonAssociatedReactions = new HashSet<AcReaction>();
		nonAssociatedReactions.addAll(reactions);
		for (AcSpeciesAssociationType assocType : assocTypes) {
			nonAssociatedReactions.removeAll(getAssociatedReactions(selectedSpecies, assocType));
		}
		return nonAssociatedReactions;
	}

	private Set<AcReaction> getNonAssociatedReactions(
		AcSpecies selectedSpecies,
		AcSpeciesAssociationType assocType,
		Collection<AcReaction> allowedReactions
	) {
		Set<AcReaction> nonAssociatedReactions = new HashSet<AcReaction>();
		nonAssociatedReactions.addAll(allowedReactions);
		nonAssociatedReactions.removeAll(getAssociatedReactions(selectedSpecies, assocType));
		return nonAssociatedReactions;
	}

	private Collection<AcSpecies> getAllowedSpecies(AcSpeciesAssociationType assocType) {
		if (assocTypeAllowedSpeciesMap == null) {
			return species;
		}
		final Collection<AcSpecies> allowedSpecies = assocTypeAllowedSpeciesMap.get(assocType); 
		if (allowedSpecies == null) {
			return species;
		}
		return allowedSpecies;
	}

	private void createSpeciesAssoc(
		AcReaction reaction,
		AcSpecies selectedSpecies,
		AcSpeciesAssociationType assocType
	) {
		AcSpeciesReactionAssociation speciesReactionAssoc = new AcSpeciesReactionAssociation(selectedSpecies, assocType);
		reaction.addSpeciesAssociation(speciesReactionAssoc);
		speciesAssocReactionMap.get(speciesReactionAssoc.getSpeciesAssociation()).add(reaction);
	}

	private void createSpeciesAssocs(
		AcReaction reaction,
		Collection<AcSpecies> selectedSpecies,
		AcSpeciesAssociationType assocType
	) {
		for (AcSpecies oneSpecies : selectedSpecies) {
			createSpeciesAssoc(reaction, oneSpecies, assocType);
		}
	}
}