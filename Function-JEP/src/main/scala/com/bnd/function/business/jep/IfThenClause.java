package com.bnd.function.business.jep;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * IF/Then function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2012  
 */
public class IfThenClause extends PostfixMathCommand {

	public static final String TAG = "if";

	public IfThenClause() {
		numberOfParameters = 3;
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

		checkStack(stack);

	    Object secondValue = stack.pop();
	    Object firstValue = stack.pop();
		Object conditionValue = stack.pop();

		boolean condition = false;
	    if (conditionValue instanceof Number) {
			condition = ((Number) conditionValue).doubleValue() == 1.0;
	    } else {
	    	throw new ParseException("Invalid parameter type");
	    }

	    if (condition) {
	    	stack.push(firstValue);
	    } else {
	    	stack.push(secondValue);
	    }
	}
}