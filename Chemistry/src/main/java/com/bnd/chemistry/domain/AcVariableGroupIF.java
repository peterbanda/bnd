package com.bnd.chemistry.domain;

import java.util.Collection;

public interface AcVariableGroupIF<M extends AcVariable<?>> {

	public Collection<M> getVariables();

	public void initVariables();

	public void setVariables(Collection<M> items);

	public void addVariable(M item);

	public void addVariables(Collection<M> items);

	public void removeVariable(M item);
}