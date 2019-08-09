package com.bnd.function.evaluator;

import java.util.Map;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public interface FunctionEvaluator<IN, OUT> {

	Integer getArity();

	OUT evaluate(IN[] inputs);

	OUT evaluate(Iterable<IN> inputs);

	OUT evaluate(Map<Integer, IN> environment);
}