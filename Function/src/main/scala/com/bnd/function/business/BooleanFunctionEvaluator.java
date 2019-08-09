package com.bnd.function.business;

import java.util.Arrays;

/**
 * @author © Peter Banda
 * @since 2012  
 */
abstract class BooleanFunctionEvaluator extends AbstractFunctionEvaluator<Boolean, Boolean> {

	@Override
	public Boolean evaluate(Boolean[] inputs) {
		return evaluate(Arrays.asList(inputs));
	}
}