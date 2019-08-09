package com.bnd.function.business.ode;

import com.bnd.function.evaluator.FunctionEvaluator;

/**
 * @author © Drew Blount
 * @since 2013
 */
class NonadaptiveTableauSolver extends TableauSolver {

	private final Double[] weights;

	protected NonadaptiveTableauSolver(
		FunctionEvaluator<Double, Double[]> diffEquationsEvaluator,
		Double timeStep,
		Double[][] coefficients,
		Double[] weights
	) {
		super(diffEquationsEvaluator, timeStep, coefficients);
		this.weights = weights;
	}

	@Override
	protected Double[] weightDiffs(
		Double[][] diffs,
		Double enforcedTimeStep
	) {
		for (int i = 0; i < weights.length; i++) {
			diffs[i] = scaleVect(diffs[i], weights[i]);
		}
		return addVectors(diffs);
	}
}


