package com.bnd.network.domain;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.function.domain.AbstractFunction;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class NodeFunction<T> extends TechnicalDomainObject {

	private Integer index;
	private AbstractFunction<T, T> function;
	private NetworkFunction<T> networkFunction;

    public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public AbstractFunction<T, T> getFunction() {
		return function;
	}

	public void setFunction(AbstractFunction<T, T> function) {
		this.function = function;
	}

	public NetworkFunction<T> getNetworkFunction() {
		return networkFunction;
	}

	public void setNetworkFunction(NetworkFunction<T> networkFunction) {
		this.networkFunction = networkFunction;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return "" + index + " / " + function.getArity();
    }
}