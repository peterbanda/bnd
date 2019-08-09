package com.bnd.function.business.jep.array;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Min function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2012  
 */
public class MinFunction extends PostfixMathCommand {

	public static final String TAG = "min";
	
	public MinFunction() {
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
	        result = Math.min(param.doubleValue(), result);
	    }
	    return result;
	}

	public double runVector(Stack<Number[]> stack) throws ParseException {         
		if (curNumberOfParameters > 1) {
			throw new ParseException("Vector version of min function expects only one parameter.");
		}
		return getMinRecursively(stack.pop());
	}

	private Double getMinRecursively(Object[] array) {
		double min = Double.MAX_VALUE;
		for (Object element : array) {
			if (element.getClass().isArray()) {
				 double subMin = getMinRecursively((Object[]) element);
				 if (subMin < min) min = subMin;
			} else {
				double value = ((Number) element).doubleValue();
				if (value < min) min = value;
			}
		}
		return min;
	}
}