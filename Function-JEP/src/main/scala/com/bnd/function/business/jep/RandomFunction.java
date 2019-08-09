package com.bnd.function.business.jep;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import com.bnd.core.util.RandomUtil;

/**
 * Uniform random function defined for JEP tree / parser.
 *
 * @author © Peter Banda
 * @since 2012  
 */
public class RandomFunction extends PostfixMathCommand {

	public static final String TAG = "random";

	public RandomFunction() {
		numberOfParameters = 2;
	}

	/**
	 * Generated the random number from given interval
	 */
	@SuppressWarnings("unchecked")
	public void run(Stack stack) throws ParseException {
		checkStack(stack);// check the stack
		final Double to = (Double) stack.pop();
		final Double from = (Double) stack.pop();
		stack.push(RandomUtil.nextDouble(from, to));
	}
}