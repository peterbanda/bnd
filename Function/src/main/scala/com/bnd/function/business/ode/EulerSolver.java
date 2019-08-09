package com.bnd.function.business.ode;

import com.bnd.function.evaluator.FunctionEvaluator;

/**
 * @author © Peter Banda
 * @since 2012  
 */
class EulerSolver extends AbstractODESolver {

	protected EulerSolver(
		FunctionEvaluator<Double, Double[]> diffEquationsEvaluator,
		Double timeStep
	) {
		super(diffEquationsEvaluator, timeStep);
	}

	@Override
	public Double[] getApproxDiffs(
		final Double[] currentValues,
		final Double enforcedTimeStep
	) {
		final Double[] diffs = getDerivations(currentValues);
		if (getTimeStep() != 1d) { 
			mulByConst(diffs, enforcedTimeStep);
		}
		return diffs;
	}
}