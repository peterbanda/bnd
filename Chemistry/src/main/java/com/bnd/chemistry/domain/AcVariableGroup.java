package com.bnd.chemistry.domain;

import java.util.Collection;
import java.util.HashSet;

import com.bnd.core.domain.TechnicalDomainObject;

public class AcVariableGroup<M extends AcVariable<?>> extends TechnicalDomainObject implements AcVariableGroupIF<M> {

	private String label;
	private Collection<M> variables = new HashSet<M>();

	public AcVariableGroup(Collection<M> variables) {
		this();
		this.variables = variables;
	}

	public AcVariableGroup() {
		// no-op
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void addVariable(M variable) {
		variables.add(variable);
	}

	@Override
	public void addVariables(Collection<M> variables) {
		for (M variable : variables) {
			addVariable(variable);
		}
	}

	@Override
	public Collection<M> getVariables() {
		return variables;
	}

	@Override
	public void initVariables() {
		variables = new HashSet<M>();		
	}

	@Override
	public void removeVariable(M variable) {
		variables.remove(variable);
	}

	@Override
	public void setVariables(Collection<M> variables) {
		this.variables = variables;
	}
}