package com.bnd.chemistry.business.reactionode;

import java.util.Collection;
import java.util.Map;

import com.bnd.chemistry.domain.AcCollectiveSpeciesReactionAssociationType;
import com.bnd.chemistry.domain.AcReaction;
import com.bnd.chemistry.domain.AcSpeciesAssociationType;
import com.bnd.core.util.ConversionUtil;

final class AcBinaryStoichiometryMichaelisMentenDiffBO extends AcBinaryStoichiometryDiffBO {

	private final int[][] reactionCatalystIndeces;
	private final int[][] reactionInhibitorIndeces;
	private AcCollectiveSpeciesReactionAssociationType[] collectiveCatalysisTypes;
	private AcCollectiveSpeciesReactionAssociationType[] collectiveInhibitionTypes;

	private final double[][] rateConstants;

	AcBinaryStoichiometryMichaelisMentenDiffBO(
		Collection<AcReaction> reactions,
		int speciesCount,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		super(reactions, speciesCount, magnitudeIndexConversionMap);
		validate(reactions);
		this.reactionCatalystIndeces = createReactionAssocTypeIndeces(reactions, AcSpeciesAssociationType.Catalyst, magnitudeIndexConversionMap);
		this.reactionInhibitorIndeces = createReactionAssocTypeIndeces(reactions, AcSpeciesAssociationType.Inhibitor, magnitudeIndexConversionMap);
		this.collectiveCatalysisTypes = createCollectiveCatalysisTypes(reactions);
		this.collectiveInhibitionTypes = createCollectiveInhibitionTypes(reactions);
		this.rateConstants = createRateConstants(reactions);
	}

// Initializations

	private void validate(Collection<AcReaction> reactions) {
		// TODO
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

	@Override
	protected double[] getCurrentReactionRates(final Double[] magnitudes) {
		double[] reactionRates = new double[reactionCount];
		int reactionIndex = 0;
		for (final double[] constants : rateConstants) {
			double result = 0;
			if (isReactionActive(magnitudes, reactionIndex)) {
				// reactant product				
				final double reactantProduct = product(magnitudes, reactionReactantIndeces[reactionIndex]);

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