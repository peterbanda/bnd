package com.bnd.function.business;

import java.util.Collection;
import java.util.Iterator;

import com.bnd.core.util.RandomUtil;
import com.bnd.function.BndFunctionException;
import com.bnd.function.domain.AggregateFunction;
import com.bnd.function.domain.Expression;
import com.bnd.function.domain.Function;
import com.bnd.function.domain.TransitionTable;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public class FunctionFactory {

	private FunctionUtility functionUtility = new FunctionUtility();

	public FunctionFactory() {
		// Nothing to do
	}

	public TransitionTable<Boolean, Boolean> createRandomBoolTransitionTable(int arity) {
		TransitionTable<Boolean, Boolean> transitionTable = createBoolTransitionTable();
		transitionTable.setArity(arity);
		final double numberOfRows = Math.pow(2, arity);
		for (int index = 0; index < numberOfRows; index++) {
			transitionTable.addOutput(RandomUtil.nextBoolean());
		}
		return transitionTable;
	}

	public TransitionTable<Boolean, Boolean> createBoolTransitionTable(Collection<Boolean> tableOutputs) {
		if (tableOutputs == null || tableOutputs.isEmpty()) {
			throw new BndFunctionException("Transition table can not be empty!");
		}
		TransitionTable<Boolean, Boolean> transitionTable = createBoolTransitionTable();
		Iterator<Boolean> tableOutputsIterator = tableOutputs.iterator();
		final int numberOfRows = tableOutputs.size();
		final int arity = (int) (Math.log(numberOfRows) / Math.log(2));
		transitionTable.setArity(arity);
		for (int index = 0; index < numberOfRows; index++) {
			transitionTable.addOutput(tableOutputsIterator.next());
		}
		return transitionTable;
	}

	private TransitionTable<Boolean, Boolean> createBoolTransitionTable() {
		TransitionTable<Boolean, Boolean> transitionTable = new TransitionTable<Boolean, Boolean>();
		transitionTable.setInputClazz(Boolean.class);
		transitionTable.setOutputClazz(Boolean.class);
		transitionTable.setRangeFrom(Boolean.FALSE);
		transitionTable.setRangeTo(Boolean.TRUE);
		return transitionTable;
	}

//	public Function<Double, Double> createUniformRandomFunction(double from, double to) {
//		return createFunctionFormula(RandomFunction.TAG, new Double[]{from, to});
//	}
//
//	public Function<Double, Double> createNormalRandomFunction(double mean, double variance) {
//		return createFunctionFormula(NormalRandomFunction.TAG, new Double[]{mean, variance});
//	}

	public Function<Double[], Double> createAggregateFunctionFormula(AggregateFunction aggregateFunction, int variableIndex) {
		final String formula = functionUtility.getFunctionFormula(aggregateFunction.getFunctionTag(), variableIndex);
		return new Expression<Double[], Double>(formula);
	}
	
	public Function<Double[], Double> createAggregateGreaterThanFunctionFormula(
		AggregateFunction aggregateFunction,
		int variableIndex,
		double thresholdValue
	) {
		final String functionTag = aggregateFunction.getFunctionTag();
		StringBuilder sb = new StringBuilder();
		sb.append(functionUtility.getFunctionFormula(functionTag, variableIndex));
		sb.append(" > ");
		sb.append(thresholdValue);
		return new Expression<Double[], Double>(sb.toString());
	}
	
	public Function<Double[], Double> createAggregateGreaterThanFunctionFormula(
		AggregateFunction aggregateFunction,
		int variableIndex1,
		int variableIndex2
	) {
		final String functionTag = aggregateFunction.getFunctionTag();
		StringBuilder sb = new StringBuilder();
		sb.append(functionUtility.getFunctionFormula(functionTag, variableIndex1));
		sb.append(" > ");
		sb.append(functionUtility.getFunctionFormula(functionTag, variableIndex2));
		return new Expression<Double[], Double>(sb.toString());
	}
}