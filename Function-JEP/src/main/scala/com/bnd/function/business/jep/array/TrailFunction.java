package com.bnd.function.business.jep.array;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * The maze JEP constructor.
 *
 * @author © Peter Banda
 * @since 2014
 */
public class TrailFunction extends PostfixMathCommand implements TrailFunctionConsts, TrailFunctionConsts2 {

	public static final String TAG = "trail";

	public TrailFunction() {
		numberOfParameters = 1;
	}

	public void run(Stack stack) throws ParseException {         
		// Check if stack is null
		if (stack == null) {
			throw new ParseException("Stack argument null");
	    }

	    int trailId = ((Number) stack.pop()).intValue();

	    if (trailId > 0 && trailId < 21) { 
	    	final Double[][] trail = createNewDoubleTrail((trailId < 14) ? TRAILS[trailId - 1] : TRAILS2[trailId - 14]);
	    	stack.push(trail);
	    } else throw new ParseException("A legal trail id must be > 0 and < 21");
	}

	private Double[][] createNewDoubleTrail(int[][] trailTemplate) {
		final int height = trailTemplate.length;
		Double[][] doubles = new Double[height][];
		for (int i = 0; i < height; i++) {
			final int width = trailTemplate[i].length;
			doubles[i] = new Double[width];
			for (int j = 0; j < width; j++) {
				doubles[i][j] = (double) trailTemplate[i][j];
			}
		}
		return doubles;
	}
}