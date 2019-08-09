package com.bnd.function.business.ode;

import com.bnd.function.evaluator.FunctionEvaluator;

abstract class TableauSolver extends AbstractODESolver{

	private final Double[][] coefficients;

	protected TableauSolver(
		FunctionEvaluator<Double, Double[]> diffEquationsEvaluator,
		Double timeStep, Double[][] coefficients
	) {
		super(diffEquationsEvaluator, timeStep);
		this.coefficients = coefficients;
	}

	@Override
	public Double[] getApproxDiffs(final Double[] currentValues) {
		return getApproxDiffs(currentValues, null);
	}

	@Override
	public Double[] getApproxDiffs(
		final Double[] currentValues,
		final Double enforcedTimeStep
	) {
		double timeStep = enforcedTimeStep != null ? enforcedTimeStep : getTimeStep();
		// http://en.wikipedia.org/wiki/Runge-Kutta_method
		final int order = coefficients.length;
		Double[][] diffs = new Double[order][];

		// finds all ks by method in Wikipedia
		for (int i = 0; i < order; i++) {
			Double[] values = currentValues.clone();
			for (int j = 0; j < i; j++) {
				addToVector(values, diffs[j], coefficients[i][j]);
			}
			diffs[i] = getDerivations(values).clone();
			mulByConst(diffs[i], timeStep);
		}

		return weightDiffs(diffs, enforcedTimeStep);
	}
	
	protected abstract Double[] weightDiffs(
		Double[][] diffs,
		Double enforcedTimeStep
	);
}