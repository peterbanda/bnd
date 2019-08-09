package com.bnd.function.business.jep.array;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Max function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2012  
 */
public class MaxFunction extends PostfixMathCommand {

	public static final String TAG = "max";

	public MaxFunction() {
		numberOfParameters = -1;
	}

	/**
	 * Calculates the result of summing up all parameters, which are assumed
	 * to be of the Double type.
	 */
	// Optimize me
	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (null == stack) {
			throw new ParseException("Stack argument null");
	    }

		Object  param = stack.peek();
		double result;
	    if (param instanceof Number) {
	    	result = runScalar(stack);
	    } else {
	    	result = runVector(stack);
	    }
	    // push the result on the inStack
	    stack.push(new Double(result));
	}

	public double runScalar(Stack<Number> stack) throws ParseException {         
		double result = stack.pop().doubleValue();
		Number param;

	    for (int i = 1; i < curNumberOfParameters; ++i) {
	    	param = stack.pop();
	        result = Math.max(param.doubleValue(), result);
	    }
	    return result;
	}

	public double runVector(Stack<Object[]> stack) throws ParseException {         
		if (curNumberOfParameters > 1) {
			throw new ParseException("Vector version of max function expects only one parameter.");
		}
		return getMaxRecursively(stack.pop());
	}

	private Double getMaxRecursively(Object[] array) {
		double max = Double.MIN_VALUE;
		for (Object element : array) {
			if (element.getClass().isArray()) {
				 double subMax = getMaxRecursively((Object[]) element);
				 if (subMax > max) max = subMax;
			} else {
				double value = ((Number) element).doubleValue();
				if (value > max) max = value;
			}
		}
		return max;
	}
}