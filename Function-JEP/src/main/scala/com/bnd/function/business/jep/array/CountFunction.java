package com.bnd.function.business.jep.array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Count function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2014  
 */
public class CountFunction extends PostfixMathCommand {

	public static final String TAG = "count";

	public CountFunction() {
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
		Collection<Number> values = new ArrayList<Number>();
	    for (int i = 0; i < curNumberOfParameters - 1; ++i) {
	    	values.add(stack.pop());
	    }
	    final Number element = stack.pop();
		int count = 0;
		for (Number el : values)
			if (el.equals(element)) count++;

		return new Double(count);
	}

	public double runVector(Stack stack) throws ParseException {         
		// Check if stack is null
		if (stack == null) {
			throw new ParseException("Stack argument null");
	    }

		final Object[] array = (Object[]) stack.pop();
		final Number element = (Number) stack.pop();
	    return countRecursively(array, element);
	}

	private int countRecursively(Object[] array, Number matchElement) {
		int count = 0;
		for (Object element : array) {
			if (element.getClass().isArray())
				count += countRecursively((Object[]) element, matchElement);
			else
				if (matchElement.equals(element)) count++;
		}
		return count;
	}
}