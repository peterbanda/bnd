package com.bnd.function.evaluator;

import java.io.Serializable;
import java.util.Map;

import com.bnd.function.domain.Function;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public interface FunctionEvaluatorFactory {

	<IN, OUT> FunctionEvaluator<IN, OUT> createInstance(
		Function<IN, OUT> function,
		Map<Integer, Integer> variableIndexConversionMap);

	<IN, OUT> FunctionEvaluator<IN, OUT> createInstance(
			Function<IN, OUT> function);
}