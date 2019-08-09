package com.bnd.function.business.ode;

import java.util.Arrays;

import com.bnd.core.dynamics.ODESolver;
import com.bnd.function.evaluator.FunctionEvaluator;

/**
 * @author © Peter Banda
 * @since 2012  
 */
abstract class AbstractODESolver implements ODESolver {

	private final FunctionEvaluator<Double, Double[]> diffEquationsEvaluator;
	private double timeStep;

	protected AbstractODESolver(
		FunctionEvaluator<Double, Double[]> diffEquationsEvaluator,
		Double timeStep
	) {
		this.diffEquationsEvaluator = diffEquationsEvaluator;
		if (timeStep == null) {
			this.timeStep = 1d;
		} else {
			this.timeStep = timeStep;
		}
	}

	protected void setTimeStep(double timeStep) {
		this.timeStep = timeStep;
	}

	@Override
	public double getTimeStep() {
		return timeStep;
	}

	protected FunctionEvaluator<Double, Double[]> getDiffEquationsEvaluator() {
		return diffEquationsEvaluator;
	}

	protected Double[] getDerivations(final Double[] values) {
		return diffEquationsEvaluator.evaluate(values);		
	}

	@Override
	public Double[] getApproxDiffs(final Double[] currentValues) {
		return getApproxDiffs(currentValues, timeStep);
	}

	protected static void mulByConst(Double[] vector, double constant) {
		final int length = vector.length;
		for (int i = 0; i < length; i++) {
			if (vector[i] != 0d) {
				vector[i] *= constant;
			}
		}
	}
	
	// This is like mulByConst but returns the product rather than changing the vector
	// ie scaleVect gives the product of a vector and a scalar.
	protected static Double[] scaleVect(Double[] vector, double scalar) {
		final int length = vector.length;
		Double[] product = new Double[length];
		for (int i = 0; i < length; i++) {
			product[i] = vector[i]*scalar;
		}
		return product;
	}
	
	/**
	 * Calculates V = vector1 + constant * vector2
	 * 
	 * @param vector1
	 * @param vector2
	 * @param constant
	 * @return
	 */
	protected static Double[] addVectors(final Double[] vector1, final Double[] vector2, final double constant) {
		final int length = Math.min(vector1.length, vector2.length);
		Double[] result = vector1.clone();
		for (int i = 0; i < length; i++) {
			if (vector2[i] != 0d) {
				result[i] += constant * vector2[i];
			}
		}
		return result;
	}

	protected static void addToVector(final Double[] vector1, final Double[] vector2, final double constant) {
		final int length = Math.min(vector1.length, vector2.length);
		for (int i = 0; i < length; i++) {
			if (vector2[i] != 0d) {
				vector1[i] += constant * vector2[i];
			}
		}
	}

	protected static Double[] addVectors(final Double[][] vectors) {
		final int length = vectors[0].length;
		Double[] result = new Double[length];
		Arrays.fill(result, 0D);
		for (final Double[] vector : vectors) {
			for (int i = 0; i < length; i++) {
				result[i] += vector[i];
			}
		}
		return result;
	}
}