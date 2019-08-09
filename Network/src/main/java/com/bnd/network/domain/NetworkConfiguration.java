package com.bnd.network.domain;

import java.util.List;

import com.bnd.core.domain.TechnicalDomainObject;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class NetworkConfiguration<T> extends TechnicalDomainObject {

	private Integer step;
	private List<T> states;

	public List<T> getStates() {
		return states;
	}

	public void setStates(List<T> states) {
		this.states = states;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (step != null) {
    		sb.append(step);
        	sb.append(" / ");
    	}
    	sb.append(states.size());
    	return sb.toString();
    }
}