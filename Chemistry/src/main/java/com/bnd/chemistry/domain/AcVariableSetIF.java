package com.bnd.chemistry.domain;

import java.util.Collection;

public interface AcVariableSetIF<M extends AcVariable<? extends AcVariableSetIF>> extends AcVariableGroupIF<M> {

	public Integer getNextVarSequenceNum();

	public Collection<M> getOwnAndInheritedVariables();
}