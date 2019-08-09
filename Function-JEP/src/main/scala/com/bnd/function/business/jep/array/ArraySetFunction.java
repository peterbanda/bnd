package com.bnd.function.business.jep.array;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Array set function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2014 
 */
public class ArraySetFunction extends PostfixMathCommand {

	public static final String TAG = "set";

	public ArraySetFunction() {
		numberOfParameters = -1;
	}

	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (stack == null) {
			throw new ParseException("Stack argument null");
	    }

		Double value = (Double) stack.pop();

		List<Integer> indeces = new ArrayList<Integer>();
	    for (int i = 1; i < curNumberOfParameters - 2; ++i) {
	    	indeces.add(((Number) stack.pop()).intValue());
	    }
	    int lastIndex = ((Number) stack.pop()).intValue();
	    Object arrayG = stack.pop();
	    if (arrayG.getClass().isArray()) {
	    	Object[] array = (Object[]) arrayG;

	    	for (int index : indeces) {
	    		array = (Object[]) array[index];
	    	}

	    	array[lastIndex] = value; 
	    }
	    stack.push(new Object());
	}
}