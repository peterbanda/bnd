package com.bnd.function.business;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.nfunk.jep.JEP;
import org.nfunk.jep.function.PostfixMathCommand;

import com.bnd.core.util.RandomUtil;
import com.bnd.function.BndFunctionException;
import com.bnd.function.business.jep.IfThenClause;
import com.bnd.function.business.jep.NormalRandomFunction;
import com.bnd.function.business.jep.RandomFunction;
import com.bnd.function.business.jep.array.ArrayFunction;
import com.bnd.function.business.jep.array.ArrayGetFunction;
import com.bnd.function.business.jep.array.ArraySetFunction;
import com.bnd.function.business.jep.array.AvgFunction;
import com.bnd.function.business.jep.array.CountFunction;
import com.bnd.function.business.jep.array.EqualsFunction;
import com.bnd.function.business.jep.array.FirstFunction;
import com.bnd.function.business.jep.array.LastFunction;
import com.bnd.function.business.jep.array.LastPositiveFunction;
import com.bnd.function.business.jep.array.LengthFunction;
import com.bnd.function.business.jep.array.LengthsFunction;
import com.bnd.function.business.jep.array.MaxFunction;
import com.bnd.function.business.jep.array.MiddleFunction;
import com.bnd.function.business.jep.array.MinFunction;
import com.bnd.function.business.jep.array.ShiftFunction;
import com.bnd.function.business.jep.array.SumFunction;
import com.bnd.function.business.jep.array.TrailFunction;
import com.bnd.function.domain.Expression;

/**
 * @author © Peter Banda
 * @since 2012  
 */
final public class JepExpressionEvaluator<IN, OUT> extends ExpressionEvaluator<IN, OUT> {

	private final JEP jep;

	public JepExpressionEvaluator(Expression<IN, OUT> expression) {
		super(expression);
		this.jep = createJep(formula, recognizedVariableNames);
	}

	public JepExpressionEvaluator(Expression<IN, OUT> expression, Map<Integer, Integer> recognizedVariableIndexConversionMap) {
		super(expression, recognizedVariableIndexConversionMap);
		this.jep = createJep(formula, recognizedVariableNames);
	}

	//TODO: this might break things. Use only for validations
	public JepExpressionEvaluator(Expression<IN, OUT> expression, String[] recognizedVariables) {
		super(expression, recognizedVariables);
		this.jep = createJep(formula, recognizedVariableNames);
	}

	private JepExpressionEvaluator(Expression<IN, OUT> expression, JEP jep) {
		super(expression);
		this.jep = jep;
	}

	public static <IN,OUT> ExpressionEvaluator<IN, OUT> createInstance(
		Expression<IN, OUT> expression,
		Map<String, PostfixMathCommand> extraFunctionTagMap
	) {
		String[] recognizedVariableNames = createRecognizedVariableNames(expression.getReferencedVariableIndeces(), new FunctionUtility());
		final JEP jep = createJep(expression.getFormula(), recognizedVariableNames, extraFunctionTagMap);
		return new JepExpressionEvaluator<IN,OUT>(expression, jep);
	}

	private static JEP createJep(final String formula, final String[] recognizedVariables) {
		return createJep(formula, recognizedVariables, new HashMap<String, PostfixMathCommand>());
	}

	private static JEP createJep(final String formula, final String[] recognizedVariables, Map<String, PostfixMathCommand> extraFunctionTagMap) {
		JEP jep = new JEP();
		jep.addStandardConstants();
		jep.addStandardFunctions();
		jep.addFunction(ArrayFunction.TAG, new ArrayFunction());
		jep.addFunction(ArrayGetFunction.TAG, new ArrayGetFunction());
		jep.addFunction(ArraySetFunction.TAG, new ArraySetFunction());
		jep.addFunction(TrailFunction.TAG, new TrailFunction());
		jep.addFunction(SumFunction.TAG, new SumFunction());
		jep.addFunction(AvgFunction.TAG, new AvgFunction());
		jep.addFunction(LengthFunction.TAG, new LengthFunction());
		jep.addFunction(LengthsFunction.TAG, new LengthsFunction());
		jep.addFunction(CountFunction.TAG, new CountFunction());
		jep.addFunction(ShiftFunction.TAG, new ShiftFunction());
		jep.addFunction(MinFunction.TAG, new MinFunction());
		jep.addFunction(MaxFunction.TAG, new MaxFunction());
		jep.addFunction(FirstFunction.TAG, new FirstFunction());
		jep.addFunction(LastFunction.TAG, new LastFunction());
		jep.addFunction(EqualsFunction.TAG, new EqualsFunction());
		jep.addFunction(MiddleFunction.TAG, new MiddleFunction());
		jep.addFunction(IfThenClause.TAG, new IfThenClause());
		jep.addFunction(RandomFunction.TAG, new RandomFunction());
		jep.addFunction(NormalRandomFunction.TAG, new NormalRandomFunction());
		jep.addFunction(LastPositiveFunction.TAG, new LastPositiveFunction());

		for (Entry<String, PostfixMathCommand> tagFunction : extraFunctionTagMap.entrySet()) {
			jep.addFunction(tagFunction.getKey(), tagFunction.getValue());
		}

		for (String recognizedVariable : recognizedVariables) {
			jep.addVariable(recognizedVariable, 0);
		}
		jep.parseExpression(formula);
		return jep;
//		jep.getErrorInfo();
	}	

	// warning: for performance reason no validation is done in evaluate functions!
	@Override
	public OUT evaluate(final IN[] values) {
		for (int i = 0; i < recognizedVariablesNum; i++) {
			jep.addVariableAsObject(recognizedVariableNames[i], values[associatedVariableConvertedIndeces[i]]);				
		}
		return getAndCheckResult();
	}

	protected OUT evaluateX(final Map<String, IN> environment) {
		for (final String recognizedVariableName : recognizedVariableNames) {
			if (recognizedVariableName == null) {
				throw new BndFunctionException("Name of variable cannot be null.");
			}
			if (environment.get(recognizedVariableName) == null) {
				throw new BndFunctionException("Value for '" + recognizedVariableName + "' in environment '" + environment + "' is null.");
			}
			jep.addVariableAsObject(recognizedVariableName, environment.get(recognizedVariableName));				
		}
		return getAndCheckResult();
	}

	private OUT getAndCheckResult() {
		final OUT result = (OUT) jep.getValueAsObject();
		if (result == null) {
			throw new BndFunctionException("Expression '" + formula + "' cannot be evaluated. " + jep.getErrorInfo());
		}
		return result;
	}

	@Override
	public void validate() throws BndFunctionException {
		for (final String recognizedVariableName : recognizedVariableNames) {
			jep.addVariableAsObject(recognizedVariableName, 0d);			
		}
		final OUT result = (OUT) jep.getValueAsObject();
		if (result == null) {
			throw new BndFunctionException(jep.getErrorInfo());
		}
	}

	public static void main(String args[]) {
		int size = 10;
		Object[] array = createArray(size);
		for (int i = 0; i < size; i++)
			setArrayElement(array, i, RandomUtil.nextDouble(10));
		System.out.println("Original");
		System.out.println(Arrays.toString(array));
		shift(array,1);
		System.out.println("Moved right by 1");
		System.out.println(Arrays.toString(array));
		shift(array,-1);
		System.out.println("Moved left by 1");
		System.out.println(Arrays.toString(array));
		shift(array,-1);
		System.out.println(Arrays.toString(array));
		System.out.println("Moved left by 1");
	}

	public static void mainx(String args[]) {
		int size1 = 10;
		int size2 = 5;
		int size3 = 8;
		int size4 = 9;
		int repetitions = 10;

		System.out.println("5/8= " + div(5, 8));
		System.out.println("8/5= " + div(8, 5));
		System.out.println("-1/5= " + div(-1, 5));
		System.out.println("Sizes: " + size1 + ", " + size2 + ", " + size3 + ", " + size4);

		Object[] array = createArray(size1, size2, size3, size4);
		double length = calcArrayLength(array);
		System.out.println("Length: " + length + ", expected : " + (size1 * size2 * size3 * size4));

		double sum = 0;
		for (int i = 0; i < repetitions; i++) {
			int index1 = RandomUtil.nextInt(size1);
			int index2 = RandomUtil.nextInt(size2);
			int index3 = RandomUtil.nextInt(size3);
			int index4 = RandomUtil.nextInt(size4);
			double element = RandomUtil.nextDouble();
			sum += element;

			setArrayElement(array, index1, index2, index3, index4, element);
			System.out.println("Stored:    " + element);

			final double retrievedElement = getArrayElement(array, index1, index2, index3, index4);
			System.out.println("Retrieved: " + retrievedElement);
		}

		final double calcedSum = calcSum(array);
		final double avg = calcAvg(array);
		System.out.println("Sum: " + calcedSum + ", expected: " + sum);
		System.out.println("Avg: " + avg + ", expected: " + (calcedSum / length));
		
		Object[] trail = createTrail(0);
		double trailLength = calcArrayLength(trail);
		Double[] trailLengths = calcArrayLengths(trail);
		System.out.println("Trail Length: " + trailLength);
		System.out.println("Trail Lengths: " + trailLengths[0] + "," + trailLengths[1]);

		System.out.println("Trail Element at (14,4): " + getArrayElement2D(trail, 14, 4));
		
		final double calcedTrailSum = calcSum(trail);
		final double calcedTrailAvg = calcAvg(trail);
		System.out.println("Trail Sum: " + calcedTrailSum);
		System.out.println("Trail Avg: " + calcedTrailAvg);
	}

	private static Object[] createArray(int size1) {
		String formula = "array(x1)";
		Expression<Double, Object[]> expression = new Expression<Double, Object[]>(formula);
		Map<String, Double> environment = new HashMap<String, Double>();
		environment.put("x1", (double) size1);
		JepExpressionEvaluator<Double, Object[]> evaluator = new JepExpressionEvaluator<Double, Object[]>(expression);
		return evaluator.evaluateX(environment);		
	}

	private static void shift(Object[] array, int shift) {
		String formula = "shift(x1,x2)";
		Expression<Object, Double> expression = new Expression<Object, Double>(formula);
		Map<String, Object> environment = new HashMap<String, Object>();
		environment.put("x1", array);
		environment.put("x2", shift);
		JepExpressionEvaluator<Object, Double> evaluator = new JepExpressionEvaluator<Object, Double>(expression);
		evaluator.evaluateX(environment);
	}


	private static Object[] createArray(int size1, int size2, int size3, int size4) {
		String formula = "array(x1,x2,x3,x4)";
		Expression<Double, Object[]> expression = new Expression<Double, Object[]>(formula);
		Map<String, Double> environment = new HashMap<String, Double>();
		environment.put("x1", (double) size1);
		environment.put("x2", (double) size2);
		environment.put("x3", (double) size3);
		environment.put("x4", (double) size4);
		JepExpressionEvaluator<Double, Object[]> evaluator = new JepExpressionEvaluator<Double, Object[]>(expression);
		return evaluator.evaluateX(environment);
	}

	private static Double div(double x, double length) {
		String formula = "x1 % x2";
		Expression<Double, Double> expression = new Expression<Double, Double>(formula);
		Map<String, Double> environment = new HashMap<String, Double>();
		environment.put("x1", (x + length));
		environment.put("x2", length);
		JepExpressionEvaluator<Double, Double> evaluator = new JepExpressionEvaluator<Double, Double>(expression);
		return evaluator.evaluateX(environment);
	}

	private static Object[] createTrail(int id) {
		String formula = "trail(x1)";
		Expression<Double, Object[]> expression = new Expression<Double, Object[]>(formula);
		Map<String, Double> environment = new HashMap<String, Double>();
		environment.put("x1", (double) id);
		JepExpressionEvaluator<Double, Object[]> evaluator = new JepExpressionEvaluator<Double, Object[]>(expression);
		return evaluator.evaluateX(environment);
	}

	private static Double calcArrayLength(Object[] array) {
		String formula = "length(x1)";
		Expression<Object, Double> expression = new Expression<Object, Double>(formula);
		Map<String, Object> environment = new HashMap<String, Object>();
		environment.put("x1", array);
		JepExpressionEvaluator<Object, Double> evaluator = new JepExpressionEvaluator<Object, Double>(expression);
		return evaluator.evaluateX(environment);
	}

	private static Double[] calcArrayLengths(Object[] array) {
		String formula = "lengths(x1)";
		Expression<Object, Double[]> expression = new Expression<Object, Double[]>(formula);
		Map<String, Object> environment = new HashMap<String, Object>();
		environment.put("x1", array);
		JepExpressionEvaluator<Object, Double[]> evaluator = new JepExpressionEvaluator<Object, Double[]>(expression);
		return evaluator.evaluateX(environment);
	}

	private static void setArrayElement(Object[] array, int index, Double element) {
		String formula = "set(x0,x1,x2)";
		Expression<Object, Double> expression = new Expression<Object, Double>(formula);
		Map<String, Object> environment = new HashMap<String, Object>();
		environment.put("x0", array);
		environment.put("x1", index);
		environment.put("x2", element);
		JepExpressionEvaluator<Object, Double> evaluator = new JepExpressionEvaluator<Object, Double>(expression);
		evaluator.evaluateX(environment);
	}

	private static void setArrayElement(Object[] array, int index1, int index2, int index3, int index4, Double element) {
		String formula = "set(x0,x1,x2,x3,x4,x5)";
		Expression<Object, Double> expression = new Expression<Object, Double>(formula);
		Map<String, Object> environment = new HashMap<String, Object>();
		environment.put("x0", array);
		environment.put("x1", index1);
		environment.put("x2", index2);
		environment.put("x3", index3);
		environment.put("x4", index4);
		environment.put("x5", element);
		JepExpressionEvaluator<Object, Double> evaluator = new JepExpressionEvaluator<Object, Double>(expression);
		evaluator.evaluateX(environment);
	}

	private static Double getArrayElement(Object[] array, int index1, int index2, int index3, int index4) {
		String formula = "get(x0,x1,x2,x3,x4)";
		Expression<Object, Double> expression = new Expression<Object, Double>(formula);
		Map<String, Object> environment = new HashMap<String, Object>();
		environment.put("x0", array);
		environment.put("x1", index1);
		environment.put("x2", index2);
		environment.put("x3", index3);
		environment.put("x4", index4);
		JepExpressionEvaluator<Object, Double> evaluator = new JepExpressionEvaluator<Object, Double>(expression);
		return evaluator.evaluateX(environment);
	}

	private static Object getArrayElement2D(Object[] array, int index1, int index2) {
		String formula = "get(x0,x1,x2)";
		Expression<Object, Object> expression = new Expression<Object, Object>(formula);
		Map<String, Object> environment = new HashMap<String, Object>();
		environment.put("x0", array);
		environment.put("x1", index1);
		environment.put("x2", index2);
		JepExpressionEvaluator<Object, Object> evaluator = new JepExpressionEvaluator<Object, Object>(expression);
		return evaluator.evaluateX(environment);
	}

	private static Double calcSum(Object[] array) {
		String formula = "sum(x1)";
		Expression<Object, Double> expression = new Expression<Object, Double>(formula);
		Map<String, Object> environment = new HashMap<String, Object>();
		environment.put("x1", array);
		JepExpressionEvaluator<Object, Double> evaluator = new JepExpressionEvaluator<Object, Double>(expression);
		return evaluator.evaluateX(environment);
	}

	private static Double calcAvg(Object[] array) {
		String formula = "avg(x1)";
		Expression<Object, Double> expression = new Expression<Object, Double>(formula);
		Map<String, Object> environment = new HashMap<String, Object>();
		environment.put("x1", array);
		JepExpressionEvaluator<Object, Double> evaluator = new JepExpressionEvaluator<Object, Double>(expression);
		return evaluator.evaluateX(environment);
	}

	public static void mainb(String args[]) {
//		String formula = "if (1 < 2) max(min(12, x1, 11), 2^3), 5";
//		String formula = "if(1 < x1, 20, 30)";
//		String formula = "min(x1,2,3)";
//		String formula = "if(x4 == 1.1 && x5 > 3, random(2,10), 0)";
		String formula = "x1 + 3 + ssd(2 + 4)";
		Expression<Double, Double> expression = new Expression<Double, Double>(formula);
		Map<String, Double> environment = new HashMap<String, Double>();
		environment.put("x1", 2.2);
		JepExpressionEvaluator<Double, Double> jetExpressionEvaluator = new JepExpressionEvaluator<Double, Double>(expression);
		Double value = jetExpressionEvaluator.evaluateX(environment);
		System.out.println(value);
	}

	public static void maina(String args[]) {
		final double[] x3Values = new double[] {0.2, 1.5, 0.6, 1.6};
		final String[] x3ValueNames = new String[] {"0", "1", "Undefined", "Undefined"};
		final double[] x1Values = new double[] {0d, 0d, 1d, 1d};
		final double[] x2Values = new double[] {0d, 1d, 0d, 1d};

		final String[] formulas = new String[] {
			    "0.2 * (x3>0.5)",
				"0.2 * if(x1==0 && x2==0,x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if(x1==0 && x2!=0,x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if(x1==0,x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if(x1!=0 && x2==0,x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if(x2==0,x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if((x1!=0 && x2==0) || (x1==0 && x2!=0),x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if(x1==0 || x2==0,x3<1.49 || x3>1.51,x3>0.5)", // x
				"0.2 * if(x1!=0 && x2!=0,x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if((x1==0 && x2==0) || (x1!=0 && x2!=0),x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if(x2!=0,x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if(x1==0 || x2!=0,x3<1.49 || x3>1.51,x3>0.5)", // x
				"0.2 * if(x1!=0,x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * if(x1!=0 || x2==0,x3<1.49 || x3>1.51,x3>0.5)", // x
				"0.2 * if(x1!=0 || x2!=0,x3<1.49 || x3>1.51,x3>0.5)",
				"0.2 * (x3<1.49 || x3>1.51)"
		};
		final String[] functionNames = new String[] {
				"CONST0",
				"NOR",
				"NCIMPL",
				"NOT X1",
				"NIMPL",
				"NOT X2",
				"XOR",
				"NAND", // x
				"AND",
				"NXOR",
				"PROJ X2",
				"IMPL", // x
				"PROJ X1",
				"CIMPL", // x
				"OR",
				"CONST1",
		};

//		final String[] formulas = new String[] {
//			    "0.2 * (x3>0.4)",
//				"0.2 * if(x1==0 && x2==0, x3<0.6,x3>0.4)",
//				"0.2 * if(x1==0 && x2!=0, x3<0.6,x3>0.4)",
//				"0.2 * if(x1==0, x3<0.6,x3>0.4)",
//				"0.2 * if(x1!=0 && x2==0, x3<0.6,x3>0.4)",
//				"0.2 * if(x2==0, x3<0.6,x3>0.4)",
//				"0.2 * if(!(x1!=0 && x2!=0), x3<0.6,x3>0.4)",
//				"0.2 * if(x1!=0 && x2!=0, x3<0.6,x3>0.4)",
//				"0.2 * if(x2!=0, x3<0.6,x3>0.4)",
//				"0.2 * if(!(x1!=0 && x2==0), x3<0.6,x3>0.4)",
//				"0.2 * if(x1!=0, x3<0.6,x3>0.4)",
//				"0.2 * if(!(x1==0 && x2!=0), x3<0.6,x3>0.4)",
//				"0.2 * if(x1!=0 || x2!=0, x3<0.6,x3>0.4)",
//				"0.2 * (x3<0.6)",
//				"0.2 * if((x1!=0 && x2==0) || (x1==0 && x2!=0), x3<0.6,x3>0.4)",
//				"0.2 * if((x1==0 && x2==0) || (x1!=0 && x2!=0), x3<0.6,x3>0.4)"
//		};
//		final String[] functionNames = new String[] {
//				"CONST0",
//				"NOR",
//				"NCIMPL",
//				"NOT X1",
//				"NIMPL",
//				"NOT X2",
//				"NAND",
//				"AND",
//				"PROJ X2",
//				"IMPL",
//				"PROJ X1",
//				"CIMPL",
//				"OR",
//				"CONST1",
//				"XOR",
//				"NXOR",
//		};
		for (int i = 0; i < formulas.length; i++) {
			Expression<Double, Double> expression = new Expression<Double, Double>(formulas[i]);
			Map<String, Double> environment = new HashMap<String, Double>();
			JepExpressionEvaluator<Double, Double> jetExpressionEvaluator = new JepExpressionEvaluator<Double, Double>(expression);
		
			System.out.println(functionNames[i]);
			System.out.println("-----");
			double partialOutputSum = 0d;
			int k = 0;
			for (double x3 : x3Values) {
				System.out.println("Y = " + x3ValueNames[k]);
				environment.put("x3", x3);
				for (int j = 0; j < x1Values.length; j++) {
					environment.put("x1", x1Values[j]);
					environment.put("x2", x2Values[j]);

					Double value = jetExpressionEvaluator.evaluateX(environment);
					partialOutputSum+=value;
					System.out.println(x1Values[j] + "," + x2Values[j] + ":     " + value);
				}
				System.out.println();
				k++;
			}
//			if (Math.abs(partialOutputSum - 1.6d) > 0.01) {
//				throw new BndRuntimeException("Partial output binary function sum is not 1.6, but is " + partialOutputSum);
//			}
			System.out.println();
		}
	}
}