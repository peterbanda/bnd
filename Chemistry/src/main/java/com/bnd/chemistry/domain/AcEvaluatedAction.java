package com.bnd.chemistry.domain;

import java.util.HashSet;
import java.util.Set;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.util.ObjectUtil;

public class AcEvaluatedAction extends TechnicalDomainObject implements Comparable<AcEvaluatedAction> {

	private Integer applyTime;
	private AcInteraction action;
	private AcEvaluatedActionSeries evaluatedActionSeries;
	private Set<AcEvaluatedSpeciesAction> evaluatedSpeciesActions = new HashSet<AcEvaluatedSpeciesAction>();

	public Integer getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Integer applyTime) {
		this.applyTime = applyTime;
	}

	public AcInteraction getAction() {
		return action;
	}

	public void setAction(AcInteraction action) {
		this.action = action;
	}

	public Set<AcEvaluatedSpeciesAction> getEvaluatedSpeciesActions() {
		return evaluatedSpeciesActions;
	}

	protected void setEvaluatedSpeciesActions(Set<AcEvaluatedSpeciesAction> evaluatedSpeciesActions) {
		this.evaluatedSpeciesActions = evaluatedSpeciesActions;
	}

	public void addEvaluatedSpeciesAction(AcEvaluatedSpeciesAction evaluatedSpeciesAction) {
		evaluatedSpeciesActions.add(evaluatedSpeciesAction);
		evaluatedSpeciesAction.setEvaluatedAction(this);
	}

	public void removeEvaluatedSpeciesAction(AcEvaluatedSpeciesAction evaluatedSpeciesAction) {
		evaluatedSpeciesActions.remove(evaluatedSpeciesAction);
		evaluatedSpeciesAction.setEvaluatedAction(null);
	}

	public boolean isApplicable(double currentTime) {
		return ObjectUtil.isEqualOrLess(applyTime.doubleValue(), currentTime);
	}

	public AcEvaluatedActionSeries getEvaluatedActionSeries() {
		return evaluatedActionSeries;
	}

	protected void setEvaluatedActionSeries(AcEvaluatedActionSeries evaluatedActionSeries) {
		this.evaluatedActionSeries = evaluatedActionSeries;
	}

	/**
	 * @see edu.tlab.rbnpg.domain.TechnicalDomainObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!super.equals(object)) {
			return false;
		}
		if (object == null || !(object instanceof AcEvaluatedAction)) {
			return false;
		}
		AcEvaluatedAction evaluatedAction = (AcEvaluatedAction) object;
		return ObjectUtil.areObjectsEqual(getApplyTime(), evaluatedAction.getApplyTime())
			&& ObjectUtil.areObjectsEqual(getAction(), evaluatedAction.getAction());
	}

	@Override
	public int compareTo(AcEvaluatedAction evaluatedAction) {
		return ObjectUtil.compareObjects(applyTime, evaluatedAction.getApplyTime());
	}
}