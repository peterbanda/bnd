package com.bnd.chemistry.domain;

import java.util.Comparator;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;

public abstract class AcVariable<M extends AcVariableSetIF> extends TechnicalDomainObject {

	public static class AcVariableIndexComparator<T extends AcVariable<?>> implements Comparator<T> {

		@Override
		public int compare(T var1, T var2) {
			return ObjectUtil.compareObjects(var1.getVariableIndex(), var2.getVariableIndex());
		}
	}

	public static class AcVariableLabelComparator<T extends AcVariable<?>> implements Comparator<T> {

		@Override
		public int compare(T var1, T var2) {
			return ObjectUtil.compareObjects(var1.getLabel(), var2.getLabel());
		}
	}

	private String label;
	private Integer sortOrder;
	private Integer variableIndex;
	private M parentSet;

	public AcVariable() {
		super();
	}

	public AcVariable(Long id) {
		super(id);
	}

	public AcVariable(String label) {
		super();
		this.label = label;
	}

    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Integer getVariableIndex() {
		return variableIndex;
	}

	public void setVariableIndex(Integer index) {
		this.variableIndex = index;
	}

	public M getParentSet() {
		return parentSet;
	}

	protected void setParentSet(M parentSet) {
		this.parentSet = parentSet;
	}

	protected void setParentSetUnsafe(AcVariableSetIF<?> parentSet) {
		setParentSet((M) parentSet);
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
		AcVariable<?> variable = (AcVariable<?>) object;
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