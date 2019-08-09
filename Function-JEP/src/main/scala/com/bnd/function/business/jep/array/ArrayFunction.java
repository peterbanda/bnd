package com.bnd.function.business.jep.array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * The array JEP constructor.
 *
 * @author © Peter Banda
 * @since 2014
 */
public class ArrayFunction extends PostfixMathCommand {

	public static final String TAG = "array";
	
	public ArrayFunction() {
		numberOfParameters = -1;
	}

	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (stack == null) {
			throw new ParseException("Stack argument null");
	    }

		List<Integer> sizes = new ArrayList<Integer>();
	    for (int i = 0; i < curNumberOfParameters; ++i) {
	    	sizes.add(((Number) stack.pop()).intValue());
	    }

	    Object[] array;
	    if (sizes.size() == 1) {
	    	array = new Double[sizes.get(0)];
			for (int i = 0; i < sizes.get(0); i++) {
				array[i] = 0d;
			}
	    } else {
	    	array = new Object[sizes.get(0)];
		    Collection<Object[]> arrays = Collections.singleton(array);
		    for (int i = 1; i < sizes.size() - 1; i++) {
		    	arrays = init(arrays, sizes.get(i));
		    }
		    initDouble(arrays, sizes.get(sizes.size() - 1));
	    }

	    stack.push(array);
	}

	private Collection<Object[]> init(Collection<Object[]> arrays, int size) {
		Collection<Object[]> newArrays = new ArrayList<Object[]>();
		for (Object[] array : arrays) {
			for (int i = 0; i < array.length; i++) {
				array[i] = new Object[size];
				newArrays.add((Object[]) array[i]);
			}
		}
		return newArrays;
	}

	private void initDouble(Collection<Object[]> arrays, int size) {
		for (Object[] array : arrays) {
			for (int i = 0; i < array.length; i++) {
				array[i] = new Double[size];
				Double[] doubleArray = (Double[]) array[i];
				for (int j = 0; j < size; j++) {
					doubleArray[j] = 0d;
				}
			}
		}
	}
}