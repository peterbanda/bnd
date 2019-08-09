package com.bnd.function.business;

import java.util.Arrays;
import java.util.Map;

import com.bnd.function.evaluator.FunctionEvaluator;

/**
 * @author © Peter Banda
 * @since 2012  
 */
abstract class AbstractFunctionEvaluator<IN, OUT> implements FunctionEvaluator<IN, OUT> {

	@Override
	public OUT evaluate(Iterable<IN> inputs) {
		throw new RuntimeException("FunctionEvaluator.evaluate(Iterable) not implemented!");
	}

	@Override
	public OUT evaluate(IN[] inputs) {
		return evaluate(Arrays.asList(inputs));
	}

	@Override
	public OUT evaluate(Map<Integer, IN> environment) {
		throw new RuntimeException("FunctionEvaluator.evaluate(Map) not implemented!");
	}

	@Override
	public Integer getArity() {
		return null;
	}
}