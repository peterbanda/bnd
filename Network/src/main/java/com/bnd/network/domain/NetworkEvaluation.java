package com.bnd.network.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.um.User;
import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;

public class NetworkEvaluation implements FunctionHolder<Double[], Double>, Serializable {

	private Long id;
	private Long version = new Long(1);
	private String name;
	private Date timeCreated = new Date();
	private User createdBy;

	private Collection<NetworkEvaluationVariable> variables = new HashSet<NetworkEvaluationVariable>();
	private Integer varSequenceNum = 0;
	private Function<Double[], Double> evalFunction;
	private Set<NetworkEvaluationItem> evalItems = new HashSet<NetworkEvaluationItem>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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

	public Function<Double[], Double> getEvalFunction() {
		return evalFunction;
	}

	public void setEvalFunction(Function<Double[], Double> evalFunction) {
		this.evalFunction = evalFunction;
	}

	@Override
	public Function<Double[], Double> getFunction() {
		return evalFunction;
	}

	@Override
	public void setFunction(Function<Double[], Double> function) {
		this.evalFunction = function;
	}

	public Collection<NetworkEvaluationVariable> getVariables() {
		return variables;
	}

	public void setVariables(Collection<NetworkEvaluationVariable> variables) {
		this.variables = variables;
	}

	public void addVariable(NetworkEvaluationVariable variable) {
		if (variable.getVariableIndex() == null) {
			variable.setVariableIndex(getNextVarSequenceNum());
		}
		variables.add(variable);
		variable.setParentSet(this);
	}

	public void removeVariable(NetworkEvaluationVariable variable) {
		variables.remove(variable);
		variable.setParentSet(null);
	}

	public Integer getVarSequenceNum() {
		return varSequenceNum;
	}

	public void setVarSequenceNum(Integer varSequenceNum) {
		this.varSequenceNum = varSequenceNum;
	}

	public Integer getNextVarSequenceNum() {
		return varSequenceNum++;
	}

	public Set<NetworkEvaluationItem> getEvaluationItems() {
		return evalItems;
	}

	public void setEvaluationItems(Set<NetworkEvaluationItem> evalItems) {
		this.evalItems = evalItems;
	}

	public void addEvaluationItem(NetworkEvaluationItem evalItem) {
		evalItems.add(evalItem);
		evalItem.setEvaluation(this);
	}

	public void removeEvaluationItem(NetworkEvaluationItem evalItem) {
		evalItems.remove(evalItem);
		evalItem.setEvaluation(null);
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return name;
    }
}