package com.bnd.chemistry.business.reactionode;

import java.util.*;

import com.bnd.chemistry.domain.AcCollectiveSpeciesReactionAssociationType;
import com.bnd.chemistry.domain.AcReaction;
import com.bnd.chemistry.domain.AcSpeciesAssociationType;
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation;
import com.bnd.core.util.ConversionUtil;
import com.bnd.function.evaluator.FunctionEvaluator;

abstract class AcSpeciesDiffsBO implements FunctionEvaluator<Double, Double[]> {

	protected final int reactionCount;
	protected final int speciesCount;

	AcSpeciesDiffsBO(int reactionCount, int speciesCount) {
		this.reactionCount = reactionCount;
		this.speciesCount = speciesCount;
	}

	protected static int[][] createReactionAssocTypeIndeces(
		Collection<AcReaction> reactions,
		AcSpeciesAssociationType type,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		int reactionCount = reactions.size();
		int[][] assocReactantIndeces = new int[reactionCount][];
		for (final AcReaction reaction : reactions) {
			int reactionIndex = reaction.getIndex();
			final Collection<AcSpeciesReactionAssociation> associations = reaction.getSpeciesAssociations(type);
			assocReactantIndeces[reactionIndex] = new int[associations.size()];
			int convertedSpeciesIndex;
			int index = 0;
			for (final AcSpeciesReactionAssociation assoc : associations) {
				convertedSpeciesIndex = magnitudeIndexConversionMap.get(assoc.getSpeciesIndex());
				assocReactantIndeces[reactionIndex][index] = convertedSpeciesIndex;
				index++;
			}
		}
		return assocReactantIndeces;
	}

	protected static AcCollectiveSpeciesReactionAssociationType[] createCollectiveCatalysisTypes(
		Collection<AcReaction> reactions
	) {
		AcCollectiveSpeciesReactionAssociationType[] types = new  AcCollectiveSpeciesReactionAssociationType[reactions.size()];
		for (final AcReaction reaction : reactions) {
			types[reaction.getIndex()] = reaction.getCollectiveCatalysisType();
		}
		return types;
	}

	protected static AcCollectiveSpeciesReactionAssociationType[] createCollectiveInhibitionTypes(
		Collection<AcReaction> reactions
	) {
		AcCollectiveSpeciesReactionAssociationType[] types = new  AcCollectiveSpeciesReactionAssociationType[reactions.size()];
		for (final AcReaction reaction : reactions) {
			types[reaction.getIndex()] = reaction.getCollectiveInhibitionType();
		}
		return types;
	}

	public abstract double[] getSpeciesDifferences(Double[] speciesConcentrations);

	@Override
	public Double[] evaluate(Double[] inputs) {
		return ConversionUtil.toComplexType(getSpeciesDifferences(inputs));
	}

	@Override
	public Double[] evaluate(Iterable<Double> inputs) {
		throw new RuntimeException("AcSpeciesDiffsBO.evaluate(Iterable) not implemented!");
	}

	@Override
	public Double[] evaluate(Map<Integer, Double> environment) {
		List<Integer> keys = new ArrayList<Integer>();
		keys.addAll(environment.keySet());
		Collections.sort(keys);
		List<Double> inputs = new ArrayList<Double>();
		for (Integer key : keys) {
			inputs.add(environment.get(key));
		}
		return evaluate(inputs);
	}

	@Override
	public Integer getArity() {
		return speciesCount;
	}

	protected int getSpeciesCount() {
		return speciesCount;
	}

	protected static boolean areAllMagnitudesNonZero(final Double[] magnitudes, int[] indeces) {
		for (int index : indeces)
			if (magnitudes[index] == 0)
				return false;
		return true;
	}

	protected static double product(final Double[] magnitudes, int[] indeces) {
		double reactantProduct = 1;
		for (int index : indeces) {
			reactantProduct *= magnitudes[index];
		}
		return reactantProduct;
	}

	protected static double sum(final Double[] magnitudes, int[] indeces) {
		double reactantSum = 0;
		for (int index : indeces) {
			reactantSum += magnitudes[index];
		}
		return reactantSum;
	}
}