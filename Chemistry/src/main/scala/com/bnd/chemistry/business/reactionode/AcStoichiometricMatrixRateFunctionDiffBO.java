package com.bnd.chemistry.business.reactionode;

import java.util.*;

import com.bnd.chemistry.business.AcKineticsBO;
import com.bnd.chemistry.domain.AcParameter;
import com.bnd.chemistry.domain.AcReaction;
import com.bnd.core.util.ObjectUtil;
import com.bnd.chemistry.business.DoubleFunctionEvaluatorWrapper;
import com.bnd.chemistry.business.FunctionEvaluatorWrapper;
import com.bnd.function.domain.Function;

final class AcStoichiometricMatrixRateFunctionDiffBO extends AcStoichiometricMatrixDiffBO {

	private final Collection<DoubleFunctionEvaluatorWrapper<AcReaction>> wrappedReactions;
	private final Map<Integer, Collection<Integer>> guardReactionIndexMap;
	private final Map<Integer, Integer> reversedMagnitudeIndexConversionMap;

	AcStoichiometricMatrixRateFunctionDiffBO(
		Collection<AcReaction> reactions,
		int speciesCount,
		Collection<AcParameter> params,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		super(reactions, speciesCount, magnitudeIndexConversionMap);
		this.wrappedReactions = createWrappedReactions(reactions, params, magnitudeIndexConversionMap);
		this.guardReactionIndexMap = createGuardReactionIndexMap(reactions, params);
		this.reversedMagnitudeIndexConversionMap = ObjectUtil.switchKeyWithValue(magnitudeIndexConversionMap);
	}

// Initializations

	private static Collection<DoubleFunctionEvaluatorWrapper<AcReaction>> createWrappedReactions(
		Collection<AcReaction> reactions,
		Collection<AcParameter> substitutionParameters,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		Collection<DoubleFunctionEvaluatorWrapper<AcReaction>> wrappedReactions = new ArrayList<DoubleFunctionEvaluatorWrapper<AcReaction>>();
		for (final AcReaction reaction : reactions) {
			DoubleFunctionEvaluatorWrapper<AcReaction> wrappedReaction;
			if (reaction.getForwardRateFunction() != null) {
				wrappedReaction = new DoubleFunctionEvaluatorWrapper<AcReaction>(reaction, magnitudeIndexConversionMap);
			} else {
				final AcKineticsBO kineticsBO = AcKineticsBO.createInstance(reaction, true, substitutionParameters);
				final Function<Double, Double> rateFunction = kineticsBO.createRateFunction();
				wrappedReaction = new DoubleFunctionEvaluatorWrapper<AcReaction>(reaction, rateFunction, magnitudeIndexConversionMap);
			}
			wrappedReactions.add(wrappedReaction);
		}
		return wrappedReactions;
	}

	private static Map<Integer, Collection<Integer>> createGuardReactionIndexMap(
		Collection<AcReaction> reactions,
		Collection<AcParameter> substitutionParameters
	) {
		Map<Integer, Collection<Integer>> guardReactionIndecesMap = new HashMap<Integer, Collection<Integer>>();
		for (final AcReaction reaction : reactions) {
			if (reaction.getForwardRateFunction() == null) {
				final AcKineticsBO kineticsBO = AcKineticsBO.createInstance(reaction, true, substitutionParameters);
				for (final Integer guardIndex : kineticsBO.getGuardVariableIndeces()) {
					Collection<Integer> reactionIndeces = guardReactionIndecesMap.get(guardIndex);
					if (reactionIndeces == null) {
						reactionIndeces = new HashSet<Integer>();
						guardReactionIndecesMap.put(guardIndex, reactionIndeces);
					}
					reactionIndeces.add(reaction.getIndex());
				}
			} else {
				// TODO: Collect guards also for custom reaction rate
			}
		}
		return guardReactionIndecesMap;
	}

// Main functions

	@Override
	protected double[] getCurrentReactionRates(Double[] magnitudes) {
		double[] reactionRates = new double[reactionCount];
		// before we evaluate reaction rate expression we check whether all guards are non-zero
		boolean[] reactionGuardNotPresentFlags = new boolean[reactionCount];
		for (int speciesIndex = 0; speciesIndex < speciesCount; speciesIndex++) {
			if (magnitudes[speciesIndex].equals(new Double(0))) {
				final Collection<Integer> reactionIndeces = guardReactionIndexMap.get(reversedMagnitudeIndexConversionMap.get(speciesIndex));
				if (reactionIndeces != null) {
					for (final Integer reactionIndex : reactionIndeces) {
						reactionGuardNotPresentFlags[reactionIndex] = true;
					}
				}
			}
		}
		for (FunctionEvaluatorWrapper<AcReaction, Double, Double> wrappedReaction : wrappedReactions) {
			final int reactionIndex = wrappedReaction.getFunctionHolder().getIndex();
			if (!reactionGuardNotPresentFlags[reactionIndex]) {
				reactionRates[reactionIndex] = wrappedReaction.getFunctionEvaluator().evaluate(magnitudes);
			}
		}
		return reactionRates;
	}
}