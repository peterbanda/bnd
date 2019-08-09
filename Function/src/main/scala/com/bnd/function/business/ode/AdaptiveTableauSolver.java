package com.bnd.function.business.ode;

import com.bnd.core.runnable.TimeStepUndefinedException;
import com.bnd.function.BndFunctionException;
import com.bnd.function.evaluator.FunctionEvaluator;

/**
 * @author © Drew Blount
 * @since 2013
 */
class AdaptiveTableauSolver extends TableauSolver {

	private static final double MAX_TIME_STEP = 10;
	private static final double MIN_TIME_STEP = 0.00001;

	private final Double[] estweights; // The weights used to make the estimation
	private final Double[] errweights; // The weights used to estimate the error
	private final Double tolerance;    // A parameter regarding time step updates

	protected AdaptiveTableauSolver(
		FunctionEvaluator<Double, Double[]> diffEquationsEvaluator,
		Double timeStep,
		Double[][] coefficients,
		Double[] estweights, 
		Double[] errweights,
		Double tolerance
	) {
		super(diffEquationsEvaluator, timeStep, coefficients);
		this.estweights = estweights;
		this.errweights = errweights;
		this.tolerance = tolerance;
		if (tolerance == null) throw 
			new BndFunctionException("Tolerance is mandatory for adaptive solvers.");
	}

	@Override
	protected Double[] weightDiffs(
		Double[][] diffs,
		Double enforcedTimeStep
	) {
		Double[][] diffs2 = diffs.clone();
		int order = estweights.length;
		
		// Produces the main estimate
		for (int i = 0; i < order; i++) {
			mulByConst(diffs[i], estweights[i]);
		}
		final Double[] answer = addVectors(diffs);
		
		// Making this third set of weights reduces rounding errors
		// when computing the estimation error
		Double[] weightDifference = new Double[order];
		for (int i = 0; i < order; i++) {
			weightDifference[i] = estweights[i] - errweights[i];
		}
		for (int i = 0; i < order; i++) {
			mulByConst(diffs2[i], weightDifference[i]);
		}
		final Double[] errors = addVectors(diffs2);

		// Finds the total error
		double error = 0d;
		for(int i = 0; i < errors.length; i++) {
			error += errors[i] * errors[i];
		}
		error = Math.sqrt(error);

		// Updates the time step according to the Step Control section of
		// http://reference.wolfram.com/mathematica/tutorial/NDSolveExplicitRungeKutta.html
		double p = order - 1;
		// Note: p is one greater than the smaller order of the two methods,
		// in all of our cases this equals length - 1.
		// TOLERANCE is the error threshold for step-size adjustment. If the discrepancy
		// between the two estimates is greater than tolerance, the step-size is decreased;
		// lesser, increased.
		double calcTimeStep = getTimeStep() * Math.pow((tolerance/error), 1/p);

		if (calcTimeStep == Double.NaN) {
			throw new TimeStepUndefinedException("Time step " + calcTimeStep + " is ill-defined.");
		}

		if (calcTimeStep < MIN_TIME_STEP)
			setTimeStep(MIN_TIME_STEP);
		else
			setTimeStep(Math.min(MAX_TIME_STEP, calcTimeStep));

		return answer;
	}
}