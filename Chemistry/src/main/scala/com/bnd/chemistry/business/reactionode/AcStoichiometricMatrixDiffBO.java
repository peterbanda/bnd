package com.bnd.chemistry.business.reactionode;

import java.util.Collection;
import java.util.Map;

import org.jblas.DoubleMatrix;

import com.bnd.chemistry.domain.AcReaction;
import com.bnd.chemistry.domain.AcSpeciesAssociationType;
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation;

abstract class AcStoichiometricMatrixDiffBO extends AcSpeciesDiffsBO {

	protected final DoubleMatrix stoichiometricMatrix;
	
	AcStoichiometricMatrixDiffBO(
		Collection<AcReaction> reactions,
		int speciesCount,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		super(reactions.size(), speciesCount);
		this.stoichiometricMatrix = createStoichiometricMatrix(reactions, speciesCount, magnitudeIndexConversionMap);
	}

	private static DoubleMatrix createStoichiometricMatrix(
		Collection<AcReaction> reactions,
		int speciesCount,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		final double[][] stoichiometricMatrix = createStoichiometricMatrixRaw(reactions, speciesCount, magnitudeIndexConversionMap);
		DoubleMatrix stoichiometricMatrix2 = new DoubleMatrix(stoichiometricMatrix);
		return stoichiometricMatrix2.transpose();
	}

	private static double[][] createStoichiometricMatrixRaw(
		Collection<AcReaction> reactions,
		int speciesCount,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		int reactionCount = reactions.size();
		double[][] stoichiometricMatrix = new double[reactionCount][speciesCount];
		for (final AcReaction reaction : reactions) {
			int reactionIndex = reaction.getIndex();
			int convertedSpeciesIndex;
			for (final AcSpeciesReactionAssociation reactantAssociation : reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant)) {
				convertedSpeciesIndex = magnitudeIndexConversionMap.get(reactantAssociation.getSpeciesIndex());
				stoichiometricMatrix[reactionIndex][convertedSpeciesIndex] -= reactantAssociation.getStoichiometricFactor();
			}
			for (final AcSpeciesReactionAssociation productAssociation : reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product)) {
				convertedSpeciesIndex = magnitudeIndexConversionMap.get(productAssociation.getSpeciesIndex());
				stoichiometricMatrix[reactionIndex][convertedSpeciesIndex] += productAssociation.getStoichiometricFactor();
			}
		}
		return stoichiometricMatrix;
	}

	@Override
	public double[] getSpeciesDifferences(Double[] magnitudes) {
		final double[] currentReactionRates = getCurrentReactionRates(magnitudes);
		final DoubleMatrix currentReactionRatesVector = new DoubleMatrix(currentReactionRates);
		final DoubleMatrix magnitudesDiffMatrix = stoichiometricMatrix.mmul(currentReactionRatesVector);
		return magnitudesDiffMatrix.data;
	}

	protected abstract double[] getCurrentReactionRates(final Double[] magnitudes);
}