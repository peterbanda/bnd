package com.bnd.function.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.bnd.core.EntryKeyLengthDescComparator;
import com.bnd.core.util.ObjectUtil;
import com.bnd.function.BndFunctionException;
import com.bnd.function.domain.Expression;
import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class FunctionUtility implements Serializable {

	private static final String VARIABLE_PLACEHOLDER = "x";

	public FunctionUtility() {
		// no-op
	}

	public <T> Map<String, T> convertVarIndexToStringPlaceholder(Map<Integer, T> indexVariableMap) {
		Map<String, T> substitutionMap = new HashMap<String, T>();
		for (Entry<Integer, T> setEntry : indexVariableMap.entrySet()) {
			substitutionMap.put(getVariablePlaceHolder(setEntry.getKey()), setEntry.getValue());
		}
		return substitutionMap;
	}

	public Map<String, String> convertVarIndecesToStringPlaceholders(Map<Integer, Integer> indexVariableMap) {
		Map<String, String> substitutionMap = new HashMap<String, String>();
		for (Entry<Integer, Integer> setEntry : indexVariableMap.entrySet()) {
			substitutionMap.put(getVariablePlaceHolder(setEntry.getKey()), getVariablePlaceHolder(setEntry.getValue()));
		}
		return substitutionMap;
	}

	public String getFormulaIndexPlaceholdersReplacedWithVariableNames(Function<?, ?> function, Map<Integer, String> variableIndexNameMap) {
		if (function == null) {
			return null;
		}
		return getFormulaIndexPlaceholdersReplacedWithVariableNames(function.toString(), function.getReferencedVariableIndeces(), variableIndexNameMap);
	}

	public String getFormulaWithReplacedIndexPlaceholders(Function<?, ?> function, Map<Integer, Integer> variableIndexMap) {
		if (function == null) {
			return null;
		}
		Map<Integer, Integer> variableIndexMapCopy = new HashMap<Integer, Integer>();
		for (Integer varIndex : function.getReferencedVariableIndeces()) {
			final Integer newVarIndex = variableIndexMap.get(varIndex);
			if (newVarIndex != null) {
				variableIndexMapCopy.put(varIndex, newVarIndex);
			}
		}

		Map<String, String> substitutionMap = convertVarIndecesToStringPlaceholders(variableIndexMapCopy);
		return getFormulaWithReplacedVariables(function.toString(), substitutionMap);
	}

	public String getFormulaIndexPlaceholdersReplacedWithVariableNames(
		String formula,
		Set<Integer> referencedVariableIndeces,
		Map<Integer, String> variableIndexNameMap
	) {
		validateVariableIndecesConsistency(variableIndexNameMap.keySet(), referencedVariableIndeces, formula);
		Map<String, String> substitutionMap = convertVarIndexToStringPlaceholder(variableIndexNameMap);
		return getFormulaWithReplacedVariables(formula, substitutionMap);
	}

	public String getFormulaVariableNamesReplacedWithIndexPlaceholders(
		String formula,
		Set<Integer> referencedVariableIndeces,
		Map<Integer, String> variableIndexNameMap
	) {
		if (formula == null || StringUtils.isBlank(formula)) {
			return formula;
		}
		validateVariableIndecesConsistency(variableIndexNameMap.keySet(), referencedVariableIndeces, formula);
		Map<String, String> substitutionMap = convertVarIndexToStringPlaceholder(variableIndexNameMap);
		Map<String, String> substitutionMapSwitched = ObjectUtil.switchKeyWithValue(substitutionMap);
		return getFormulaWithReplacedVariables(formula, substitutionMapSwitched);
	}

	private void validateVariableIndecesConsistency(
		Set<Integer> givenVariableIndeces,
		Set<Integer> referencedVariableIndeces,
		String formula) {
		if (// !referencedVariableIndeces.containsAll(givenVariableIndeces) ||
			!givenVariableIndeces.containsAll(referencedVariableIndeces)) {
			throw new BndFunctionException(
					"Variable indeces that need to be replaced '"
							+ givenVariableIndeces
							+ "' do not match referenced indeces '"
							+ referencedVariableIndeces + "' in function '"
							+ formula + "'.");
		}
	}

	private String getFormulaWithReplacedVariables(String formula, Map<String, String> substitutionMap) {
		if (formula == null) {
			return null;
		}
		List<Entry<String, String>> sortedEntries = new ArrayList<Entry<String, String>>();
		sortedEntries.addAll(substitutionMap.entrySet());
		Collections.sort(sortedEntries, new EntryKeyLengthDescComparator<String>());
		
		final int stringsNum = sortedEntries.size();
		String[] originalStrings = new String[stringsNum];
		String[] newStrings = new String[stringsNum];

		int i = 0;
		for (final Entry<String, String> entry : sortedEntries) {
			originalStrings[i] = entry.getKey();
			newStrings[i] = entry.getValue();
			i++;
		}
		return StringUtils.replaceEach(formula, originalStrings, newStrings);
	}

	public String getFunctionFormula(String functionTag, String[] inputs) {
		return getFunctionFormula(functionTag, Arrays.asList(inputs));
	}

	public String getFunctionFormula(String functionTag, Collection<String> inputs) {
		return getFunctionFormula(functionTag, StringUtils.join(inputs, ","));
	}

	public String getFunctionFormula(String functionTag, int variableIndex) {
		return getFunctionFormula(functionTag, getVariablePlaceHolder(variableIndex));
	}

	public <IN, OUT> void setExpressionFunctionFromString(String convertedFormula, FunctionHolder<IN, OUT> functionHolder) {
		Expression<IN, OUT> newExpression = getExpressionFunctionFromString(convertedFormula);
		if (newExpression == null || functionHolder.getFunction() == null) {
			functionHolder.setFunction(newExpression);
		} else {
			((Expression<IN, OUT>) functionHolder.getFunction()).setFormula(convertedFormula);				
		}
	}

	public <IN, OUT> Expression<IN, OUT> getExpressionFunctionFromString(String convertedFormula) {
		if (StringUtils.isBlank(convertedFormula)) {
			return null;
		}
		return new Expression<IN, OUT>(convertedFormula);
	}

	public String getFunctionFormula(String functionTag, String input) {
		StringBuilder sb = new StringBuilder();
		sb.append(functionTag);
		sb.append("(");
		sb.append(input);
		sb.append(")");
		return sb.toString();
	}

	public String getVariablePlaceHolder(int index) {
		return VARIABLE_PLACEHOLDER + index;
	}

	public Map<String, Double> createVariableNameValueMap(double[] values) {
		int count = values.length;
		Map<String, Double> environment = new HashMap<String, Double>();
		for (int index = 0; index < count; index++) {
			environment.put(getVariablePlaceHolder(index), values[index]);
		}
		return environment;
	}

	public static void main(String[] args) {
		final String[] originalStrings = new String[] {"x11", "x10", "x1", "x21"};
		final String[] newStrings = new String[] {"x51", "x8", "x11", "x2"};
		String originalString = "x11 + x10 + x1 + x21 * x11";
		String replacedString = StringUtils.replaceEach(originalString, originalStrings, newStrings);
		System.out.println("Original String: " + originalString);
		System.out.print("New String : " + replacedString);
		System.out.println("");
		System.out.println("original strings: " + Arrays.toString(originalStrings));
		System.out.println("new strings     : " + Arrays.toString(newStrings));
	}
}