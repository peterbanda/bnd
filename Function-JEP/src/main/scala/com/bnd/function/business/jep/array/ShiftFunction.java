package com.bnd.function.business.jep.array;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * Array shift function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2014  
 */
public class ShiftFunction extends PostfixMathCommand {

	public static final String TAG = "shift";
	
	public ShiftFunction() {
		numberOfParameters = 2;
	}

	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (null == stack) {
			throw new ParseException("Stack argument null");
	    }

		int shift = ((Number) stack.pop()).intValue();
		Object param = stack.pop();
	    if (param instanceof Number) {
	    	// ??
	    } else {
	    	shiftRecursively((Object[]) param, -shift);
	    }
	    stack.push(new Object());
	}

	private void shiftRecursively(Object[] array, int shift) {
		if (array[0].getClass().isArray())
			for (Object element : array) 
				shiftRecursively((Object[]) element, shift);
		else {
			int size = array.length;
			if (shift < 1)
				shift = size + shift;
			Object[] oldArray = array.clone();
			for (int i = 0; i < size; i++)
				array[i] = oldArray[(i + shift) % size];
		}
	}
}