package com.bnd.chemistry.business.factory;

import java.util.*;
import java.util.Map.Entry;

import com.bnd.chemistry.domain.*;
import com.bnd.core.util.RandomUtil;
import com.bnd.math.business.rand.RandomDistributionProvider;
import com.bnd.math.business.rand.RandomDistributionProviderFactory;

public class AcSpeciesSetFactory {

	private static AcSpeciesSetFactory instance = new AcSpeciesSetFactory();

	private AcSpeciesSetFactory() {
		// Nothing to do
	}

	public static AcSpeciesSetFactory getInstance() {
		return instance;
	}

	public AcSpeciesSet createFixedOrder(Collection<String> speciesLabels) {
		AcSpeciesSet speciesSet = createEmptySpeciesSet();
	
		for (String speciesLabel : speciesLabels) {
			speciesSet.addVariable(new AcSpecies(speciesLabel));
		}
		return speciesSet;
	}

	public AcSpeciesSet createRandomOrder(Collection<String> speciesLabels) {
		List<String> speciesLabelsCopy = new ArrayList<String>(speciesLabels);
		Collections.shuffle(speciesLabelsCopy);
		return createFixedOrder(speciesLabelsCopy);
	}

	public AcSpeciesSet createFixedOrder(int speciesNumber) {
		return createFixedOrder(getLabels(speciesNumber));
	}

	public AcSpeciesSet createFixedOrder(
		int internalSpeciesNum,
		int inputSpeciesNumber,
		int outputSpeciesNum,
		int functionalSpeciesNum
	) {
		int totalSpeciesNum = internalSpeciesNum + inputSpeciesNumber + outputSpeciesNum + functionalSpeciesNum;
		if (functionalSpeciesNum != 0) {
			totalSpeciesNum += outputSpeciesNum;
		}
		AcSpeciesSet speciesSet = createFixedOrder(getLabels(totalSpeciesNum));
		Iterator<AcSpecies> speciesInterator = speciesSet.getVariablesSorted().iterator();
		for (int i = 0; i < inputSpeciesNumber; i++) {
			speciesSet.addSpeciesToGroup(AcSpeciesType.Input, speciesInterator.next());
		}
		for (int i = 0; i < outputSpeciesNum; i++) {
			speciesSet.addSpeciesToGroup(AcSpeciesType.Output, speciesInterator.next());
		}
		if (functionalSpeciesNum != 0) {
			for (int i = 0; i < outputSpeciesNum; i++) {
				speciesSet.addSpeciesToGroup(AcSpeciesType.Feedback, speciesInterator.next());
			}			
		}
		for (int i = 0; i < functionalSpeciesNum; i++) {
			speciesSet.addSpeciesToGroup(AcSpeciesType.Functional, speciesInterator.next());
		}
		return speciesSet;
	}

	public AcSpeciesSet createRandomOrder(int speciesNumber) {
		return createRandomOrder(getLabels(speciesNumber));
	}

	public AcSpeciesSet createRandomOrder(int speciesNumber, int startingSequenceNum, int startingLabel) {
		AcSpeciesSet speciesSet = new AcSpeciesSet();
		speciesSet.setParameterSet(new AcParameterSet());
		speciesSet.setVarSequenceNum(startingSequenceNum);
		for (String label : getLabels(speciesNumber, startingLabel)) {
			speciesSet.addVariable(new AcSpecies(label));
		}
		return speciesSet;
	}

	private Collection<String> getLabels(int speciesNumber) {
		return getLabels(speciesNumber, 0);
	}

	private Collection<String> getLabels(int speciesNumber, int startingLabel) {
		Collection<String> speciesLabels = new ArrayList<String>();
		for (int speciesLabel = startingLabel; speciesLabel < startingLabel + speciesNumber; speciesLabel++) {
			StringBuilder sb = new StringBuilder();
			sb.append("S");
			if (speciesLabel < 10) {
				sb.append("0");
			}
			sb.append(speciesLabel);
			speciesLabels.add(sb.toString());
		}
		return speciesLabels;
	}

	public AcDNAStrandSpeciesSet createDNAStrandSpeciesSet(AcDNAStrandSpec spec) {
		final int lowerStrandsNum = (int) Math.round(spec.getSingleStrandsNum() / (1 + spec.getUpperToLowerStrandRatio()));
		final int upperStrandsNum = spec.getSingleStrandsNum() - lowerStrandsNum;
		int fullDoubleStrandsNum = (int) Math.round(Math.min(lowerStrandsNum, upperStrandsNum) * spec.getComplementaryStrandsRatio());

		final RandomDistributionProvider<Integer> partialBindingNumDistributionProvider = RandomDistributionProviderFactory
				.apply(spec.getUpperStrandPartialBindingDistribution());

		Map<Integer, Collection<Integer>> upperLowerPartialBindingsMap = new HashMap<Integer, Collection<Integer>>();
		for (int upperIndex = 0; upperIndex < upperStrandsNum; upperIndex++) {
			int partialBindingsNum = partialBindingNumDistributionProvider.next();
			if (upperIndex > fullDoubleStrandsNum) {
				if (partialBindingsNum > lowerStrandsNum) {
					partialBindingsNum = lowerStrandsNum;
				}
			} else if (partialBindingsNum > lowerStrandsNum - 1) {
				partialBindingsNum = lowerStrandsNum - 1;
			}
			if (partialBindingsNum != 0) {
				upperLowerPartialBindingsMap.put(
					upperIndex,
					(upperIndex <= fullDoubleStrandsNum) ? RandomUtil
							.nextElementsWithoutRepetitionsExcept(upperIndex, lowerStrandsNum,
									partialBindingsNum) : RandomUtil
							.nextElementsWithoutRepetitions(lowerStrandsNum, partialBindingsNum));
			} else {
				upperLowerPartialBindingsMap.put(
					upperIndex,
					new ArrayList<Integer>());
			}
		}

		if (spec.isMirrorComplementarity()) {
			Map<Integer, Collection<Integer>> additionalPartialBindingsMap = new HashMap<Integer, Collection<Integer>>();
			for (int upperIndex = 0; upperIndex < upperStrandsNum; upperIndex++) {
				additionalPartialBindingsMap.put(upperIndex, new ArrayList<Integer>());
			}
			for (int upperStrandIndex = 0; upperStrandIndex < fullDoubleStrandsNum; upperStrandIndex++) {
				Collection<Integer> lowerStrandOfPartialBindings = upperLowerPartialBindingsMap.get(upperStrandIndex);
				for (Integer lowerStrandOfPartialBinding : lowerStrandOfPartialBindings) {
					if (lowerStrandOfPartialBinding < fullDoubleStrandsNum) {
						additionalPartialBindingsMap.get(lowerStrandOfPartialBinding).add(upperStrandIndex);
					}
				}
			}

			for (Entry<Integer, Collection<Integer>> additionalPartialBindings : additionalPartialBindingsMap.entrySet()) {
				final Collection<Integer> associatedLowerStrands = upperLowerPartialBindingsMap.get(additionalPartialBindings.getKey());
				for (Integer additionalLowerStrand : additionalPartialBindings.getValue()) {
					if (!associatedLowerStrands.contains(additionalLowerStrand)) {
						associatedLowerStrands.add(additionalLowerStrand);
					}
				}
			}
		}

		// create species
		final AcDNAStrandSpeciesSet speciesSet = createEmptyDNAStrandSpeciesSet();
		Map<Integer, AcSpecies> upperStrandIndexSpeciesMap = new HashMap<Integer, AcSpecies>();
		Map<Integer, AcSpecies> lowerStrandIndexSpeciesMap = new HashMap<Integer, AcSpecies>();

		for (int upperIndex = 0; upperIndex < upperStrandsNum; upperIndex++) {
			final AcSpecies species = new AcSpecies(upperStrandLabel(upperIndex));
			speciesSet.addUpperStrand(species);
			upperStrandIndexSpeciesMap.put(upperIndex, species);
		}

		for (int lowerIndex = 0; lowerIndex < lowerStrandsNum; lowerIndex++) {
			final AcSpecies species = new AcSpecies(lowerStrandLabel(lowerIndex));
			speciesSet.addLowerStrand(species);
			lowerStrandIndexSpeciesMap.put(lowerIndex, species);
		}
	
		for (int fullDoubleIndex = 0; fullDoubleIndex < fullDoubleStrandsNum; fullDoubleIndex++) {
			final AcCompositeSpecies species = new AcCompositeSpecies(fullDoubleStrandLabel(fullDoubleIndex));
			species.addComponent(upperStrandIndexSpeciesMap.get(fullDoubleIndex));
			species.addComponent(lowerStrandIndexSpeciesMap.get(fullDoubleIndex));
			speciesSet.addFullDoubleStrand(species);
		}

		for (Entry<Integer, Collection<Integer>> upperLowerStrandBindings : upperLowerPartialBindingsMap.entrySet()) {
			final Integer upperStrandOfBinding = upperLowerStrandBindings.getKey();
			for (Integer lowerStrandOfBinding : upperLowerStrandBindings.getValue()) {
				final AcCompositeSpecies species = new AcCompositeSpecies(partialDoubleStrandLabel(upperStrandOfBinding, lowerStrandOfBinding));
				species.addComponent(upperStrandIndexSpeciesMap.get(upperStrandOfBinding));
				species.addComponent(lowerStrandIndexSpeciesMap.get(lowerStrandOfBinding));
				speciesSet.addPartialDoubleStrand(species);
			}
		}

		return speciesSet;
	}

	private static String upperStrandLabel(int upperStrandIndex) {
		return "U_"+ upperStrandIndex; 
	}

	private static String lowerStrandLabel(int lowerStrandIndex) {
		return "L_"+ lowerStrandIndex; 
	}

	private static String fullDoubleStrandLabel(int fullDoubleIndex) {
		return "FD_" + fullDoubleIndex;
	}

	private static String partialDoubleStrandLabel(int upperStrandIndex, int lowerStrandIndex) {
		return "PD_" + upperStrandIndex + ":" + lowerStrandIndex; 
	}

	private AcSpeciesSet createEmptySpeciesSet() {
		AcSpeciesSet speciesSet = new AcSpeciesSet();
		AcParameterSet parameterSet = new AcParameterSet();
		speciesSet.setParameterSet(parameterSet);
		parameterSet.setSpeciesSet(speciesSet);		
		return speciesSet;
	}

	private AcDNAStrandSpeciesSet createEmptyDNAStrandSpeciesSet() {
		AcDNAStrandSpeciesSet speciesSet = new AcDNAStrandSpeciesSet();
		AcParameterSet parameterSet = new AcParameterSet();
		speciesSet.setParameterSet(parameterSet);
		parameterSet.setSpeciesSet(speciesSet);		
		return speciesSet;
	}
}