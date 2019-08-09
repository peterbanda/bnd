package com.bnd.function.business;

import java.util.Arrays;
import java.util.Map;

import com.bnd.function.BndFunctionException;

final class TransitionTableEvaluator<IN, OUT> extends AbstractFunctionEvaluator<IN, OUT> {

	private final Map<Iterable<IN>, OUT> table;
	private final int arity;

	public <T extends Iterable<IN>> TransitionTableEvaluator(
		final Map<Iterable<IN>, OUT> table,
		final int arity
	) {
		this.table = table;
		this.arity = arity;
	}

	@Override
	public OUT evaluate(IN[] inputs) {
		return evaluate(Arrays.asList(inputs));
	}

	@Override
	public OUT evaluate(Iterable<IN> inputs) {
		final OUT output = table.get(inputs);
		if (output == null) {
			throw new BndFunctionException("Given inputs do not match any row in transition table!");
		}
		return output;
	}

	@Override
	public Integer getArity() {
		return arity;
	}
}