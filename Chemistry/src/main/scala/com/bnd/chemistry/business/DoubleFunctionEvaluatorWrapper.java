package com.bnd.chemistry.business;

import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;

import java.util.Map;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class DoubleFunctionEvaluatorWrapper<T extends FunctionHolder<Double, Double>> extends FunctionEvaluatorWrapper<T, Double, Double> {

	public DoubleFunctionEvaluatorWrapper(T functionHolder) {
		super(functionHolder);
	}

	public DoubleFunctionEvaluatorWrapper(T functionHolder, Map<Integer, Integer> variableIndexConversionMap) {
		super(functionHolder, variableIndexConversionMap);
	}
	
	public DoubleFunctionEvaluatorWrapper(T functionHolder, Function<Double, Double> function) {
		super(functionHolder, function);
	}

	public DoubleFunctionEvaluatorWrapper(T functionHolder, Function<Double, Double> function, Map<Integer, Integer> variableIndexConversionMap) {
		super(functionHolder, function, variableIndexConversionMap);
	}
}
