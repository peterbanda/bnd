package com.bnd.network.domain;

import java.io.Serializable;

import com.bnd.core.util.ObjectUtil;

public class NetworkEvaluationVariable implements Serializable {

	private Long id;
	private Long version = new Long(1);
	private String label;

	private Integer variableIndex;
	private NetworkEvaluation parentSet;

	public NetworkEvaluationVariable() {
		// no-op
	}

	public NetworkEvaluationVariable(Long id) {
		this();
		this.id = id;
	}

	public NetworkEvaluationVariable(String label) {
		this();
		this.label = label;
	}

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

    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getVariableIndex() {
		return variableIndex;
	}

	public void setVariableIndex(Integer index) {
		this.variableIndex = index;
	}

	public NetworkEvaluation getParentSet() {
		return parentSet;
	}

	protected void setParentSet(NetworkEvaluation parentSet) {
		this.parentSet = parentSet;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		boolean result = super.equals(object);
		if (!result) {
			return false;
		}
		NetworkEvaluationVariable variable = (NetworkEvaluationVariable) object;
		return ObjectUtil.areObjectsEqual(getVariableIndex(), variable.getVariableIndex());
	}

	@Override
	public int hashCode() {
		return ObjectUtil.getHashCode(new Object[] {getId(), getVariableIndex()});	
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(label);
    	return sb.toString();
    }
}