package com.bnd.chemistry.business;

import com.bnd.function.business.ExpressionSupportedFunctionEvaluatorFactoryImpl;
import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;
import com.bnd.function.enumerator.ListEnumeratorFactoryImpl;
import com.bnd.function.evaluator.FunctionEvaluator;
import com.bnd.function.evaluator.FunctionEvaluatorFactory;

import java.util.Map;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class FunctionEvaluatorWrapper<T extends FunctionHolder<IN, OUT>, IN, OUT> {

	private final FunctionEvaluatorFactory functionEvaluatorFactory = new ExpressionSupportedFunctionEvaluatorFactoryImpl(
			new ListEnumeratorFactoryImpl());

	private final T functionHolder;
	private final FunctionEvaluator<IN, OUT> functionEvaluator;

	public FunctionEvaluatorWrapper(T functionHolder) {
		this(functionHolder, functionHolder.getFunction()); 
	}

	public FunctionEvaluatorWrapper(T functionHolder, Map<Integer, Integer> variableIndexConversionMap) {
		this(functionHolder, functionHolder.getFunction(), variableIndexConversionMap); 
	}

	public FunctionEvaluatorWrapper(T functionHolder, Function<IN, OUT> function) {
		this.functionHolder = functionHolder;
		this.functionEvaluator = functionEvaluatorFactory.createInstance(function); 
	}

	public FunctionEvaluatorWrapper(T functionHolder, Function<IN, OUT> function, Map<Integer, Integer> variableIndexConversionMap) {
		this.functionHolder = functionHolder;
		this.functionEvaluator = functionEvaluatorFactory.createInstance(function, variableIndexConversionMap); 
	}

	public T getFunctionHolder() {
		return functionHolder;
	}

	public FunctionEvaluator<IN, OUT> getFunctionEvaluator() {
		return functionEvaluator;
	}
}