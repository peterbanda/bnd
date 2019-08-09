package com.bnd.function.domain;

/**
 * @author © Peter Banda
 * @since 2012  
 */
public interface FunctionHolder<IN, OUT> {

	public Function<IN, OUT> getFunction();

	public void setFunction(Function<IN, OUT> function);
}
