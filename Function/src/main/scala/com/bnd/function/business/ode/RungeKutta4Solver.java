package com.bnd.function.business.ode;

import com.bnd.function.evaluator.FunctionEvaluator;

/**
 * @author © Peter Banda
 * @since 2012  
 */
@Deprecated
class RungeKutta4Solver extends AbstractODESolver {

	private static final double[] NODES = {0, 0.5, 0.5, 1};
	
	protected RungeKutta4Solver(
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
		final int order = NODES.length;
		Double[][] diffs = new Double[order][];

		diffs[0] = getDerivations(currentValues);
		for (int i = 1; i < order; i++) {
			final Double[] values = addVectors(currentValues, diffs[i-1], NODES[i] * enforcedTimeStep);
			diffs[i] = getDerivations(values).clone();
		}
		mulByConst(diffs[1], 2);
		mulByConst(diffs[2], 2);
		final Double[] finalDiffs = addVectors(diffs);
		mulByConst(finalDiffs, enforcedTimeStep / 6);
		return finalDiffs;
	}
}