package com.bnd.function.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.bnd.core.util.ConversionUtil;
import com.bnd.function.BndFunctionException;
import com.bnd.function.domain.Expression;

/**
 * @author © Peter Banda
 * @since 2012  
 */
abstract class ExpressionEvaluator<IN, OUT> extends AbstractFunctionEvaluator<IN, OUT> {

	protected final String formula;
	protected final int recognizedVariablesNum;
	protected final String[] recognizedVariableNames;
	protected final int[] associatedVariableConvertedIndeces;
	protected final FunctionUtility functionUtil = new FunctionUtility();

	public ExpressionEvaluator(Expression<IN, OUT> expression, String[] recognizedVariableNames) {
		this.formula = expression.getFormula();
		this.recognizedVariableNames = recognizedVariableNames;
		this.recognizedVariablesNum = recognizedVariableNames.length;
		this.associatedVariableConvertedIndeces = new int[0];
	}

	public ExpressionEvaluator(Expression<IN, OUT> expression, Map<Integer, Integer> recognizedVariableIndexConversionMap) {
		this.formula = expression.getFormula();
		this.recognizedVariableNames = createRecognizedVariableNames(expression.getReferencedVariableIndeces(), functionUtil);
		this.associatedVariableConvertedIndeces = createAssociatedVariableConvertedIndeces(expression.getReferencedVariableIndeces(), recognizedVariableIndexConversionMap);
		this.recognizedVariablesNum = recognizedVariableNames.length;
	}

	public ExpressionEvaluator(Expression<IN, OUT> expression) {
		this.formula = expression.getFormula();
		this.recognizedVariableNames = createRecognizedVariableNames(expression.getReferencedVariableIndeces(), functionUtil);
		this.associatedVariableConvertedIndeces = createAssociatedVariableConvertedIndeces(expression.getReferencedVariableIndeces());
		this.recognizedVariablesNum = recognizedVariableNames.length;
	}

	protected static String[] createRecognizedVariableNames(Collection<Integer> variableIndeces, FunctionUtility functionUtil) {
		final int variableIndecesNum = variableIndeces.size();
		String[] recognizedVariableNames = new String[variableIndecesNum];
		int i = 0;
		for (int variableIndex : variableIndeces) {
			recognizedVariableNames[i] = functionUtil.getVariablePlaceHolder(variableIndex);
			i++;
		}
		return recognizedVariableNames;
	}

	private static int[] createAssociatedVariableConvertedIndeces(Collection<Integer> variableIndeces) {
		return ConversionUtil.toSimpleType(variableIndeces.toArray(new Integer[0]));
	}

	private static int[] createAssociatedVariableConvertedIndeces(
		final Collection<Integer> variableIndeces,
		final Map<Integer, Integer> recognizedVariableIndexConversionMap) {
		int[] associatedVariableConvertedIndeces = new int[variableIndeces.size()];
		int i = 0;
		for (final Integer variableIndex : variableIndeces) {
			associatedVariableConvertedIndeces[i] = recognizedVariableIndexConversionMap.get(variableIndex);
			i++;
		}
		return associatedVariableConvertedIndeces;
	}

	@Override
	public OUT evaluate(final Iterable<IN> inputs) {
		Map<Integer, IN> environment = new HashMap<Integer, IN>();
		int variableId = 0;
		for (IN input : inputs) {
			if (input != null) {
				environment.put(variableId, input);
			}
			variableId++;
		}
		return evaluate(environment);
	}

	@Override
	public OUT evaluate(final Map<Integer, IN> environment) {
		final Map<String, IN> environmentWithVariables = functionUtil.convertVarIndexToStringPlaceholder(environment);
		return evaluateX(environmentWithVariables);
	}

	protected abstract OUT evaluateX(final Map<String, IN> environment);

	public abstract void validate() throws BndFunctionException;

	@Override
	public Integer getArity() {
		return recognizedVariablesNum;
	}
}