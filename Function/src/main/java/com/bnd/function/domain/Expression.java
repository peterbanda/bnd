package com.bnd.function.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class Expression<IN, OUT> extends AbstractFunction<IN, OUT> {

	private String formula;

	private Set<String> referencedVariables = new HashSet<String>();
	private Set<Integer> referencedVariableIndeces = new HashSet<Integer>();

	public Expression() {
		super();
	}

	public Expression(String formula) {
		this();
		setFormula(formula);
	}

	public Expression(String formula, Class<IN> inputClazz, Class<OUT> outputClazz) {
		super(inputClazz, outputClazz);
		setFormula(formula);
	}

	public void setFormula(String formula) {
		this.formula = formula;
		initReferencedVariables();	
	}

	public String getFormula() {
		return formula;
	}

	private void initReferencedVariables() {
		String[] variableChunks = formula.split("x");
		for (int index = 1; index < variableChunks.length; index++) {
			char[] characters = variableChunks[index].toCharArray();
			int charIndex = 0;
			for (; charIndex < characters.length; charIndex++) {
				char character = characters[charIndex];
				if (charIndex == 0) {
					if (character != '-' && !Character.isDigit(character)) {
						break;
					}
				} else if (!Character.isDigit(character)) {
					break;
				}
			}
			String variableIndex = variableChunks[index].substring(0, charIndex);
//			throw new RbnPGRuntimeException("Empty expression variable found in the formula '" + formula + "'");
			if (!variableIndex.isEmpty()) {
				referencedVariables.add("x" + variableIndex);
				referencedVariableIndeces.add(Integer.parseInt(variableIndex));
			}
		}
	}

	@Override
	public Set<String> getReferencedVariables() {
		return referencedVariables;
	}

	@Override
	public Set<Integer> getReferencedVariableIndeces() {
		return referencedVariableIndeces;
	}

	public static Expression<Double, Double> Double(String formula) {
		return new Expression<Double, Double>(formula);
	}
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(formula);
    	return sb.toString();
    }

    public static void main(String args[]) {
    	Expression<Double, Double> expression = new Expression<Double, Double>("x1^2 + x234 / (2 + x908)");
    	System.out.println(expression.getReferencedVariables());
    }
}