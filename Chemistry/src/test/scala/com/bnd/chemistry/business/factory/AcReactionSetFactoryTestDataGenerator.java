package com.bnd.chemistry.business.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.bnd.chemistry.domain.*;
import com.bnd.core.util.RandomUtil;
import com.bnd.math.domain.rand.RandomDistribution;


public class AcReactionSetFactoryTestDataGenerator extends AcCoreTestDataGenerator {

	protected class AcComplexRSData {
		protected Collection<AcSpecies> species;
		protected AcReactionSetConstraints reactionSetConstraints;
		protected AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy;
	}

	protected class AcComplexRSAllowedSpeciesData {
		protected Collection<AcSpecies> allowedReactantSpecies;
		protected Collection<AcSpecies> allowedProductSpecies;
		protected Collection<AcSpecies> allowedCatalystSpecies;
		protected Collection<AcSpecies> allowedInhibitorSpecies;
		protected AcReactionSetConstraints reactionSetConstraints;
		protected AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy;
	}

	protected class AcRandomRSData {
		protected int reactionsNumber;
		protected int reactantsNumber;
		protected int productsNumber;
		protected int catalystsNumber;
		protected int inhibitorsNumber;
		protected AcSpeciesSet speciesSet;
		protected AcReactionSpeciesForbiddenRedundancy speciesForbiddenRedundancy;
	}

	protected class AcRandomReactionSymmetricRSData {
		protected AcSymmetricSpec spec;
		protected AcSpeciesSet speciesSet;
	}

	protected class AcDNAStrandRSData {
		protected AcDNAStrandSpec spec;
		protected AcDNAStrandSpeciesSet speciesSet;
	}

	private Collection<AcComplexRSData> complexRSData = new ArrayList<AcComplexRSData>();
	private Collection<AcComplexRSData> complexRSFewSpeciesData = new ArrayList<AcComplexRSData>();
	private Collection<AcComplexRSAllowedSpeciesData> complexRSAllowedSpeciesData = new ArrayList<AcComplexRSAllowedSpeciesData>();
	private Collection<AcComplexRSAllowedSpeciesData> complexFewSpeciesRSAllowedSpeciesData = new ArrayList<AcComplexRSAllowedSpeciesData>();
	private Collection<AcRandomRSData> randomRSData = new ArrayList<AcRandomRSData>();
	private Collection<AcRandomReactionSymmetricRSData> randomReactionSymmetricRSData = new ArrayList<AcRandomReactionSymmetricRSData>();
	private Collection<AcSpeciesSet> outputFeedbackEnabledSpeciesSets = new ArrayList<AcSpeciesSet>();
	private Collection<AcDNAStrandRSData> dnaStrandRSData = new ArrayList<AcDNAStrandRSData>();

	private Collection<AcReactionSetConstraints> reactionSetConstraints = new HashSet<AcReactionSetConstraints>();

	public AcReactionSetFactoryTestDataGenerator() {
		setUpTestData();
	}

	private void setUpTestData() {
		setUpReactionSetConstraints();
		setUpComplexRSData();
		setUpComplexRSFewSpeciesData();
		setUpComplexRSAllowedSpeciesData();
		setUpComplexFewSpeciesRSAllowedSpeciesData();
		setUpRandomRSData();
		setUpRandomReactionSymmetricRSData();
		setUpOutputFeedbackEnabledSpeciesSets();
		setUpDnaStrandRSData();
	}

	private void setUpReactionSetConstraints() {
		// instance 1
		AcReactionToSpeciesConstraints rToSConstraints1 = new AcReactionToSpeciesConstraints();
		rToSConstraints1.setMaxReactantsNum(2);
		rToSConstraints1.setMaxProductsNum(2);
		rToSConstraints1.setMaxCatalystsNum(1);
		rToSConstraints1.setMaxInhibitorsNum(0);

		AcSpeciesToReactionConstraints sToRConstraints1 = new AcSpeciesToReactionConstraints();
		sToRConstraints1.setFixedProductAssocsNum(2);

		AcReactionSetConstraints reactionSetConstraints1 = new AcReactionSetConstraints(rToSConstraints1, sToRConstraints1, 5d);
		reactionSetConstraints.add(reactionSetConstraints1);

		// instance 2
		AcReactionToSpeciesConstraints rToSConstraints2 = new AcReactionToSpeciesConstraints();
		rToSConstraints2.setMaxReactantsNum(1);
		rToSConstraints2.setMaxProductsNum(1);
		rToSConstraints2.setMaxCatalystsNum(1);
		rToSConstraints2.setMaxInhibitorsNum(1);
		rToSConstraints2.setMinReactantsNum(1);
		rToSConstraints2.setMinProductsNum(1);

		AcSpeciesToReactionConstraints sToRConstraints2 = new AcSpeciesToReactionConstraints();
		sToRConstraints2.setFixedReactantAssocsNum(1);
		sToRConstraints2.setFixedCatalystAssocsNum(1);

		AcReactionSetConstraints reactionSetConstraints2 = new AcReactionSetConstraints(rToSConstraints2, sToRConstraints2, 1d);
		reactionSetConstraints.add(reactionSetConstraints2);

		// instance 3
		AcReactionToSpeciesConstraints rToSConstraints3 = new AcReactionToSpeciesConstraints();
		rToSConstraints3.setMaxReactantsNum(2);
		rToSConstraints3.setMaxProductsNum(2);
		rToSConstraints3.setMaxCatalystsNum(1);
		rToSConstraints3.setMaxInhibitorsNum(1);
		rToSConstraints3.setMinReactantsNum(1);
		rToSConstraints3.setMinProductsNum(1);

		AcSpeciesToReactionConstraints sToRConstraints3 = new AcSpeciesToReactionConstraints();
		sToRConstraints3.setFixedProductAssocsNum(2);
		sToRConstraints3.setFixedCatalystAssocsNum(1);

		AcReactionSetConstraints reactionSetConstraints3 = new AcReactionSetConstraints(rToSConstraints3, sToRConstraints3, 1.6d);
		reactionSetConstraints.add(reactionSetConstraints3);

		// instance 4
		AcReactionToSpeciesConstraints rToSConstraints4 = new AcReactionToSpeciesConstraints();
		rToSConstraints4.setMaxReactantsNum(3);
		rToSConstraints4.setMaxProductsNum(3);
		rToSConstraints4.setMaxCatalystsNum(2);
		rToSConstraints4.setMaxInhibitorsNum(2);
		rToSConstraints4.setMinCatalystsNum(1);

		AcSpeciesToReactionConstraints sToRConstraints4 = new AcSpeciesToReactionConstraints();
		sToRConstraints4.setFixedReactantAssocsNum(3);
		sToRConstraints4.setFixedProductAssocsNum(1);
		sToRConstraints4.setFixedInhibitorAssocsNum(1);

		AcReactionSetConstraints reactionSetConstraints4 = new AcReactionSetConstraints(rToSConstraints4, sToRConstraints4, 3d);
		reactionSetConstraints.add(reactionSetConstraints4);
	}

	private void setUpComplexRSData() {
		for (AcReactionSetConstraints oneReactionSetConstraints : reactionSetConstraints) {
			AcComplexRSData testData = new AcComplexRSData();
			testData.species = createSpecies(randomInt(20, 60));
			testData.reactionSetConstraints = oneReactionSetConstraints;
			testData.speciesForbiddenRedundancy = AcReactionSpeciesForbiddenRedundancy.SameAssocTypeAndReactantProduct;
			complexRSData.add(testData);			
		}
	}

	private void setUpComplexRSFewSpeciesData() {
		for (AcReactionSetConstraints oneReactionSetConstraints : reactionSetConstraints) {
			AcComplexRSData testData = new AcComplexRSData();
			testData.species = createSpecies(randomInt(10, 20));
			testData.reactionSetConstraints = oneReactionSetConstraints;
			testData.speciesForbiddenRedundancy = AcReactionSpeciesForbiddenRedundancy.SameAssocTypeAndReactantProduct;
			complexRSFewSpeciesData.add(testData);			
		}
	}

	private void setUpComplexRSAllowedSpeciesData() {
		for (final AcReactionSetConstraints oneReactionSetConstraints : reactionSetConstraints) {
			AcReactionSetConstraints oneReactionSetConstraintsCopy = new AcReactionSetConstraints();
			oneReactionSetConstraintsCopy.copyFrom(oneReactionSetConstraints);
			final AcReactionToSpeciesConstraints rToSContraints = oneReactionSetConstraints.getReactionToSpeciesConstraints();
			for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
				Integer minAssocsNum = rToSContraints.getMinSpeciesAssocsNum(assocType);
				if (minAssocsNum != null) {
					oneReactionSetConstraintsCopy.setReactionsPerSpeciesRatioAssocType(assocType);
					break;
				}
			}
			List<AcSpeciesSet> speciesGroups = new ArrayList<AcSpeciesSet>();
			
			// prepare species groups with random overlap
			AcSpeciesSet reactantGroup = addRandomSpeciesSet(speciesGroups, 50, 60);
			AcSpeciesSet productGroup = addRandomSpeciesSet(speciesGroups, 30, 60);
			AcSpeciesSet catalystGroup = addRandomSpeciesSet(speciesGroups, 10, 30);
			AcSpeciesSet inhibitorGroup = addRandomSpeciesSet(speciesGroups, 8, 20);

			AcComplexRSAllowedSpeciesData testData = new AcComplexRSAllowedSpeciesData();			
			testData.allowedReactantSpecies = reactantGroup.getVariables();
			testData.allowedProductSpecies = productGroup.getVariables();
			testData.allowedCatalystSpecies = catalystGroup.getVariables();
			testData.allowedInhibitorSpecies = inhibitorGroup.getVariables();
			testData.reactionSetConstraints = oneReactionSetConstraintsCopy;
			testData.speciesForbiddenRedundancy = AcReactionSpeciesForbiddenRedundancy.SameAssocTypeAndReactantProduct;
			complexRSAllowedSpeciesData.add(testData);			
		}
	}

	private void setUpComplexFewSpeciesRSAllowedSpeciesData() {
		for (final AcReactionSetConstraints oneReactionSetConstraints : reactionSetConstraints) {
			AcReactionSetConstraints oneReactionSetConstraintsCopy = new AcReactionSetConstraints();
			oneReactionSetConstraintsCopy.copyFrom(oneReactionSetConstraints);
			final AcReactionToSpeciesConstraints rToSContraints = oneReactionSetConstraints.getReactionToSpeciesConstraints();
			for (AcSpeciesAssociationType assocType : AcSpeciesAssociationType.values()) {
				Integer minAssocsNum = rToSContraints.getMinSpeciesAssocsNum(assocType);
				if (minAssocsNum != null) {
					oneReactionSetConstraintsCopy.setReactionsPerSpeciesRatioAssocType(assocType);
					break;
				}
			}
			List<AcSpeciesSet> speciesGroups = new ArrayList<AcSpeciesSet>();
			
			// prepare species groups with random overlap
			AcSpeciesSet reactantGroup = addRandomSpeciesSet(speciesGroups, 10, 15);
			AcSpeciesSet productGroup = addRandomSpeciesSet(speciesGroups, 10, 15);
			AcSpeciesSet catalystGroup = addRandomSpeciesSet(speciesGroups, 8, 10);
			AcSpeciesSet inhibitorGroup = addRandomSpeciesSet(speciesGroups, 5, 20);

			AcComplexRSAllowedSpeciesData testData = new AcComplexRSAllowedSpeciesData();			
			testData.allowedReactantSpecies = reactantGroup.getVariables();
			testData.allowedProductSpecies = productGroup.getVariables();
			testData.allowedCatalystSpecies = catalystGroup.getVariables();
			testData.allowedInhibitorSpecies = inhibitorGroup.getVariables();
			testData.reactionSetConstraints = oneReactionSetConstraintsCopy;
			testData.speciesForbiddenRedundancy = AcReactionSpeciesForbiddenRedundancy.SameAssocTypeAndReactantProduct;
			complexFewSpeciesRSAllowedSpeciesData.add(testData);			
		}
	}

	private void setUpDnaStrandRSData() {
		for (int i = 0; i < 20; i++) {
			AcDNAStrandSpec spec = new AcDNAStrandSpec();
			spec.setUpperToLowerStrandRatio(randomDouble(0.5, 2D));
			spec.setComplementaryStrandsRatio(randomDouble(0.1, 0.5));
			spec.setUpperStrandPartialBindingDistribution(RandomDistribution.createNormalDistribution(Integer.class, 2D, 0.25));
			spec.setMirrorComplementarity(true);
			spec.setUseGlobalOrder(RandomUtil.nextBoolean());
			spec.setConstantSpeciesRatio(randomDouble(0, 0.1));
			spec.setSingleStrandsNum(randomInt(10, 20));
			spec.setRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.1, 0.001));

			spec.setInfluxRatio(randomDouble(0, 0.4));
			spec.setInfluxRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.01, 0.001));

			spec.setOutfluxRatio(randomDouble(0, 0.4));
			spec.setOutfluxRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.01, 0.001));

			spec.setOutfluxNonReactiveRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.5, 0D));

			AcDNAStrandRSData testData = new AcDNAStrandRSData();			
			testData.speciesSet = speciesSetFactory.createDNAStrandSpeciesSet(spec);
			testData.spec = spec; 
			dnaStrandRSData.add(testData);
		}
	}

	private void setUpRandomRSData() {
		for (int i = 0; i < 20; i++) {
			AcRandomRSData testData = new AcRandomRSData();
			testData.reactantsNumber = randomInt(10, 30);
			testData.productsNumber = randomInt(10, 30);
			testData.catalystsNumber = randomInt(5, 15);
			testData.inhibitorsNumber = randomInt(5, 15);
			testData.reactionsNumber = randomInt(50, 100);
			testData.speciesSet = createSpeciesSet(randomInt(30, 50), null);
			testData.speciesForbiddenRedundancy = AcReactionSpeciesForbiddenRedundancy.SameAssocTypeAndReactantProduct;
			randomRSData.add(testData);
		}
	}

	private void setUpRandomReactionSymmetricRSData() {
		for (int i = 0; i < 20; i++) {
			AcSymmetricSpec spec = new AcSymmetricSpec();
			spec.setReactionNum(randomInt(50, 100));
			spec.setSpeciesNum(randomInt(30, 50));
			spec.setReactantsPerReactionNumber(randomInt(1, 3));
			spec.setProductsPerReactionNumber(randomInt(1, 3));
			spec.setCatalystsPerReactionNumber(randomInt(0, 2));
			spec.setInhibitorsPerReactionNumber(randomInt(0, 2));
			spec.setRateConstantDistribution(RandomDistribution.createLogNormalDistribution(-2D, 0.25));
			spec.setSpeciesForbiddenRedundancy(AcReactionSpeciesForbiddenRedundancy.SameAssocTypeAndReactantProduct);
			spec.setInfluxRatio(0.1);
			spec.setInfluxRateConstantDistribution(RandomDistribution.createPositiveNormalDistribution(0.01, 0.001));

			AcRandomReactionSymmetricRSData testData = new AcRandomReactionSymmetricRSData();
			testData.spec = spec;
			testData.speciesSet = createSpeciesSet(spec.getSpeciesNum(), spec.getConstantSpeciesRatio());

			randomReactionSymmetricRSData.add(testData);
		}
	}
	
	private void setUpOutputFeedbackEnabledSpeciesSets() {
		for (int i = 0; i < 20; i++) {
			final AcSpeciesSet speciesSet = speciesSetFactory.createFixedOrder(randomInt(10, 100), 0, 2, 2 * randomInt(1, 4));
			outputFeedbackEnabledSpeciesSets.add(speciesSet);
		}		
	}

	protected Collection<AcComplexRSData> getComplexRSData() {
		return complexRSData;
	}

	protected Collection<AcComplexRSData> getComplexRSFewSpeciesData() {
		return complexRSFewSpeciesData;
	}

	protected Collection<AcComplexRSAllowedSpeciesData> getComplexRSAllowedSpeciesData() {
		return complexRSAllowedSpeciesData;
	}

	protected Collection<AcComplexRSAllowedSpeciesData> getComplexFewSpeciesRSAllowedSpeciesData() {
		return complexFewSpeciesRSAllowedSpeciesData;
	}

	protected Collection<AcRandomRSData> getRandomRSData() {
		return randomRSData;
	}

	protected Collection<AcRandomReactionSymmetricRSData> getRandomReactionSymmetricRSData() {
		return randomReactionSymmetricRSData;
	}

	protected Collection<AcSpeciesSet> getOutputFeedbackEnabledSpeciesSets() {
		return outputFeedbackEnabledSpeciesSets;
	}

	protected Collection<AcDNAStrandRSData> getDnaStrandRSData() {
		return dnaStrandRSData;
	}

	private AcSpeciesSet addRandomSpeciesSet(
		List<AcSpeciesSet> speciesSets,
		int speciesMin,
		int speciesMax
	) {
		int maxSequenceNum = 0;
		int speciesNum = 0;
		for (AcSpeciesSet speciesSet : speciesSets) {
			maxSequenceNum = Math.max(speciesSet.getVarSequenceNum(), maxSequenceNum);
			speciesNum += speciesSet.getVariablesNumber();
		}
		int groupIndex = randomInt(-1, speciesSets.size());
		AcSpeciesSet speciesGroup = null;
		if (groupIndex < 0) {
			speciesGroup = createSpeciesSet(randomInt(speciesMin, speciesMax), maxSequenceNum, speciesNum);
			speciesSets.add(speciesGroup);
		} else {
			speciesGroup = speciesSets.get(groupIndex);
		}
		return speciesGroup;
	}
}