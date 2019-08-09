package com.bnd.chemistry.domain;

import java.util.Date;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;

public class AcEvaluatedRun extends TechnicalDomainObject {

	private Integer steps;
	private Date createTime = new Date();
	private User createdBy;

	private Double[] evaluatedValues;
	private AcEvaluation evaluation;
	private AcTranslatedRun translatedRun;

	public void setSteps(Integer steps) {
		this.steps = steps;
	}

	public Integer getSteps() {
		if (steps == null) {
			refreshSteps();
		}
		return steps;
	}

	public Integer getCurrentSteps() {
		refreshSteps();
		return steps;
	}

	public void refreshSteps() {
		steps = evaluatedValues != null ? evaluatedValues.length : 0;
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

	public AcEvaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(AcEvaluation evaluation) {
		this.evaluation = evaluation;
	}

	public AcTranslatedRun getTranslatedRun() {
		return translatedRun;
	}

	public void setTranslatedRun(AcTranslatedRun translatedRun) {
		this.translatedRun = translatedRun;
	}

	public Double[] getEvaluatedValues() {
		return evaluatedValues;
	}

	public void setEvaluatedValues(Double[] evaluatedValues) {
		this.evaluatedValues = evaluatedValues;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getCreateTime());
    	return sb.toString();
    }
}