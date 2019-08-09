package com.bnd.function.domain;

import java.util.Set;

import com.bnd.core.domain.KeyHolder;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public interface Function<IN, OUT> extends KeyHolder<Long> {

	public Integer getArity();

	public Set<Integer> getReferencedVariableIndeces();

	public Set<String> getReferencedVariables();
}