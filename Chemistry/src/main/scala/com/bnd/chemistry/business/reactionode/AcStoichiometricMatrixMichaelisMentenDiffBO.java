package com.bnd.chemistry.business.reactionode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.bnd.chemistry.domain.AcCollectiveSpeciesReactionAssociationType;
import com.bnd.chemistry.domain.AcReaction;
import com.bnd.chemistry.domain.AcSpeciesAssociationType;
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation;
import com.bnd.core.util.ConversionUtil;

final class AcStoichiometricMatrixMichaelisMentenDiffBO extends AcStoichiometricMatrixDiffBO {

	private final int[][] reactionReactantIndeces;
	private final int[][] reactionCatalystIndeces;
	private final int[][] reactionInhibitorIndeces;
	private AcCollectiveSpeciesReactionAssociationType[] collectiveCatalysisTypes;
	private AcCollectiveSpeciesReactionAssociationType[] collectiveInhibitionTypes;

	private final double[][] reactantStoichiometricMatrix;
	private final double[][] rateConstants;
	private final Map<Integer, Collection<Integer>> guardReactionIndexMap;

	AcStoichiometricMatrixMichaelisMentenDiffBO(
		Collection<AcReaction> reactions,
		int speciesCount,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		super(reactions, speciesCount, magnitudeIndexConversionMap);
		validate(reactions);
		this.reactionReactantIndeces = createReactionAssocTypeIndeces(reactions, AcSpeciesAssociationType.Reactant, magnitudeIndexConversionMap);
		this.reactionCatalystIndeces = createReactionAssocTypeIndeces(reactions, AcSpeciesAssociationType.Catalyst, magnitudeIndexConversionMap);
		this.reactionInhibitorIndeces = createReactionAssocTypeIndeces(reactions, AcSpeciesAssociationType.Inhibitor, magnitudeIndexConversionMap);
		this.collectiveCatalysisTypes = createCollectiveCatalysisTypes(reactions);
		this.collectiveInhibitionTypes = createCollectiveInhibitionTypes(reactions);

		this.reactantStoichiometricMatrix = createReactantStoichiometricMatrix(reactions);
		this.rateConstants = createRateConstants(reactions);
		this.guardReactionIndexMap = createGuardReactionIndexMap(reactions, speciesCount, magnitudeIndexConversionMap);
	}

// Initializations

	private void validate(Collection<AcReaction> reactions) {
		// TODO
	}

	private static double[][] createReactantStoichiometricMatrix(Collection<AcReaction> reactions) {
		int reactionCount = reactions.size();
		double[][] rateSpeciesStoichiometricMatrix = new double[reactionCount][];
		for (final AcReaction reaction : reactions) {
			final int reactionIndex = reaction.getIndex();
			int speciesOrder = 0;
			Collection<AcSpeciesReactionAssociation> reactantAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant);
			rateSpeciesStoichiometricMatrix[reactionIndex] = new double[reactantAssocs.size()];
			for (final AcSpeciesReactionAssociation reactantAssociation : reactantAssocs) {
				rateSpeciesStoichiometricMatrix[reactionIndex][speciesOrder] = reactantAssociation.getStoichiometricFactor();
				speciesOrder++;
			}
		}
		return rateSpeciesStoichiometricMatrix;
	}

	private static double[][] createRateConstants(Collection<AcReaction> reactions) {
		double[][] rateConstants = new double[reactions.size()][];
		int reactionIndex = 0;
		for (AcReaction reaction : reactions) {
			rateConstants[reactionIndex] = ConversionUtil.toSimpleType(reaction.getForwardRateConstants());
			reactionIndex++;
		}
		return rateConstants;
	}

	private static Map<Integer, Collection<Integer>> createGuardReactionIndexMap(
		Collection<AcReaction> reactions,
		int speciesCount,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		Map<Integer, Collection<Integer>> guardReactionIndecesMap = new HashMap<Integer, Collection<Integer>>();
		for (int speciesIndex = 0; speciesIndex < speciesCount; speciesIndex++) {
			guardReactionIndecesMap.put(speciesIndex, new ArrayList<Integer>());
		}
		for (final AcReaction reaction : reactions) {
			int reactionIndex = reaction.getIndex();
			int convertedSpeciesIndex;
			Collection<AcSpeciesReactionAssociation> reactantAssocs = reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant);
			for (final AcSpeciesReactionAssociation reactantAssociation : reactantAssocs) {
				convertedSpeciesIndex = magnitudeIndexConversionMap.get(reactantAssociation.getSpeciesIndex());
				Collection<Integer> reactionIndeces = guardReactionIndecesMap.get(convertedSpeciesIndex);
				reactionIndeces.add(reactionIndex);
			}
		}
		return guardReactionIndecesMap;
	}	

// Main functions

	@Override
	protected double[] getCurrentReactionRates(final Double[] magnitudes) {
//		Set<Integer> idleReactions = new HashSet<Integer>();
//		for (int speciesIndex = 0; speciesIndex < getSpeciesCount(); speciesIndex++) {
//			if (magnitudes[speciesIndex] == 0) {
//				idleReactions.addAll(guardReactionIndexMap.get(speciesIndex));
//			}
//		}
		double[] reactionRates = new double[reactionCount];
		int reactionIndex = 0;		
		for (final int[] reactantIndeces : reactionReactantIndeces) {
			double result = 0;
			if (isReactionActive(magnitudes, reactionIndex)) {
				final int reactantNum = reactantIndeces.length;
				final double[] reactantStoichiometricVector = reactantStoichiometricMatrix[reactionIndex];
				final double[] constants = rateConstants[reactionIndex];
			
				// reactant product
				double reactantProduct = 1;
				for (int reactantIndex = 0; reactantIndex < reactantNum; reactantIndex++) {
					final double magnitude = magnitudes[reactantIndeces[reactantIndex]];
					if (reactantStoichiometricVector[reactantIndex] == 1d) {
						reactantProduct *= magnitude;
					} else {
						reactantProduct *= Math.pow(magnitude, reactantStoichiometricVector[reactantIndex]);
					}
				}

				result = constants[0] * reactantProduct; 
				int constIndex = 1;

				// catalysts
				if (reactionCatalystIndeces[reactionIndex].length > 0) {
					if (collectiveCatalysisTypes[reactionIndex] == AcCollectiveSpeciesReactionAssociationType.OR)
						result *= sum(magnitudes, reactionCatalystIndeces[reactionIndex]);
					else
						result *= product(magnitudes, reactionCatalystIndeces[reactionIndex]);

					double denominator = constants[constIndex] + reactantProduct;
					constIndex++;

					// if we have more than one reactant we add const * reactant magnitude terms
					if (reactionReactantIndeces[reactionIndex].length > 1) {
						for (int reactantIndex : reactionReactantIndeces[reactionIndex]) {
							denominator += constants[constIndex] * magnitudes[reactantIndex];
							constIndex++;
						}
					}
					result /= denominator;
				}

				// inhibitors
				if (reactionInhibitorIndeces[reactionIndex].length > 0) {
					double inhibitorExpression = 0;
					if (collectiveInhibitionTypes[reactionIndex] == AcCollectiveSpeciesReactionAssociationType.OR)
						inhibitorExpression = sum(magnitudes, reactionInhibitorIndeces[reactionIndex]);
					else
						inhibitorExpression = product(magnitudes, reactionInhibitorIndeces[reactionIndex]);

					result /= (1 + constants[constIndex] * inhibitorExpression);
				}
			}

			reactionRates[reactionIndex] = result;
			reactionIndex++;
		}
		return reactionRates;
	}

	private boolean isReactionActive(Double[] magnitudes, int reactionIndex) {
		final int catalystsNum = reactionCatalystIndeces[reactionIndex].length;
		if (areAllMagnitudesNonZero(magnitudes, reactionReactantIndeces[reactionIndex])) {
			 if (catalystsNum == 0)
				 return true;
			 if (catalystsNum == 1 || collectiveCatalysisTypes[reactionIndex] != AcCollectiveSpeciesReactionAssociationType.OR) 
				 return areAllMagnitudesNonZero(magnitudes, reactionCatalystIndeces[reactionIndex]);
			 return true;
		}
		return false;
	}
}