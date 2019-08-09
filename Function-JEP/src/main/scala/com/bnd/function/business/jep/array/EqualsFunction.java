package com.bnd.function.business.jep.array;

import java.util.Arrays;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 'Equals' function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2014
 */
public class EqualsFunction extends PostfixMathCommand {

	public static final String TAG = "equals";
	
	public EqualsFunction() {
		numberOfParameters = 2;
	}

	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (null == stack) {
			throw new ParseException("Stack argument null");
	    }

		Object param = stack.peek();
		double result;
	    if (param.getClass().isArray()) {
	    	result = Arrays.equals((Object[]) stack.pop(), (Object[]) stack.pop()) ? 1d : 0d;
	    } else {
	    	result = stack.pop().equals(stack.pop()) ? 1d : 0d;
	    }
	    // push the result on the inStack
	    stack.push(new Double(result));
	}
}