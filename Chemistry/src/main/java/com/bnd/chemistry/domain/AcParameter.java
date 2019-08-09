package com.bnd.chemistry.domain;

import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;

public class AcParameter extends AcVariable<AcParameterSet> implements FunctionHolder<Double, Double> {

	private Function<Double, Double> evolFunction;

	public Function<Double, Double> getEvolFunction() {
		return evolFunction;
	}

	public void setEvolFunction(Function<Double, Double> evolFunction) {
		this.evolFunction = evolFunction;
	}

	@Override
	public Function<Double, Double> getFunction() {
		return evolFunction;
	}

	@Override
	public void setFunction(Function<Double, Double> function) {
		this.evolFunction = function;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(super.toString());
    	return sb.toString();
    }
}