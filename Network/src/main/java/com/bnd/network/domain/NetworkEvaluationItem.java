package com.bnd.network.domain;

import java.io.Serializable;

import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;

public class NetworkEvaluationItem implements FunctionHolder<Double[], Double>, Serializable {

	private Long id;
	private Long version = 1l;

	private NetworkEvaluationVariable variable;
	private Function<Double[], Double> evalFunction;
	private NetworkEvaluation evaluation;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public NetworkEvaluationVariable getVariable() {
		return variable;
	}

	public void setVariable(NetworkEvaluationVariable variable) {
		this.variable = variable;
	}

	public String getLabel() {
		return variable != null ? variable.getLabel() : null;
	}

	public Function<Double[], Double> getEvalFunction() {
		return evalFunction;
	}

	public void setEvalFunction(Function<Double[], Double> evalFunction) {
		this.evalFunction = evalFunction;
	}

	public NetworkEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(NetworkEvaluation evaluation) {
		this.evaluation = evaluation;
	}

	@Override
	public Function<Double[], Double> getFunction() {
		return evalFunction;
	}

	@Override
	public void setFunction(Function<Double[], Double> evalFunction) {
		this.evalFunction = evalFunction;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getLabel());
    	sb.append(" / ");
        sb.append(evalFunction);
    	return sb.toString();
    }
}