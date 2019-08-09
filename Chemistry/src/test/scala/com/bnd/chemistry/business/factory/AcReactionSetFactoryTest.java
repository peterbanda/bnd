package com.bnd.chemistry.business.factory;

import java.util.*;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.bnd.chemistry.business.ArtificialChemistryUtil;
import com.bnd.chemistry.business.factory.AcReactionSetFactoryTestDataGenerator.AcComplexRSAllowedSpeciesData;
import com.bnd.chemistry.business.factory.AcReactionSetFactoryTestDataGenerator.AcComplexRSData;
import com.bnd.chemistry.business.factory.AcReactionSetFactoryTestDataGenerator.AcDNAStrandRSData;
import com.bnd.chemistry.business.factory.AcReactionSetFactoryTestDataGenerator.AcRandomRSData;
import com.bnd.chemistry.business.factory.AcReactionSetFactoryTestDataGenerator.AcRandomReactionSymmetricRSData;
import com.bnd.chemistry.domain.*;
import com.bnd.core.util.ObjectUtil;
import com.bnd.math.domain.rand.RandomDistribution;

public class AcReactionSetFactoryTest extends TestCase {

	private AcReactionSetFactoryTestDataGenerator testDataGenerator = new AcReactionSetFactoryTestDataGenerator();
	private AcReactionSetFactory reactionSetFactory = AcReactionSetFactory.getInstance();
	private ArtificialChemistryUtil acUtil = ArtificialChemistryUtil.getInstance();

	@Test
	public void testDNAStrandRS() {
		for (AcDNAStrandRSData testData : testDataGenerator.getDnaStrandRSData()) {
			AcReactionSet reactionSet = reactionSetFactory.createDNAStrandRS(testData.spec, testData.speciesSet);
			assertNotNull(reactionSet);

			System.out.println(testData.spec.isUseGlobalOrder() ? "Global order" : "Local order");
			System.out.println(acUtil.getDNAStrandSpeciesSetAsString(testData.speciesSet, "\n"));			
			System.out.println(getReactionSetAsString(reactionSet));
		}
	}

	@Test
	public void testCreateComplexRS() {
		for (AcComplexRSData testData : testDataGenerator.getComplexRSData()) {
			AcReactionSet reactionSet = reactionSetFactory.createComplexRS(
					testData.species,
					testData.reactionSetConstraints,
					testData.speciesForbiddenRedundancy);
			assertNotNull(reactionSet);
			checkReactionSetConstraints(reactionSet, testData);
			checkSpeciesRedundancy(reactionSet, testData.speciesForbiddenRedundancy);
		}
	}

	@Test
	public void testCreateComplexConsoleOutputRS() {
		for (AcComplexRSData testData : testDataGenerator.getComplexRSFewSpeciesData()) {
			AcReactionSet reactionSet = reactionSetFactory.createComplexRS(
					testData.species,
					testData.reactionSetConstraints,
					testData.speciesForbiddenRedundancy);
			assertNotNull(reactionSet);
			checkReactionSetConstraints(reactionSet, testData);
			checkSpeciesRedundancy(reactionSet, testData.speciesForbiddenRedundancy);
			System.out.println(testData.reactionSetConstraints);
			System.out.println(getReactionSetAsString(reactionSet));
		}
	}

	@Test
	public void testCreateComplexRSAllowedSpecies() {
		for (AcComplexRSAllowedSpeciesData testData : testDataGenerator.getComplexRSAllowedSpeciesData()) {
			AcReactionSet reactionSet = reactionSetFactory.createComplexRS(
					testData.allowedReactantSpecies,
					testData.allowedProductSpecies,
					testData.allowedCatalystSpecies,
					testData.allowedInhibitorSpecies,
					testData.reactionSetConstraints,
					testData.speciesForbiddenRedundancy);
			assertNotNull(reactionSet);
			checkReactionSetConstraints(reactionSet, testData);
			checkSpeciesRedundancy(reactionSet, testData.speciesForbiddenRedundancy);
		}
	}

	@Test
	public void testCreateComplexConsoleOutputRSAllowedSpecies() {
		for (AcComplexRSAllowedSpeciesData testData : testDataGenerator.getComplexFewSpeciesRSAllowedSpeciesData()) {
			AcReactionSet reactionSet = reactionSetFactory.createComplexRS(
					testData.allowedReactantSpecies,
					testData.allowedProductSpecies,
					testData.allowedCatalystSpecies,
					testData.allowedInhibitorSpecies,
					testData.reactionSetConstraints,
					testData.speciesForbiddenRedundancy);
			assertNotNull(reactionSet);
			checkReactionSetConstraints(reactionSet, testData);
			checkSpeciesRedundancy(reactionSet, testData.speciesForbiddenRedundancy);
			System.out.println(acUtil.getVariableLabelsAsString(testData.allowedReactantSpecies));
			System.out.println(acUtil.getVariableLabelsAsString(testData.allowedProductSpecies));
			System.out.println(acUtil.getVariableLabelsAsString(testData.allowedCatalystSpecies));
			System.out.println(acUtil.getVariableLabelsAsString(testData.allowedInhibitorSpecies));
			System.out.println(testData.reactionSetConstraints);
			System.out.println(getReactionSetAsString(reactionSet));
		}
	}

	@Test
	public void testCreateRandomRS() {
		for (AcRandomRSData testData : testDataGenerator.getRandomRSData()) {
			AcReactionSet reactionSet = reactionSetFactory.createRandomRS(
					testData.reactionsNumber,
					testData.reactantsNumber,
					testData.productsNumber,
					testData.catalystsNumber,
					testData.inhibitorsNumber,
					testData.speciesSet,
					testData.speciesForbiddenRedundancy);
			assertNotNull(reactionSet);
			checkSpeciesRedundancy(reactionSet, testData.speciesForbiddenRedundancy);
			assertEquals(testData.reactionsNumber, reactionSet.getReactionsNum());

			int reactantsNum = 0;
			int productsNum = 0;
			int catalystsNum = 0;
			int inhibitorsNum = 0;
			for (AcReaction reaction : reactionSet.getReactions()) {
				for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
					int assocsNum = reaction.getSpeciesAssociationsNum(assocType);
					switch (assocType) {
						case Reactant: reactantsNum += assocsNum; break; 
						case Product: productsNum += assocsNum; break;
						case Catalyst: catalystsNum += assocsNum; break;
						case Inhibitor: inhibitorsNum += assocsNum; break;
					}
				}
			}
			assertEquals(testData.reactantsNumber, reactantsNum);
			assertEquals(testData.productsNumber, productsNum);
			assertEquals(testData.catalystsNumber, catalystsNum);
			assertEquals(testData.inhibitorsNumber, inhibitorsNum);
		}
	}

	@Test
	public void testCreateRandomReactionSymmetricRS() {
		for (AcRandomReactionSymmetricRSData testData : testDataGenerator.getRandomReactionSymmetricRSData()) {
			AcSymmetricSpec spec = testData.spec;
			AcReactionSet reactionSet = reactionSetFactory.createRandomReactionSymmetricRS(testData.spec, testData.speciesSet);

			assertNotNull(reactionSet);
			checkSpeciesRedundancy(reactionSet, spec.getSpeciesForbiddenRedundancy());
			
			int reactionNum = spec.getReactionNum();
			if (spec.getInfluxRatio() != null) {
				reactionNum += spec.getInfluxRatio();
			}
			if (spec.getOutfluxRatio() != null) {
				reactionNum += spec.getOutfluxRatio();
			}
			assertEquals(reactionNum, reactionSet.getReactionsNum());

			AcReactionToSpeciesConstraints rToSConstraints = new AcReactionToSpeciesConstraints();
			rToSConstraints.setMinReactantsNum(spec.getReactantsPerReactionNumber());
			rToSConstraints.setMaxReactantsNum(spec.getReactantsPerReactionNumber());
			rToSConstraints.setMinProductsNum(spec.getProductsPerReactionNumber());
			rToSConstraints.setMaxProductsNum(spec.getProductsPerReactionNumber());
			rToSConstraints.setMinCatalystsNum(spec.getCatalystsPerReactionNumber());
			rToSConstraints.setMaxCatalystsNum(spec.getCatalystsPerReactionNumber());
			rToSConstraints.setMinInhibitorsNum(spec.getInhibitorsPerReactionNumber());
			rToSConstraints.setMaxInhibitorsNum(spec.getInhibitorsPerReactionNumber());
			
			Collection<AcReaction> filteredReactions = filterSingleSpeciesReactions(reactionSet.getReactions(), AcSpeciesAssociationType.Reactant);
			filteredReactions = filterSingleSpeciesReactions(filteredReactions, AcSpeciesAssociationType.Product);
			checkReactionToSpeciesConstraints(filteredReactions, rToSConstraints);
		}
	}

	@Test
	public void testCreateOutputFeedbackRS() {
		for (AcSpeciesSet speciesSet : testDataGenerator.getOutputFeedbackEnabledSpeciesSets()) {
			AcReactionSet reactionSet = reactionSetFactory.createOutputFeedbackRS(speciesSet);
			assertNotNull(reactionSet);
			assertEquals(5 * speciesSet.getSpeciesNumber(AcSpeciesType.Functional) / 2, reactionSet.getReactionsNum());
			System.out.println("INT: " + acUtil.getVariableLabelsAsString(speciesSet.getInternalSpecies()));
			System.out.println("OUT: " + acUtil.getVariableLabelsAsString(speciesSet.getSpecies(AcSpeciesType.Output)));
			System.out.println("FEE: " + acUtil.getVariableLabelsAsString(speciesSet.getSpecies(AcSpeciesType.Feedback)));
			System.out.println("WEI: " + acUtil.getVariableLabelsAsString(speciesSet.getSpecies(AcSpeciesType.Functional)));
			System.out.println(getReactionSetAsString(reactionSet));
		}
	}

	private void checkSpeciesRedundancy(
		AcReactionSet reactionSet,
		AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy
	) {
		if (speciesForbiddenRedundancy == AcReactionSpeciesForbiddenRedundancy.None) {
			return;
		}
		for (AcReaction reaction : reactionSet.getReactions()) {
			Map<AcSpeciesAssociationType, Set<AcSpecies>> speciesAssociations = new HashMap<AcSpeciesAssociationType, Set<AcSpecies>>();
			for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
				speciesAssociations.put(assocType, new HashSet<AcSpecies>());
			}
			for (AcSpeciesReactionAssociation speciesAssoc : reaction.getSpeciesAssociations()) {
				Set<AcSpecies> typeSpecies = speciesAssociations.get(speciesAssoc.getType());
				if (typeSpecies.contains(speciesAssoc.getSpecies())) {
					assertFalse(typeSpecies.contains(speciesAssoc.getSpecies()));
				}
				typeSpecies.add(speciesAssoc.getSpecies());
			}
			if (speciesForbiddenRedundancy == AcReactionSpeciesForbiddenRedundancy.SameAssocTypeAndReactantProduct) {
				Set<AcSpecies> reactants = speciesAssociations.get(AcSpeciesAssociationType.Reactant);
				Set<AcSpecies> products = speciesAssociations.get(AcSpeciesAssociationType.Product);
				int reactantsNum = reactants.size();
				reactants.removeAll(products);
				if (reactantsNum != reactants.size()) {
					assertEquals(reactantsNum, reactants.size());
				}
			}
			// TODO: full restriction
		}
	}

	private void checkReactionSetConstraints(
		AcReactionSet reactionSet,
		AcComplexRSData complexRSData	
	) {
		final AcReactionSetConstraints rsConstraints = complexRSData.reactionSetConstraints;
		final Collection<AcSpecies> species = complexRSData.species;
		assertEquals((int) rsConstraints.getReactionsNum(species.size(), null), reactionSet.getReactions().size());
		// reactions-to-species
		checkReactionToSpeciesConstraints(reactionSet.getReactions(), rsConstraints.getReactionToSpeciesConstraints());
		// species-to-reactions
		final AcSpeciesToReactionConstraints sToRConstraints = rsConstraints.getSpeciesToReactionConstraints();
		Map<AcSpeciesAssociation, Set<AcReaction>> speciesAssocReactionMap = getSpeciesAssocReactionMap(reactionSet, species);
		for (Entry<AcSpeciesAssociation, Set<AcReaction>> speciesAssocWithReactions : speciesAssocReactionMap.entrySet()) {
			AcSpeciesAssociationType assocType = speciesAssocWithReactions.getKey().getType();
			final Integer fixedAssocsNum = sToRConstraints.getFixedAssocsNum(assocType);
			final RandomDistribution<Double> randomAssocsNumDist = sToRConstraints.getRandomAssocsNumDistribution(assocType);
			if (fixedAssocsNum != null) {
				assertEquals(fixedAssocsNum, new Integer(speciesAssocWithReactions.getValue().size()));
			}
		}
	}

	private void checkReactionSetConstraints(
		AcReactionSet reactionSet,
		AcComplexRSAllowedSpeciesData testData	
	) {
		final AcReactionSetConstraints rsConstraints = testData.reactionSetConstraints;
		Set<AcSpecies> species = new HashSet<AcSpecies>();
		species.addAll(testData.allowedReactantSpecies);
		species.addAll(testData.allowedProductSpecies);
		species.addAll(testData.allowedCatalystSpecies);
		species.addAll(testData.allowedInhibitorSpecies);
		Map<AcSpeciesAssociationType, Collection<AcSpecies>> assocTypeSpecies = new HashMap<AcSpeciesAssociationType, Collection<AcSpecies>>();
		assocTypeSpecies.put(AcSpeciesAssociationType.Reactant, testData.allowedReactantSpecies);
		assocTypeSpecies.put(AcSpeciesAssociationType.Product, testData.allowedProductSpecies);
		assocTypeSpecies.put(AcSpeciesAssociationType.Catalyst, testData.allowedCatalystSpecies);
		assocTypeSpecies.put(AcSpeciesAssociationType.Inhibitor, testData.allowedInhibitorSpecies);

		int reactionsNum = reactionSetFactory.getReactionsNum(rsConstraints, assocTypeSpecies, species.size());
		assertEquals(reactionsNum, reactionSet.getReactions().size());
		// reactions-to-species
		checkReactionToSpeciesConstraints(reactionSet.getReactions(), rsConstraints.getReactionToSpeciesConstraints());
		// species-to-reactions
		final AcSpeciesToReactionConstraints sToRConstraints = rsConstraints.getSpeciesToReactionConstraints();
		Map<AcSpeciesAssociation, Set<AcReaction>> speciesAssocReactionMap = getSpeciesAssocReactionMap(reactionSet, species);
		for (Entry<AcSpeciesAssociation, Set<AcReaction>> speciesAssocWithReactions : speciesAssocReactionMap.entrySet()) {
			final AcSpeciesAssociationType assocType = speciesAssocWithReactions.getKey().getType();
			final AcSpecies assocSpecies = speciesAssocWithReactions.getKey().getSpecies();
			final Integer speciesReactionsNum = speciesAssocWithReactions.getValue().size();
			final Integer fixedAssocsNum = sToRConstraints.getFixedAssocsNum(assocType);
			final RandomDistribution<Double> randomAssocsNumDist = sToRConstraints.getRandomAssocsNumDistribution(assocType);
			if (assocTypeSpecies.get(assocType).contains(assocSpecies)) {
				if (fixedAssocsNum != null) {
					assertEquals(fixedAssocsNum, speciesReactionsNum);
				}
			} else {
				assertEquals(new Integer(0), speciesReactionsNum);
			}
		}
	}

	private Collection<AcReaction> filterSingleSpeciesReactions(
		Collection<AcReaction> reactions,
		AcSpeciesAssociationType type
	) {
		Collection<AcReaction> filteredReactions = new ArrayList<AcReaction>();
		for (AcReaction reaction : reactions) {
			Collection<AcSpeciesReactionAssociation> assocs = reaction.getSpeciesAssociations();
			if (assocs.size() != 1 || ObjectUtil.getFirst(assocs).getType() != type) {
				filteredReactions.add(reaction);
			}
		}
		return filteredReactions;
	}

	private void checkReactionToSpeciesConstraints(
		Collection<AcReaction> reactions,
		AcReactionToSpeciesConstraints rToSConstraints
	) {
		for (AcReaction reaction : reactions) {
			for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
				final Integer assocsNum = reaction.getSpeciesAssociationsNum(assocType);
				final Integer maxAssocsNum = rToSConstraints.getMaxSpeciesAssocsNum(assocType);
				final Integer minAssocsNum = rToSConstraints.getMinSpeciesAssocsNum(assocType);
				if (minAssocsNum != null) {
					if (ObjectUtil.compareObjects(assocsNum, minAssocsNum) < 0) {
						System.out.println("Bang");
					}
					assertTrue("The assocs num '" + assocsNum + "' is lower than defined minimum '" + minAssocsNum + "'.", ObjectUtil.compareObjects(assocsNum, minAssocsNum) >= 0);
				}
				if (maxAssocsNum != null) {
					if (ObjectUtil.compareObjects(assocsNum, maxAssocsNum) > 0) {
						System.out.println("Bang");
					}
					assertTrue("The assocs num '" + assocsNum + "' is greater than defined maximum '" + maxAssocsNum + "'.", ObjectUtil.compareObjects(assocsNum, maxAssocsNum) <= 0);
				}
			}
		}
	}
	
	private String getReactionAsString(AcReaction reaction) {
		Collection<String> reactantsWithStoichiometry = acUtil.getSpeciesLabelsWithStoichiometry(
				reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant));
		Collection<String> productsWithStoichiometry = acUtil.getSpeciesLabelsWithStoichiometry(
				reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product));

		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.join(reactantsWithStoichiometry, "+"));
		sb.append("->");
		sb.append(StringUtils.join(productsWithStoichiometry, "+"));
		return sb.toString();
	}

	private String getReactionSetAsString(AcReactionSet reactionSet) {
		StringBuilder sb = new StringBuilder();
		for (AcReaction reaction : reactionSet.getReactions()) {
			sb.append(reaction.getLabel());
			sb.append(" : ");
			sb.append(getReactionAsString(reaction));
			String catalystsString = StringUtils.join(acUtil.getSpeciesLabels(reaction.getSpeciesAssociations(AcSpeciesAssociationType.Catalyst)), ",");
			if (!catalystsString.isEmpty()) {
				sb.append(", Catalysts: ");
				sb.append(catalystsString);				
			}
			String inhibitorsString = StringUtils.join(acUtil.getSpeciesLabels(reaction.getSpeciesAssociations(AcSpeciesAssociationType.Inhibitor)), ",");
			if (!inhibitorsString.isEmpty()) {
				sb.append(", Inhibitors: ");
				sb.append(inhibitorsString);
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private Map<AcSpeciesAssociation, Set<AcReaction>> getSpeciesAssocReactionMap(
		AcReactionSet reactionSet,
		Collection<AcSpecies> species
	) {
		// initialization
		Map<AcSpeciesAssociation, Set<AcReaction>> speciesAssocReactionMap = new HashMap<AcSpeciesAssociation, Set<AcReaction>>();
		for (AcSpecies oneSpecies : species)
			for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values())
				speciesAssocReactionMap.put(new AcSpeciesAssociation(oneSpecies, assocType), new HashSet<AcReaction>());

		// add reactions for species
		for (AcReaction reaction : reactionSet.getReactions())
			for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values())
				for (AcSpeciesReactionAssociation speciesReactionAssoc : reaction.getSpeciesAssociations(assocType))
					speciesAssocReactionMap.get(speciesReactionAssoc.getSpeciesAssociation()).add(reaction);

		return speciesAssocReactionMap;
	}
}