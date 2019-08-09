package com.bnd.chemistry.domain;

import com.bnd.core.domain.TechnicalDomainObject;
import com.bnd.core.domain.ValueBound;
import com.bnd.core.domain.um.User;

public class AcRateConstantTypeBound extends TechnicalDomainObject {

	private User createdBy;
	private AcRateConstantType rateConstantType;
	private ValueBound<Double> bound = new ValueBound<Double>();

	public AcRateConstantType getRateConstantType() {
		return rateConstantType;
	}

	public void setRateConstantType(AcRateConstantType rateConstantType) {
		this.rateConstantType = rateConstantType;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public ValueBound<Double> getBound() {
		return bound;
	}

	public void setBound(ValueBound<Double> bound) {
		this.bound = bound;
	}

	public Double getFrom() {
		return bound.getFrom();
	}

	public Double getTo() {
		return bound.getTo();
	}

	public void setFrom(Double from) {
		bound.setFrom(from);
	}

	public void setTo(Double to) {
		bound.setTo(to);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(rateConstantType);
		sb.append("-");
		sb.append(bound.toString());
		return sb.toString();
	}
}