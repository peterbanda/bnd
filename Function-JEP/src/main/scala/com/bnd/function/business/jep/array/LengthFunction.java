package com.bnd.function.business.jep.array;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 'Length' function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2014
 */
public class LengthFunction extends PostfixMathCommand {

	public static final String TAG = "length";
	
	public LengthFunction() {
		numberOfParameters = 1;
	}

	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (null == stack) {
			throw new ParseException("Stack argument null");
	    }

		Object param = stack.pop();
		double result;
	    if (param.getClass().isArray()) {
	    	result = calcLengthRecursively((Object[]) param);
	    } else {
	    	result = 0d;
	    }
	    // push the result on the inStack
	    stack.push(new Double(result));
	}

	private int calcLengthRecursively(Object[] array) {
		int length = array.length;
		if ((length > 0) && (array[0].getClass().isArray()))
			length *= calcLengthRecursively((Object[]) array[0]);

		return length;
	}
}