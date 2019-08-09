package com.bnd.function.business.jep.array;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Array sum function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2014  
 */
public class SumFunction extends PostfixMathCommand {

	public static final String TAG = "sum";
	
	public SumFunction() {
		numberOfParameters = -1;
	}

	/**
	 * Calculates the result by averaging all parameters, which are assumed
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
		double sum = 0;
	    for (int i = 0; i < curNumberOfParameters; ++i) {
	    	sum += stack.pop().doubleValue();
	    }
	    return sum / curNumberOfParameters;
	}

	public double runVector(Stack<Object[]> stack) throws ParseException {         
		if (curNumberOfParameters > 1) {
			throw new ParseException("Vector version of sum function expects only one parameter.");
		}
		return sumRecursively(stack.pop());
	}

	private double sumRecursively(Object[] array) {
		double sum = 0;
		for (Object element : array) {
			if (element.getClass().isArray()) 
				sum += sumRecursively((Object[]) element);
			else
				sum += ((Number) element).doubleValue();
			
		}
		return sum;
	}
}