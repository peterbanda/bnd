package com.bnd.function.business.jep.array;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 'Last' function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2012  
 */
public class LastFunction extends PostfixMathCommand {

	public static final String TAG = "last";
	
	public LastFunction() {
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
	    for (int i = 0; i < curNumberOfParameters - 1; ++i) {
	    	stack.pop();
	    }
	    return stack.pop().doubleValue();
	}

	public double runVector(Stack<Number[]> stack) throws ParseException {         
		if (curNumberOfParameters > 1) {
			throw new ParseException("Vector version of last function expects only one parameter.");
		}
		final Number[] array = stack.pop();
	    return array[array.length - 1].doubleValue();
	}
}