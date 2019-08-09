package com.bnd.function.domain;

import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.function.BndFunctionException;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public abstract class AbstractFunction<IN, OUT> extends TechnicalDomainObject implements Function<IN, OUT> {

	private Integer arity;
	private Class<IN> inputClazz;
	private Class<OUT> outputClazz;

	protected AbstractFunction() {
		super();
	}

	protected AbstractFunction(Class<IN> inputClazz, Class<OUT> outputClazz) {
		super();
		this.inputClazz = inputClazz;
		this.outputClazz = outputClazz;
	}

	@Override
	public Integer getArity() {
		return arity;
	}

	public void setArity(Integer arity) {
		this.arity = arity;
	}

	public Class<IN> getInputClazz() {
		return inputClazz;
	}

	public void setInputClazz(Class<IN> inputClazz) {
		this.inputClazz = inputClazz;
	}

	public Class<OUT> getOutputClazz() {
		return outputClazz;
	}

	public void setOutputClazz(Class<OUT> outputClazz) {
		this.outputClazz = outputClazz;
	}

	public String getInputClazzName() { 
		return inputClazz != null ? inputClazz.getName() : null;
	}

	public void setInputClazzName(String inputClazzName) {
		try {
			if (inputClazzName == null) {
				this.inputClazz = null;
				return;
			}
			this.inputClazz = (Class<IN>) Class.forName(inputClazzName);
		} catch (ClassNotFoundException e) {
			throw new BndFunctionException("Input type of function '" + inputClazzName + "' not recognized as valid Java class.", e);
		}
	}

	public String getOutputClazzName() {
		return outputClazz != null ? outputClazz.getName() : null;
	}

	public void setOutputClazzName(String outputClazzName) {
		try {
			if (outputClazzName == null) {
				this.outputClazz = null;
				return;
			}
			this.outputClazz = (Class<OUT>) Class.forName(outputClazzName);
		} catch (ClassNotFoundException e) {
			throw new BndFunctionException("Output type of function '" + outputClazzName + "' not recognized as valid Java class.", e);
		}
	}

	@Override
	public Set<Integer> getReferencedVariableIndeces() {
		Set<Integer> referencedVariableIndeces = new HashSet<Integer>();
		if (arity != null) {
			for (int variableId = 0; variableId < arity; variableId++) {
				referencedVariableIndeces.add(variableId);
			}
		}
		return referencedVariableIndeces;
	}

	@Override
	public Set<String> getReferencedVariables() {
		Set<String> referencedVariables = new HashSet<String>();
		for (Integer variableIndex : getReferencedVariableIndeces()) {
			referencedVariables.add("x" + variableIndex);
		}
		return referencedVariables;
	}
}