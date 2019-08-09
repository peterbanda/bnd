package com.bnd.chemistry.domain;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

public class AcEvaluatedActionSeries extends TechnicalDomainObject {

	private Date createTime = new Date();
	private User createdBy;

	private AcInteractionSeries actionSeries;
	private Set<AcEvaluatedAction> evaluatedActions = new HashSet<AcEvaluatedAction>();

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

	public AcInteractionSeries getActionSeries() {
		return actionSeries;
	}

	public void setActionSeries(AcInteractionSeries actionSeries) {
		this.actionSeries = actionSeries;
	}

	public Set<AcEvaluatedAction> getEvaluatedActions() {
		return evaluatedActions;
	}

	protected void setEvaluatedActions(Set<AcEvaluatedAction> evaluatedActions) {
		this.evaluatedActions = evaluatedActions;
	}

	public void addEvaluatedAction(AcEvaluatedAction evaluatedAction) {
		evaluatedActions.add(evaluatedAction);
		evaluatedAction.setEvaluatedActionSeries(this);
	}

	public void addEvaluatedActions(Collection<AcEvaluatedAction> evaluatedActions) {
		for (AcEvaluatedAction evaluatedAction : evaluatedActions) {
			addEvaluatedAction(evaluatedAction);
		}
	}

	public void removeEvaluatedAction(AcEvaluatedAction evaluatedAction) {
		evaluatedActions.remove(evaluatedAction);
		evaluatedAction.setEvaluatedActionSeries(null);
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(actionSeries.toString() + " " + SimpleDateFormat.getTimeInstance().format(createTime));
    	return sb.toString();
    }
}