package com.bnd.function.business.jep.array;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Projection function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2012  
 */
public class ArrayGetFunction extends PostfixMathCommand {

	public static final String TAG = "get";

	public ArrayGetFunction() {
		numberOfParameters = -1;
	}

	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (stack == null) {
			throw new ParseException("Stack argument null");
	    }

		List<Integer> indeces = new ArrayList<Integer>();
	    for (int i = 0; i < curNumberOfParameters - 2; ++i) {
	    	indeces.add(((Number) stack.pop()).intValue());
	    }
	    int lastIndex = ((Number) stack.pop()).intValue();
	    Object arrayG = stack.pop();
	    if (arrayG.getClass().isArray()) {
	    	Object[] array = (Object[]) arrayG;

	    	boolean error = false;
			for (int index : indeces) {
				if (index >= array.length) {
					error = true;
					break;
				}
				array = (Object[]) array[index];
			}

			if (!error) {
				if (lastIndex < array.length)
					stack.push(array[lastIndex]);
				else 
					stack.push(0d);
			} else
				stack.push(0d);
	    } else
	    	stack.push(0d);
	}
}