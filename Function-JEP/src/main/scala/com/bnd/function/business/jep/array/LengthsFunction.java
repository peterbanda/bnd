package com.bnd.function.business.jep.array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 'Length' function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2014
 */
public class LengthsFunction extends PostfixMathCommand {

	public static final String TAG = "lengths";

	public LengthsFunction() {
		numberOfParameters = 1;
	}

	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (null == stack) {
			throw new ParseException("Stack argument null");
	    }

		Object param = stack.pop();
		List<Double> result;
	    if (param.getClass().isArray()) {
	    	result = getLengthRecursively((Object[]) param);
	    } else {
	    	result = Collections.singletonList(0d);
	    }
	    // push the result on the inStack
	    stack.push(result.toArray(new Double[0]));
	}

	private List<Double> getLengthRecursively(Object[] array) {
		double length = (double) array.length;
		List<Double> lengths = new ArrayList<Double>();
		lengths.add(length);
		if ((length > 0) && (array[0].getClass().isArray()))
			lengths.addAll(getLengthRecursively((Object[]) array[0]));

		return lengths;
	}
}