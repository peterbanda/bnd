package com.bnd.function.business;

import java.util.Map;

import com.bnd.function.evaluator.FunctionEvaluator;

public class ConstantFunctionEvaluator<IN, OUT> implements FunctionEvaluator<IN, OUT> {

	private final OUT constant;

	public ConstantFunctionEvaluator(OUT constant) {
		this.constant = constant;
	}

	@Override
	public OUT evaluate(IN[] inputs) {
		return constant;
	}

	@Override
	public OUT evaluate(Iterable<IN> inputs) {
		return constant;
	}

	@Override
	public OUT evaluate(Map<Integer, IN> environment) {
		return constant;
	}

	@Override
	public Integer getArity() {
		return 0;
	}
}