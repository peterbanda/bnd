package com.bnd.function.business.jep.array;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 'Last' function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2013  
 */
public class LastPositiveFunction extends PostfixMathCommand {

	public static final String TAG = "lastPositive";

	public LastPositiveFunction() {
		numberOfParameters = -1;
	}

	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (null == stack) {
			throw new ParseException("Stack argument null");
	    }

		Object param = stack.peek();
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
		int index = 0;
	    for (; index < curNumberOfParameters - 1; ++index) {
	    	if (stack.pop().doubleValue() <= 0d) {
	    		break;
	    	}
	    }
	    return index - 1;
	}

	public double runVector(Stack<Number[]> stack) throws ParseException {         
		if (curNumberOfParameters > 1) {
			throw new ParseException("Vector version of last non zero function expects only one parameter.");
		}
		final Number[] array = stack.pop();
		int index = 0;
	    for (; index < array.length; ++index) {
	    	if (array[index].doubleValue() <= 0d) {
	    		break;
	    	}
	    }
	    return index - 1;
	}
}