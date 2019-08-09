package com.bnd.chemistry.business.factory;

import java.util.*;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.business.AcRateConstantUtil;
import com.bnd.chemistry.business.ArtificialChemistryUtil;
import com.bnd.chemistry.domain.*;
import com.bnd.chemistry.domain.AcReaction.ReactionDirection;
import com.bnd.core.RandomOrderComparator;
import com.bnd.core.util.ObjectUtil;
import com.bnd.core.util.RandomUtil;
import com.bnd.math.business.rand.RandomDistributionProvider;
import com.bnd.math.business.rand.RandomDistributionProviderFactory;
import com.bnd.math.domain.rand.RandomDistribution;

public class AcReactionSetFactory {

	private static AcReactionSetFactory instance = new AcReactionSetFactory();
	private ArtificialChemistryUtil acUtil = ArtificialChemistryUtil.getInstance();
	private AcRateConstantUtil acRateUtil = AcRateConstantUtil.getInstance();

	private AcReactionSetFactory() {
		// no-op
	}

	public static AcReactionSetFactory getInstance() {
		return instance;
	}

	public AcReactionSet createDNAStrandRS(
		AcDNAStrandSpec spec,
		AcDNAStrandSpeciesSet speciesSet
	) {
		AcReactionSet reactionSet = new AcReactionSet();
		reactionSet.setSpeciesSet(speciesSet);

		for (AcCompositeSpecies fullDoubleStrand : speciesSet.getFullDoubleStrands()) {
			final AcSpecies upperStrand = fullDoubleStrand.getComponents().get(0);
			final AcSpecies lowerStrand = fullDoubleStrand.getComponents().get(1);

			// reaction type 1
			final AcReaction fullDoubleStrandFormation = createTwoToOneReaction(
					"F_(" + upperStrand.getLabel() + " + " + lowerStrand.getLabel() + ")",
					upperStrand, lowerStrand, fullDoubleStrand);
			reactionSet.addReaction(fullDoubleStrandFormation);
		}

		for (AcCompositeSpecies partialDoubleStrand : speciesSet.getPartialDoubleStrands()) {
			final AcSpecies upperStrand = partialDoubleStrand.getComponents().get(0);
			final AcSpecies lowerStrand = partialDoubleStrand.getComponents().get(1);

			// reaction type 2
			final AcReaction partialDoubleStrandFormation = createTwoToOneReaction(
					"P_(" + upperStrand.getLabel() + " + " + lowerStrand.getLabel() + ")",
					upperStrand, lowerStrand, partialDoubleStrand);
			reactionSet.addReaction(partialDoubleStrandFormation);
		}

		final Map<AcSpecies, List<AcCompositeSpecies>> upperStrandFullDoubleStrandsMap = 
				createSingleToCompositesMap(
						speciesSet.getUpperStrands(),
						speciesSet.getFullDoubleStrands(),
						0);

		final Map<AcSpecies, List<AcCompositeSpecies>> upperStrandPartialDoubleStrandsMap = 
				createSingleToCompositesMap(
						speciesSet.getUpperStrands(),
						speciesSet.getPartialDoubleStrands(),
						0);

		final Map<AcSpecies, List<AcCompositeSpecies>> lowerStrandFullDoubleStrandsMap = 
				createSingleToCompositesMap(
						speciesSet.getLowerStrands(),
						speciesSet.getFullDoubleStrands(),
						1);

		final Map<AcSpecies, List<AcCompositeSpecies>> lowerStrandPartialDoubleStrandsMap = 
				createSingleToCompositesMap(
						speciesSet.getLowerStrands(),
						speciesSet.getPartialDoubleStrands(),
						1);

		if (spec.isUseGlobalOrder()) {
			final Comparator<AcCompositeSpecies> globalPartialDoubleStrandComparator = new RandomOrderComparator<AcCompositeSpecies>(
				speciesSet.getPartialDoubleStrands());

			addStrandDisplacementsForGlobalOrdering(
				reactionSet,
				speciesSet.getUpperStrands(),
				upperStrandFullDoubleStrandsMap,
				upperStrandPartialDoubleStrandsMap,
				1,
				"DL",
				globalPartialDoubleStrandComparator);		

			addStrandDisplacementsForGlobalOrdering(
				reactionSet,
				speciesSet.getLowerStrands(),
				lowerStrandFullDoubleStrandsMap,
				lowerStrandPartialDoubleStrandsMap,
				0,
				"DU",
				globalPartialDoubleStrandComparator);

		} else {
			addStrandDisplacementsForLocalOrdering(
				reactionSet,
				speciesSet.getUpperStrands(),
				upperStrandFullDoubleStrandsMap,
				upperStrandPartialDoubleStrandsMap,
				1,
				"DL",
				speciesSet);		

			addStrandDisplacementsForLocalOrdering(
				reactionSet,
				speciesSet.getLowerStrands(),
				lowerStrandFullDoubleStrandsMap,
				lowerStrandPartialDoubleStrandsMap,
				0,
				"DU",
				speciesSet);			
		}

		handleFluxesAndRateConstants(reactionSet, spec);
		return reactionSet;
	}

	private void addStrandDisplacementsForGlobalOrdering(
		AcReactionSet reactionSet,
		Collection<AcSpecies> singleStrands,
		Map<AcSpecies, List<AcCompositeSpecies>> fullDoubleStrandsMap,
		Map<AcSpecies, List<AcCompositeSpecies>> partialDoubleStrandsMap,
		int oppositeSingleStrandIndex,
		String reactionLabelPrefix,
		Comparator<AcCompositeSpecies> partialDoubleStrandComparator
	) {
		for (final AcSpecies singleStrand : singleStrands) {
			addStrandDisplacements(
				reactionSet,
				singleStrand,
				fullDoubleStrandsMap,
				partialDoubleStrandsMap,
				partialDoubleStrandComparator,
				oppositeSingleStrandIndex,
				reactionLabelPrefix);
		}
	}

	private void addStrandDisplacementsForLocalOrdering(
		AcReactionSet reactionSet,
		Collection<AcSpecies> singleStrands,
		Map<AcSpecies, List<AcCompositeSpecies>> fullDoubleStrandsMap,
		Map<AcSpecies, List<AcCompositeSpecies>> partialDoubleStrandsMap,
		int oppositeSingleStrandIndex,
		String reactionLabelPrefix,
		AcDNAStrandSpeciesSet speciesSet
	) {
		for (final AcSpecies singleStrand : singleStrands) {
			final Comparator<AcCompositeSpecies> partialDoubleStrandComparator = new RandomOrderComparator<AcCompositeSpecies>(
					speciesSet.getPartialDoubleStrands());

			addStrandDisplacements(
				reactionSet,
				singleStrand,
				fullDoubleStrandsMap,
				partialDoubleStrandsMap,
				partialDoubleStrandComparator,
				oppositeSingleStrandIndex,
				reactionLabelPrefix);
		}
	}

	private void addStrandDisplacements(
		AcReactionSet reactionSet,
		AcSpecies singleStrand,
		Map<AcSpecies, List<AcCompositeSpecies>> fullDoubleStrandsMap,
		Map<AcSpecies, List<AcCompositeSpecies>> partialDoubleStrandsMap,
		Comparator<AcCompositeSpecies> partialDoubleStrandComparator,
		int oppositeSingleStrandIndex,
		String reactionLabelPrefix
	) {
		final Collection<AcCompositeSpecies> fullDoubleStrands = fullDoubleStrandsMap
				.get(singleStrand);
		final AcCompositeSpecies fullDoubleStrand = (fullDoubleStrands != null && !fullDoubleStrands.isEmpty()) ? ObjectUtil.getFirst(fullDoubleStrands) : null;
		final AcSpecies complementStrand = (fullDoubleStrand != null) ? fullDoubleStrand.getComponents().get(oppositeSingleStrandIndex) : null;

		final List<AcCompositeSpecies> partialDoubleStrands = partialDoubleStrandsMap.get(singleStrand);
		Collections.sort(partialDoubleStrands, partialDoubleStrandComparator);

		int overallPartialIndex = 1;
		for (int index = 0; index < partialDoubleStrands.size(); index++) {
			final AcCompositeSpecies weakerPartialDoubleStrand = partialDoubleStrands.get(index);
			final AcSpecies weakerSingleStrand = weakerPartialDoubleStrand.getComponents().get(oppositeSingleStrandIndex);
			for (int index2 = index + 1; index2 < partialDoubleStrands.size(); index2++) {
				final AcCompositeSpecies strongerPartialDoubleStrand = partialDoubleStrands.get(index2);
				final AcSpecies strongerSingleStrand = strongerPartialDoubleStrand.getComponents().get(oppositeSingleStrandIndex);

				// reaction type 2 & 5
				final AcReaction partialDisplacement = createTwoToTwoReaction(reactionLabelPrefix
						+ "P_(" + singleStrand.getLabel() + " [" + overallPartialIndex + "])",
						strongerSingleStrand, weakerPartialDoubleStrand, weakerSingleStrand,
						strongerPartialDoubleStrand);
				reactionSet.addReaction(partialDisplacement);
				overallPartialIndex++;
			}

			if (complementStrand != null) {
				// reaction type 3 & 4
				final AcReaction fullDisplacement = createTwoToTwoReaction(reactionLabelPrefix
						+ "F_(" + singleStrand.getLabel() + " [" + (index + 1) + "])",
						complementStrand, weakerPartialDoubleStrand, weakerSingleStrand,
						fullDoubleStrand);
				reactionSet.addReaction(fullDisplacement);
			}
		}
	}

	private Map<AcSpecies, List<AcCompositeSpecies>> createSingleToCompositesMap(
		Collection<AcSpecies> singleSpecies,
		Collection<AcCompositeSpecies> compositeSpecies,
		Integer singleSpeciesInCompositeIndex
	) {
		Map<AcSpecies, List<AcCompositeSpecies>> singleSpeciesToCompositesMap = new HashMap<AcSpecies, List<AcCompositeSpecies>>();
		// initialize map
		for (AcSpecies oneSingleSpecies : singleSpecies) {
			singleSpeciesToCompositesMap.put(oneSingleSpecies, new ArrayList<AcCompositeSpecies>());
//			System.out.println(oneSingleSpecies.getLabel());
		}
		for (AcCompositeSpecies oneCompositeSpecies : compositeSpecies) {
			final AcSpecies oneSingleSpecies = oneCompositeSpecies.getComponents().get(singleSpeciesInCompositeIndex);
			List<AcCompositeSpecies> composites = singleSpeciesToCompositesMap.get(oneSingleSpecies);
			composites.add(oneCompositeSpecies);
		}
		return singleSpeciesToCompositesMap;
	}

	private AcReaction createTwoToOneReaction(
		String label,
		AcSpecies reactant1,
		AcSpecies reactant2,
		AcSpecies product
	) {
		AcReaction reaction = new AcReaction();
		reaction.setLabel(label);
		reaction.addAssociationForSpecies(reactant1, AcSpeciesAssociationType.Reactant);
		reaction.addAssociationForSpecies(reactant2, AcSpeciesAssociationType.Reactant);
		reaction.addAssociationForSpecies(product, AcSpeciesAssociationType.Product);
		return reaction;
	}

	private AcReaction createTwoToTwoReaction(
		String label,
		AcSpecies reactant1,
		AcSpecies reactant2,
		AcSpecies product1,
		AcSpecies product2
	) {
		AcReaction reaction = new AcReaction();
		reaction.setLabel(label);
		reaction.addAssociationForSpecies(reactant1, AcSpeciesAssociationType.Reactant);
		reaction.addAssociationForSpecies(reactant2, AcSpeciesAssociationType.Reactant);
		reaction.addAssociationForSpecies(product1, AcSpeciesAssociationType.Product);
		reaction.addAssociationForSpecies(product2, AcSpeciesAssociationType.Product);
		return reaction;
	}

	public AcReactionSet createComplexRS(
		Collection<AcSpecies> species,
		AcReactionSetConstraints reactionSetConstraints,
		AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy
	) {
		validateRSConstraints(reactionSetConstraints);
		int reactionsNum = reactionSetConstraints.getReactionsNum(species.size(), null);
		AcReactionSet reactionSet = createBlankReactions(reactionsNum);
		Collection<AcReaction> reactions = reactionSet.getReactions();
		AcReactionSpeciesAssociationBuilder speciesAssocsBuilder = new AcReactionSpeciesAssociationBuilder(reactions, species, speciesForbiddenRedundancy);
		linkReactionsToSpeciesAndViceVersa(speciesAssocsBuilder, reactionSetConstraints);
		setRandomRateConstants(reactionSet);
		return reactionSet;
	}

	public AcReactionSet createComplexRS(
		Collection<AcSpecies> allowedReactantSpecies,
		Collection<AcSpecies> allowedProductSpecies,
		Collection<AcSpecies> allowedCatalystSpecies,
		Collection<AcSpecies> allowedInhibitorSpecies,
		AcReactionSetConstraints reactionSetConstraints,
		AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy
	) {
		validateRSConstraints(reactionSetConstraints);
		Map<AcSpeciesAssociationType, Collection<AcSpecies>> assocTypeAllowedSpeciesMap = new HashMap<AcSpeciesAssociationType, Collection<AcSpecies>>();
		if (allowedReactantSpecies != null) {
			assocTypeAllowedSpeciesMap.put(AcSpeciesAssociationType.Reactant, allowedReactantSpecies);
		}
		if (allowedProductSpecies != null) {
			assocTypeAllowedSpeciesMap.put(AcSpeciesAssociationType.Product, allowedProductSpecies);
		}
		if (allowedCatalystSpecies != null) {
			assocTypeAllowedSpeciesMap.put(AcSpeciesAssociationType.Catalyst, allowedCatalystSpecies);
		}
		if (allowedInhibitorSpecies != null) {
			assocTypeAllowedSpeciesMap.put(AcSpeciesAssociationType.Inhibitor, allowedInhibitorSpecies);
		}
		return createComplexRS(assocTypeAllowedSpeciesMap, reactionSetConstraints, speciesForbiddenRedundancy);
	}

	public AcReactionSet createComplexRS(
		Map<AcSpeciesAssociationType, Collection<AcSpecies>> assocTypeAllowedSpeciesMap, 
		AcReactionSetConstraints reactionSetConstraints,
		AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy
	) {
		validateRSConstraints(reactionSetConstraints);
		Set<AcSpecies> species = new HashSet<AcSpecies>();
		for (Collection<AcSpecies> allowedSpecies : assocTypeAllowedSpeciesMap.values()) {
			if (allowedSpecies != null) {
				species.addAll(allowedSpecies);
			}
		}
		int reactionsNum = getReactionsNum(reactionSetConstraints, assocTypeAllowedSpeciesMap, species.size());
		AcReactionSet reactionSet = createBlankReactions(reactionsNum);
		Collection<AcReaction> reactions = reactionSet.getReactions();
		// create species associations builder and link reactions with species
		AcReactionSpeciesAssociationBuilder speciesAssocsBuilder = new AcReactionSpeciesAssociationBuilder(reactions, species, assocTypeAllowedSpeciesMap, speciesForbiddenRedundancy);
		linkReactionsToSpeciesAndViceVersa(speciesAssocsBuilder, reactionSetConstraints);
		setRandomRateConstants(reactionSet);
		return reactionSet;
	}

	private void linkReactionsToSpeciesAndViceVersa(
		AcReactionSpeciesAssociationBuilder speciesAssocsMaker,
		AcReactionSetConstraints reactionSetConstraints
	) {
		final AcReactionToSpeciesConstraints reactionToSpeciesConstraints = reactionSetConstraints.getReactionToSpeciesConstraints();
		final AcSpeciesToReactionConstraints speciesToReactionConstraints = reactionSetConstraints.getSpeciesToReactionConstraints();
		// First satisfy min. species per reaction requirement
		for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
			speciesAssocsMaker.linkReactionsToSpecies(
					reactionToSpeciesConstraints.getMinSpeciesAssocsNum(assocType),
					speciesToReactionConstraints.getFixedAssocsNum(assocType),
					assocType);			
		}
		// now add associations based on species constraints
		for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
			speciesAssocsMaker.linkSpeciesToReactions(
					reactionToSpeciesConstraints.getMaxSpeciesAssocsNum(assocType),
					speciesToReactionConstraints.getFixedAssocsNum(assocType),
					assocType);			
		}
	}

	public AcReactionSet createRandomRS(
		int reactionsNumber,
		int reactantsNumber,
		int productsNumber,
		int catalystsNumber,
		int inhibitorsNumber,
		AcSpeciesSet speciesSet,
		AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy
	) {
		AcReactionSet reactionSet = createBlankReactions(reactionsNumber);
		reactionSet.setSpeciesSet(speciesSet);
		Collection<AcReaction> reactions = reactionSet.getReactions();
		Collection<AcSpecies> species = speciesSet.getVariables();
		// Add species associations of all four types
		AcReactionSpeciesAssociationBuilder speciesAssocsBuilder = new AcReactionSpeciesAssociationBuilder(reactions, species, speciesForbiddenRedundancy);
		speciesAssocsBuilder.linkReactionAndSpecies(reactantsNumber, AcSpeciesAssociationType.Reactant);
		speciesAssocsBuilder.linkReactionAndSpecies(productsNumber, AcSpeciesAssociationType.Product);
		speciesAssocsBuilder.linkReactionAndSpecies(catalystsNumber, AcSpeciesAssociationType.Catalyst);
		speciesAssocsBuilder.linkReactionAndSpecies(inhibitorsNumber, AcSpeciesAssociationType.Inhibitor);
		setRandomRateConstants(reactionSet);
		return reactionSet;
	}

	public AcReactionSet createRandomReactionSymmetricRS(
		AcSymmetricSpec spec,
		AcSpeciesSet speciesSet
	) {
		AcReactionSet reactionSet = createBlankReactions(spec.getReactionNum());
		reactionSet.setSpeciesSet(speciesSet);
		final Collection<AcReaction> reactions = reactionSet.getReactions();
		final Collection<AcSpecies> species = speciesSet.getVariables();
		// Add species associations of all four types
		AcReactionSpeciesAssociationBuilder speciesAssocsMaker = new AcReactionSpeciesAssociationBuilder(reactions, species, spec.getSpeciesForbiddenRedundancy());
		for (AcReaction reaction : reactions) {
			speciesAssocsMaker.linkReactionToSpecies(reaction, spec.getReactantsPerReactionNumber(), AcSpeciesAssociationType.Reactant);
			speciesAssocsMaker.linkReactionToSpecies(reaction, spec.getProductsPerReactionNumber(), AcSpeciesAssociationType.Product);
			speciesAssocsMaker.linkReactionToSpecies(reaction, spec.getCatalystsPerReactionNumber(), AcSpeciesAssociationType.Catalyst);
			speciesAssocsMaker.linkReactionToSpecies(reaction, spec.getInhibitorsPerReactionNumber(), AcSpeciesAssociationType.Inhibitor);
		}

		handleFluxesAndRateConstants(reactionSet, spec);
		return reactionSet;
	}

	private void handleFluxesAndRateConstants(AcReactionSet reactionSet, ArtificialChemistrySpec spec) {
		// Regular rate constants
		setRateConstants(reactionSet.getReactions(), spec.getRateConstantDistribution());

		// Influces with rate constants
		Collection<AcReaction> influces  = 
				createSingleSpeciesReactionsWithRatio(reactionSet, reactionSet.getSpecies(), spec.getInfluxRatio(), AcSpeciesAssociationType.Product, "Influx");
		reactionSet.addReactions(influces);
		setRateConstants(influces, spec.getInfluxRateConstantDistribution());

		// Non reactive species outfluces with rate constants
		Set<AcSpecies> availableOutfluxSpecies = new HashSet<AcSpecies>(reactionSet.getSpecies());
		if (spec.hasOutfluxNonReactiveRateConstantDistribution()) {
			final Collection<AcSpecies> nonReactiveSpecies = acUtil.filterOutSpecies(
					reactionSet.getReactions(), reactionSet.getSpecies(), AcSpeciesAssociationType.Reactant);
			availableOutfluxSpecies.removeAll(nonReactiveSpecies);
			final Collection<AcReaction> nonReactiveSpeciesOutfluces = 
					createSingleSpeciesReactions(reactionSet, nonReactiveSpecies, AcSpeciesAssociationType.Reactant, "P Outflux");
			reactionSet.addReactions(nonReactiveSpeciesOutfluces);
			setRateConstants(nonReactiveSpeciesOutfluces, spec.getOutfluxNonReactiveRateConstantDistribution());
		}

		// Outfluces with rate constants
		Collection<AcReaction> outfluces = new ArrayList<AcReaction>();
		if (spec.isOutfluxAll()) {
			outfluces = createSingleSpeciesReactions(reactionSet, availableOutfluxSpecies, AcSpeciesAssociationType.Reactant, "Outflux");			
		} else {
			outfluces = createSingleSpeciesReactionsWithRatio(reactionSet, availableOutfluxSpecies, spec.getOutfluxRatio(), AcSpeciesAssociationType.Reactant, "Outflux");			
		}
		reactionSet.addReactions(outfluces);
		setRateConstants(outfluces, spec.getOutfluxRateConstantDistribution());
	}

	private Collection<AcReaction> createSingleSpeciesReactionsWithRatio(
		AcReactionSet reactionSet,
		Collection<AcSpecies> availableSpecies,
		Double ratio,
		AcSpeciesAssociationType type,
		String labelPrefix
	) {
		if (ratio == null) {
			return new ArrayList<AcReaction>();
		}
		int numToSelect = (int) Math.round(reactionSet.getSpecies().size() * ratio);
		if (numToSelect > availableSpecies.size()) {
			numToSelect = availableSpecies.size();
		}
		return createSingleSpeciesReactions(reactionSet, availableSpecies, numToSelect, type, labelPrefix);
	}

	private Collection<AcReaction> createSingleSpeciesReactions(
		AcReactionSet reactionSet,
		Collection<AcSpecies> availableSpecies,
		Integer num,
		AcSpeciesAssociationType type,
		String labelPrefix
	) { 
		if (num == null) {
			return new ArrayList<AcReaction>();
		}
		Collection<AcSpecies> selectedSpecies = RandomUtil.nextElementsWithoutRepetitions(availableSpecies, num);
		return createSingleSpeciesReactions(reactionSet, selectedSpecies, type, labelPrefix);
	}

	private Collection<AcReaction> createSingleSpeciesReactions(
		AcReactionSet reactionSet,
		Collection<AcSpecies> selectedSpecies,
		AcSpeciesAssociationType type,
		String labelPrefix
	) {
		Collection<AcReaction> singleSpeciesReactions = new ArrayList<AcReaction>();
		if (selectedSpecies == null || selectedSpecies.isEmpty()) {
			return singleSpeciesReactions;
		}

		int index = 0;
		for (AcSpecies oneSpecies : selectedSpecies) {
			AcReaction singleSpeciesReaction = new AcReaction();
			singleSpeciesReaction.setLabel(labelPrefix + " " + index);
			singleSpeciesReaction.addAssociationForSpecies(oneSpecies, type);
			singleSpeciesReactions.add(singleSpeciesReaction);
			index++;
		}
		return singleSpeciesReactions;
	}

	public AcReactionSet createOutputFeedbackRS(AcSpeciesSet speciesSet) {
		if (speciesSet.getSpeciesNumber(AcSpeciesType.Feedback) != 2 || speciesSet.getSpeciesNumber(AcSpeciesType.Output) != 2) {
			throw new BndChemistryException("The number of feedback and output species must be two and two.");
		}
		int weightSpeciesNum = speciesSet.getSpeciesNumber(AcSpeciesType.Functional);
		if (weightSpeciesNum == 0) {
			throw new BndChemistryException("The number of weight species must be positive.");
		}
		if (weightSpeciesNum % 2 != 0) {
			throw new BndChemistryException("The number of weight species must be even.");
		}
		Collection<AcSpecies> outputSpecies = speciesSet.getSpecies(AcSpeciesType.Output);
		Collection<AcSpecies> feedbackSpecies = speciesSet.getSpecies(AcSpeciesType.Feedback);
		Collection<AcSpecies> inputSpecies = speciesSet.getSpecies(AcSpeciesType.Input);
		AcSpecies outputSpecies0 = ObjectUtil.getFirst(outputSpecies);
		AcSpecies outputSpecies1 = ObjectUtil.getSecond(outputSpecies);
		AcSpecies feedbackSpecies0 = ObjectUtil.getFirst(feedbackSpecies);
		AcSpecies feedbackSpecies1 = ObjectUtil.getSecond(feedbackSpecies);
//		AcSpecies inputSpecies0 = ObjectUtil.getFirst(inputSpecies);
//		AcSpecies inputSpecies1 = ObjectUtil.getSecond(inputSpecies);
		
		Collection<AcSpecies> selectedInternalSpecies = RandomUtil.nextElementsWithoutRepetitions(
				speciesSet.getInternalSpecies(), weightSpeciesNum / 2);
		AcReactionSet reactionSet = new AcReactionSet();
		Iterator<AcSpecies> weightSpeciesIterator = speciesSet.getSpecies(AcSpeciesType.Functional).iterator();
		int index = 0;
		for (AcSpecies reactant : selectedInternalSpecies) {
			AcSpecies weightSpecies0 = weightSpeciesIterator.next();
			AcSpecies weightSpecies1 = weightSpeciesIterator.next();

			AcReaction outputReaction0 = new AcReaction();
			outputReaction0.setLabel("Ro" + index + "-");
			outputReaction0.addAssociationForSpecies(reactant, AcSpeciesAssociationType.Reactant);
			outputReaction0.addAssociationForSpecies(outputSpecies0, AcSpeciesAssociationType.Product);
			outputReaction0.addAssociationForSpecies(weightSpecies0, AcSpeciesAssociationType.Catalyst);
			reactionSet.addReaction(outputReaction0);

			AcReaction outputReaction1 = new AcReaction();
			outputReaction1.setLabel("Ro" + index + "+");
			outputReaction1.addAssociationForSpecies(reactant, AcSpeciesAssociationType.Reactant);
			outputReaction1.addAssociationForSpecies(outputSpecies1, AcSpeciesAssociationType.Product);
			outputReaction1.addAssociationForSpecies(weightSpecies1, AcSpeciesAssociationType.Catalyst);
			reactionSet.addReaction(outputReaction1);

			AcReaction weightReaction0 = new AcReaction();
			weightReaction0.setLabel("Rw" + index + "-");
			weightReaction0.addAssociationForSpecies(feedbackSpecies0, AcSpeciesAssociationType.Reactant);
			weightReaction0.addAssociationForSpecies(weightSpecies0, AcSpeciesAssociationType.Product);
			weightReaction0.addAssociationForSpecies(reactant, AcSpeciesAssociationType.Catalyst);
			weightReaction0.addAssociationForSpecies(outputSpecies1, AcSpeciesAssociationType.Catalyst);
			weightReaction0.setCollectiveCatalysisType(AcCollectiveSpeciesReactionAssociationType.AND);
//			weightReaction0.addSpeciesAssociation(inputSpecies0, AcSpeciesAssociationType.Inhibitor);
//			weightReaction0.addSpeciesAssociation(inputSpecies1, AcSpeciesAssociationType.Inhibitor);
			reactionSet.addReaction(weightReaction0);

			AcReaction weightReaction1 = new AcReaction();
			weightReaction1.setLabel("Rw" + index + "-");
			weightReaction1.addAssociationForSpecies(feedbackSpecies1, AcSpeciesAssociationType.Reactant);
			weightReaction1.addAssociationForSpecies(weightSpecies1, AcSpeciesAssociationType.Product);
			weightReaction1.addAssociationForSpecies(reactant, AcSpeciesAssociationType.Catalyst);
			weightReaction1.addAssociationForSpecies(outputSpecies0, AcSpeciesAssociationType.Catalyst);
			weightReaction1.setCollectiveCatalysisType(AcCollectiveSpeciesReactionAssociationType.AND);
//			weightReaction1.addSpeciesAssociation(inputSpecies0, AcSpeciesAssociationType.Inhibitor);
//			weightReaction1.addSpeciesAssociation(inputSpecies1, AcSpeciesAssociationType.Inhibitor);
			reactionSet.addReaction(weightReaction1);

			AcReaction weightAnihReaction = new AcReaction();
			weightAnihReaction.setLabel("Rwa" + index);
			weightAnihReaction.addAssociationForSpecies(weightSpecies0, AcSpeciesAssociationType.Reactant);
			weightAnihReaction.addAssociationForSpecies(weightSpecies1, AcSpeciesAssociationType.Reactant);
			weightAnihReaction.setForwardRateConstants(new Double[] {0.4});
			reactionSet.addReaction(weightAnihReaction);
		}
		setRandomRateConstants(reactionSet);
		return reactionSet;
	}

	private AcReactionSet createBlankReactions(int reactionsNumber) {
		AcReactionSet reactionSet = new AcReactionSet();
		for (int reactionLabelIndex = 0; reactionLabelIndex < reactionsNumber; reactionLabelIndex++) {
			AcReaction reaction = new AcReaction();
			reaction.setLabel("R " + reactionLabelIndex);
//			reaction.setIndex(index);
			reactionSet.addReaction(reaction);
		}
		return reactionSet;
	}

	public AcReactionSet mergeReactionSets(final AcReactionSet[] reactionSets) {
		AcReactionSet mergedRS = new AcReactionSet();
		int reactionLabelIndex = 0;
		for (AcReactionSet reactionSet : reactionSets) {
			for (AcReaction reaction : reactionSet.getReactions()) {
				reaction.setLabel("R " + reactionLabelIndex);
				mergedRS.addReaction(reaction);
				reactionLabelIndex++;
			}
		}
		return mergedRS;
	}

	private void validateRSConstraints(AcReactionSetConstraints rsConstraints) {
		final AcReactionToSpeciesConstraints rToSConstraints = rsConstraints.getReactionToSpeciesConstraints();
		final AcSpeciesToReactionConstraints sToRConstraints = rsConstraints.getSpeciesToReactionConstraints();
		for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
			final Integer fixedAssocsNumPerSpecies = sToRConstraints.getFixedAssocsNum(assocType);
			if (fixedAssocsNumPerSpecies != null) {
				final Integer minAssocsNumPerReaction = rToSConstraints.getMinSpeciesAssocsNum(assocType);
				final Integer maxAssocsNumPerReaction = rToSConstraints.getMaxSpeciesAssocsNum(assocType);
				if (maxAssocsNumPerReaction != null) {
					if (fixedAssocsNumPerSpecies > maxAssocsNumPerReaction * rsConstraints.getReactionsPerSpeciesRatio()) {
						throw new BndChemistryException("The fixed number of assocs. per species '" + fixedAssocsNumPerSpecies + "' of type '" + assocType + "' cannot be greater than the max. number of assocs. per reaction '" + maxAssocsNumPerReaction + "' times the reactions/species ratio '" + rsConstraints.getReactionsPerSpeciesRatio() + "'.");
					}
				}
				if (minAssocsNumPerReaction != null) {
					if (fixedAssocsNumPerSpecies < minAssocsNumPerReaction * rsConstraints.getReactionsPerSpeciesRatio()) {
						throw new BndChemistryException("The fixed number of assocs. per species '" + fixedAssocsNumPerSpecies + "' of type '" + assocType + "' cannot be smaller than the min. number of assocs. per reaction '" + maxAssocsNumPerReaction + "' times the reactions/species ratio '" + rsConstraints.getReactionsPerSpeciesRatio() + "'.");
					}
				}
			}
		}		
	}

	private void setRandomRateConstants(AcReactionSet reactionSet) {
		acRateUtil.setRandomRateConstantsIfNotSet(reactionSet, null, ReactionDirection.Both);
	}

	private void setRateConstants(
		Collection<AcReaction> reactions,
		RandomDistribution<Double> randomDistribution
	) {
		if (randomDistribution == null) {
			return;
		}
		RandomDistributionProvider<Double> distributionProvider = RandomDistributionProviderFactory.apply(randomDistribution);
		for (AcReaction reaction : reactions) {
			if (reaction.hasSpeciesAssociations(AcSpeciesAssociationType.Catalyst)) {
				reaction.setForwardRateConstants(distributionProvider.nextList(2).toArray(new Double[0]));
			} else {
				reaction.setForwardRateConstant(distributionProvider.next());
			}
		}
	}
	
	static int getReactionsNum(
		AcReactionSetConstraints rsConstraints,
		Map<AcSpeciesAssociationType, Collection<AcSpecies>> assocTypeAllowedSpeciesMap,
		int totalSpeciesNum
	) {
		AcSpeciesAssociationType reactionsNumRestrictionType = rsConstraints.getReactionsPerSpeciesRatioAssocType();
		int reactionsNum = 0;
		if (reactionsNumRestrictionType == null) {
			reactionsNum = rsConstraints.getReactionsNum(totalSpeciesNum, null);
		} else {
			Collection<AcSpecies> assocTypeAllowedSpecies = assocTypeAllowedSpeciesMap.get(reactionsNumRestrictionType);
			if (assocTypeAllowedSpecies == null) {
				throw new BndChemistryException("Reactions num specifed for assoc type '" + reactionsNumRestrictionType + "', but no species of that type provided.");
			}
			reactionsNum = rsConstraints.getReactionsNum(assocTypeAllowedSpecies.size(), reactionsNumRestrictionType);
		}
		return reactionsNum;
	}
}