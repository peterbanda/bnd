package com.bnd.chemistry.business.reactionode;

import java.util.Collection;
import java.util.Map;

import com.bnd.chemistry.domain.AcReaction;
import com.bnd.chemistry.domain.AcSpeciesAssociationType;

abstract class AcBinaryStoichiometryDiffBO extends AcSpeciesDiffsBO {

	protected final int[][] reactionReactantIndeces;
	protected final int[][] reactionProductIndeces;

	AcBinaryStoichiometryDiffBO(
		Collection<AcReaction> reactions,
		int speciesCount,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		super(reactions.size(), speciesCount);
		this.reactionReactantIndeces = createReactionAssocTypeIndeces(reactions, AcSpeciesAssociationType.Reactant, magnitudeIndexConversionMap);
		this.reactionProductIndeces = createReactionAssocTypeIndeces(reactions, AcSpeciesAssociationType.Product, magnitudeIndexConversionMap);
	}

// Main functions

	@Override
	public double[] getSpeciesDifferences(final Double[] magnitudes) {
		final double[] currentReactionRates = getCurrentReactionRates(magnitudes);
		final double[] diffs = new double[speciesCount];
		int reactionIndex = 0;
		for (double reactionRate : currentReactionRates) {
			if (reactionRate != 0) {
				for (int reactantIndex : reactionReactantIndeces[reactionIndex]) {
					diffs[reactantIndex] -= reactionRate;
				}
				for (int productIndex : reactionProductIndeces[reactionIndex]) {
					diffs[productIndex] += reactionRate;
				}
			}
			reactionIndex++;
		}
		return diffs;
	}

	protected abstract double[] getCurrentReactionRates(final Double[] magnitudes);
}