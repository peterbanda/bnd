package com.bnd.core.dynamics;

public interface ODESolver {

	Double[] getApproxDiffs(final Double[] currentValues);

	Double[] getApproxDiffs(final Double[] currentValues, final Double enforcedTimeStep);

	double getTimeStep();
}