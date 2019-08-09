package com.bnd.chemistry.domain;

import java.util.*;

import com.bnd.chemistry.BndChemistryException;
import com.bnd.chemistry.domain.AcVariable.AcVariableIndexComparator;
import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

@SuppressWarnings("unchecked")
public abstract class AcVariableSet<M extends AcVariable<? extends AcVariableSet>> extends TechnicalDomainObject implements AcVariableSetIF<M> {

	private String name;
	private Date createTime = new Date();
	private User createdBy;

	private Collection<M> variables = new HashSet<M>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public void initVariables() {
		this.variables = new HashSet<M>();
	}

	@Override
	public Collection<M> getVariables() {
		return variables;
	}

	public Collection<M> getVariablesSorted() {
		return sortVariablesByIndex(getVariables());
	}

	// Override if specific variable set supports inheritance
	public Collection<M> getOwnAndInheritedVariables() {
		return variables;
	}

	public Collection<M> getOwnAndInheritedVariablesSorted() {
		return sortVariablesByIndex(getOwnAndInheritedVariables());
	}

	@Override
	public void setVariables(Collection<M> variables) {
		this.variables = variables;
	}

	@Override
	public void addVariable(M variable) {
		if (variable.getParentSet() != null) {
			throw new BndChemistryException("AC variable (species/parameter) is already associated. Remove it first from the current set, before adding it to the new one.");
		}
		if (variable.getVariableIndex() == null) {
			variable.setVariableIndex(getNextVarSequenceNum());
		}
		if (variable.getSortOrder() == null) {
			variable.setSortOrder(variable.getVariableIndex());
		}
		variables.add(variable);
		variable.setParentSetUnsafe(this);
	}

	@Override
	public void addVariables(Collection<M> variables) {
		for (M variable : variables) {
			addVariable(variable);
		}
	}

	@Override
	public void removeVariable(M variable) {
		variables.remove(variable);
		variable.setParentSet(null);
		variable.setVariableIndex(null);
	}

	protected boolean containsVariable(M variable) {
		return variables.contains(variable);
	}

	public int getVariablesNumber() {
		return variables != null ? variables.size() : 0;
	}

	public boolean hasOwnVariables() {
		return variables != null && !variables.isEmpty();
	}

	private Collection<M> sortVariablesByIndex(Collection<M> variables) {
		List<M> sortedVariables = new ArrayList<M>();
		sortedVariables.addAll(variables);
		Collections.sort(sortedVariables, new AcVariableIndexComparator());
		return sortedVariables;
	}

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	if (name != null) {
    		sb.append(name);
    		sb.append("/");
    	}
    	sb.append(variables.size());
    	return sb.toString();
    }
}