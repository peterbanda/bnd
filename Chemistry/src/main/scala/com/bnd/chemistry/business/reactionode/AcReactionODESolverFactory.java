package com.bnd.chemistry.business.reactionode;

import java.util.Collection;
import java.util.Map;

import com.bnd.chemistry.domain.AcParameter;
import com.bnd.chemistry.domain.AcReaction;
import com.bnd.chemistry.domain.AcSpeciesAssociationType;
import com.bnd.chemistry.domain.AcSpeciesReactionAssociation;
import com.bnd.core.dynamics.ODESolver;
import com.bnd.function.business.ode.ODESolverFactory;
import com.bnd.function.domain.ODESolverType;
import com.bnd.function.evaluator.FunctionEvaluator;

public class AcReactionODESolverFactory {

	private AcReactionODESolverFactory() {
		// no-op
	}

	public static ODESolver createInstance(
		ODESolverType solverType,
		double timeStep,
		Double tolerance,
		Collection<AcReaction> reactions,
		int speciesCount,
		Collection<AcParameter> params,
		Map<Integer, Integer> magnitudeIndexConversionMap
	) {
		FunctionEvaluator<Double, Double[]> speciesDiffEvaluator = null;

		if (isBinaryStoichiometry(reactions))
			if (hasRateFunction(reactions)) 
				speciesDiffEvaluator = new AcBinaryStoichiometryRateFunctionDiffBO(reactions, speciesCount, params, magnitudeIndexConversionMap);
			else 
				speciesDiffEvaluator = new AcBinaryStoichiometryMichaelisMentenDiffBO(reactions, speciesCount, magnitudeIndexConversionMap);
		else
			if (hasRateFunction(reactions))
				speciesDiffEvaluator = new AcStoichiometricMatrixRateFunctionDiffBO(reactions, speciesCount, params, magnitudeIndexConversionMap);
			else
				speciesDiffEvaluator = new AcStoichiometricMatrixMichaelisMentenDiffBO(reactions, speciesCount, magnitudeIndexConversionMap);

		return ODESolverFactory.createInstance(speciesDiffEvaluator, solverType, timeStep, tolerance);
	}

	private static boolean isBinaryStoichiometry(Collection<AcReaction> reactions) {
		for (AcReaction reaction : reactions) {
			for (AcSpeciesReactionAssociation assoc : reaction.getSpeciesAssociations(AcSpeciesAssociationType.Reactant)) {
				if (!assoc.getStoichiometricFactor().equals(1d)) {
					return false;
				}
			}
			for (AcSpeciesReactionAssociation assoc : reaction.getSpeciesAssociations(AcSpeciesAssociationType.Product)) {
				if (!assoc.getStoichiometricFactor().equals(1d)) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean hasRateFunction(Collection<AcReaction> reactions) {
		for (AcReaction reaction : reactions)
			if (reaction.getForwardRateFunction() != null || reaction.getReverseRateFunction() != null)
				return true;
		return false;
	}
}