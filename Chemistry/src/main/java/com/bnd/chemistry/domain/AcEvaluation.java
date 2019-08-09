package com.bnd.chemistry.domain;

import java.util.Date;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.um.User;
import com.bnd.function.domain.Function;
import com.bnd.function.domain.FunctionHolder;

public class AcEvaluation extends TechnicalDomainObject implements FunctionHolder<Double, Double> {

	private String name;
	private Date createTime = new Date();
	private User createdBy;
	private AcTranslationSeries translationSeries;
	private Integer periodicTranslationsNumber;
	private Function<Double, Double> evalFunction;

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

	public AcTranslationSeries getTranslationSeries() {
		return translationSeries;
	}

	public void setTranslationSeries(AcTranslationSeries translationSeries) {
		this.translationSeries = translationSeries;
	}

	public Integer getPeriodicTranslationsNumber() {
		return periodicTranslationsNumber;
	}

	public void setPeriodicTranslationsNumber(Integer periodicTranslationsNumber) {
		this.periodicTranslationsNumber = periodicTranslationsNumber;
	}

	public Function<Double, Double> getEvalFunction() {
		return evalFunction;
	}

	public void setEvalFunction(Function<Double, Double> evalFunction) {
		this.evalFunction = evalFunction;
	}

	@Override
	public Function<Double, Double> getFunction() {
		return evalFunction;
	}

	@Override
	public void setFunction(Function<Double, Double> function) {
		this.evalFunction = function;
	}

	/**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
    	return name;
    }
}