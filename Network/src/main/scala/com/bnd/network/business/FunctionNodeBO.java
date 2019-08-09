package com.bnd.network.business;

import java.util.List;

import com.bnd.core.BndRuntimeException;
import com.bnd.core.util.ObjectUtil;
import com.bnd.function.evaluator.FunctionEvaluator;
import com.bnd.network.BndNetworkException;
import com.bnd.network.business.integrator.JavaStatesWeightsIntegrator;
import com.bnd.network.domain.TopologicalNode;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class FunctionNodeBO<T> extends NodeBO<T> {

	private final FunctionEvaluator<T, T> functionEvaluator;
	private final JavaStatesWeightsIntegrator<T> statesWeightsIntegrator;

	public FunctionNodeBO(
		TopologicalNode topologicalNode,
		boolean immutableState,
		FunctionEvaluator<T, T> functionEvaluator
	) {
		this(topologicalNode, immutableState, functionEvaluator, null);
	}

	public FunctionNodeBO(
		TopologicalNode topologicalNode,
		boolean immutableState,
		FunctionEvaluator<T, T> functionEvaluator,
		JavaStatesWeightsIntegrator<T> statesWeightsIntegrator
	) {
		super(topologicalNode, immutableState);
		this.functionEvaluator = functionEvaluator;
		this.statesWeightsIntegrator = statesWeightsIntegrator;

		if (functionEvaluator == null && statesWeightsIntegrator == null) {
			throw new BndNetworkException("Function evaluator or states weights integrator expected for FunctionNodeBO.");
		}

		// checkFunctionEvaluatorArity();
	}

	private void checkFunctionEvaluatorArity() {
		final Integer functionArity = functionEvaluator.getArity();
		int expectedFunctionArity = getInEdgesNum();
		if (statesWeightsIntegrator != null) {
			expectedFunctionArity = statesWeightsIntegrator.getOutputArity(getInEdgesNum());
		}
		if (functionArity != null) {
			if (!ObjectUtil.areObjectsEqual(expectedFunctionArity, functionArity)) {
				throw new BndNetworkException("Node '" + getId() + "' -  with the expected arity '" + expectedFunctionArity + "' differs from the arity of associated function: " + functionArity);
			}
		}
	}

	protected FunctionEvaluator<T, T> getFunctionEvaluator() {
		return functionEvaluator;
	}

	@Override
	protected T calcNewState() {
		if (!hasInEdges()) {
			return null;
		}
		final List<T> inNodeStates = getInNodeStates();
		List<T> functionInputs = inNodeStates;
		if (statesWeightsIntegrator != null) {
			functionInputs = (List<T>) statesWeightsIntegrator.integrate(inNodeStates, getInNodesWeights());
		}

		if (functionEvaluator != null) {
			return functionEvaluator.evaluate(functionInputs);
		}

		if (functionInputs.size() != 1) {
			throw new BndNetworkException("Since a function evaluator has not been provided to FunctionNodeBO, the states weights integrator is expected to have output arity of one, but has '" + functionInputs.size() + "'.");
		}
		return functionInputs.get(0);
	}

	@Override
	public void setWeight(TopologicalNode start, T value) {
		throw new BndRuntimeException("setWeight not supported.");
	}

	@Override
	public T getWeight(TopologicalNode start) {
		throw new BndRuntimeException("setWeight not supported.");
	}
}