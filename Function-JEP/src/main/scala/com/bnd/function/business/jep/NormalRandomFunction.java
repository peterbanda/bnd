package com.bnd.function.business.jep;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import com.bnd.core.util.RandomUtil;

/**
 * Gaussian random function (Normal distribution) defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2012  
 */
public class NormalRandomFunction extends PostfixMathCommand {

	public static final String TAG = "randomN";

	public NormalRandomFunction() {
		numberOfParameters = 2;
	}

	/**
	 * Generated the random number from given interval
	 */
	@SuppressWarnings("unchecked")
	public void run(Stack stack) throws ParseException {
		checkStack(stack);// check the stack
		final Double stdDeviation = (Double) stack.pop();
		final Double mean = (Double) stack.pop();
		stack.push(RandomUtil.nextNormal(mean, stdDeviation));
	}
}