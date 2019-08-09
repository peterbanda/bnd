package com.bnd.function.business.ode;

import com.bnd.core.dynamics.ODESolver;
import com.bnd.function.BndFunctionException;
import com.bnd.function.domain.ODESolverType;
import com.bnd.function.evaluator.FunctionEvaluator;

/**
 * @author © Drew Blount
 * @since 2013 
 */
public class ODESolverFactory {

	private static interface TableauCoefs {
		final Double[][] RK2 = {
				{},
				{2/3d}};
		final Double[][] RK4 = {
				{},
				{.5},
				{0d,.5},
				{0d,0d,1d}};
		final Double[][] RKCK = {
				{},
				{.25},
				{0.075, 0.225},
				{.3,-.9,1.2},
				{-11/54d, 5/2d,	-70/27d, 35/27d},
				{1631/55296d, 175/512d, 575/13824d, 44275/110592d, 253/4096d}};
		final Double[][] RKF = {
				{},
				{.25},
				{0.09375, 0.28125},
				{1932/2197d, -7200/2197d, 7296/2197d},
				{439/216d, -8d, 3680/513d, -845/4104d},
				{-8/27d, 2d, -3544/2565d, 1859/4104d, -11/40d}};
		final Double[][] RKDP = {
				{},
				{1/5d},
				{3/40d, 9/40d},
				{44/45d, -56/15d, 32/9d},
				{19372/6561d, -25360/2187d, 64448/6561d, -212/729d},
				{9017/3168d, -355/33d, 46732/5247d, 49/176d, -5103/18656d},
				{35/384d, 0d, 500/1113d, 125/192d, -2187/6784d, 11/84d}};
	}

	private static interface TableauWeights {
		final Double[] RK2 = {.25, .75};
		final Double[] RK4 = {1/6d, 1/3d, 1/3d, 1/6d};
		final Double[] RKCK4 = {2825/27648d, 0d, 18575/48384d, 13525/55296d, 277/14336d, 1/4d};
		final Double[] RKCK5 = {37/378d, 0d, 250/621d, 125/594d, 0d, 512/1771d};
		final Double[] RKF4 = {25/216d, 1408/2565d, 0d,	2197/4104d,	-1/5d, 0d};
		final Double[] RKF5 = {16/135d, 6656/12825d, 0d, 28561/56430d, -9/50d, 2/55d};
		final Double[] RKDP4 = {5179/57600d, 0d, 7571/16695d, 393/640d,	-92097/339200d,	187/2100d, 1/40d};
		final Double[] RKDP5 = {35/384d, 0d, 500/1113d, 125/192d, -2187/6784d, 11/84d, 0d};
	}

	public static ODESolver createInstance(
		FunctionEvaluator<Double, Double[]> diffEquationsEvaluator,
		ODESolverType type,
		double timeStep
	) {
		return createInstance(diffEquationsEvaluator, type, timeStep, null);
	}

	public static ODESolver createInstance(
		FunctionEvaluator<Double, Double[]> diffEquationsEvaluator,
		ODESolverType type,
		double timeStep,
		Double tolerance
	) {
		switch (type) {
			case Euler:
				return new EulerSolver(diffEquationsEvaluator, timeStep);
			case RungeKutta2:
				return new NonadaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RK2, TableauWeights.RK2);
			case RungeKutta4:
				return new RungeKutta4Solver(diffEquationsEvaluator, timeStep);
			case RungeKutta4Tableau:
				return new NonadaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RK4, TableauWeights.RK4);
			
			case RungeKuttaCashKarp:
				return new AdaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RKCK, TableauWeights.RKCK4, TableauWeights.RKCK5, tolerance);
			case RungeKuttaCashKarp4:
				return new NonadaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RKCK, TableauWeights.RKCK4);
			case RungeKuttaCashKarp5:
				return new NonadaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RKCK, TableauWeights.RKCK5);
			
			// Something to play with is which set of weights determines the estimation itself,
		    // and which set determines the error. Mathematica claims that most modern code
			// uses the higher-order for the estimation every time, but here and in RKCK I use
			// the lower-order because that was how RKCK and RKF were originally intended.
			case RungeKuttaFehlberg:
				return new AdaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RKF, TableauWeights.RKF4, TableauWeights.RKF5, tolerance);
			case RungeKuttaFehlberg4:
				return new NonadaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RKF, TableauWeights.RKF4);
			case RungeKuttaFehlberg5:
				return new NonadaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RKF, TableauWeights.RKF5);
				
			// Unlike Fehlberg and Cash-Karp, Dormand-Prince uses the higher-order estimation.
			// This is called local extrapolation and it is why RKDP5 is called before RKDP4.
			case RungeKuttaDormandPrince:
				return new AdaptiveTableauSolver(diffEquationsEvaluator, timeStep,
						TableauCoefs.RKDP, TableauWeights.RKDP5, TableauWeights.RKDP4, tolerance);
			case RungeKuttaDormandPrince4:
				return new NonadaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RKDP, TableauWeights.RKDP4);
			case RungeKuttaDormandPrince5:
				return new NonadaptiveTableauSolver(diffEquationsEvaluator, timeStep, 
						TableauCoefs.RKDP, TableauWeights.RKDP5);
				
			default: throw new BndFunctionException("ODE solver type '" + type + "' not recognized.");
		}
	}
}