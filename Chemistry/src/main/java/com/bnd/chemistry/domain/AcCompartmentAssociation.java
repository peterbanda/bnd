package com.bnd.chemistry.domain;

import java.io.Serializable;

import com.bnd.core.util.ObjectUtil;

public class AcCompartmentAssociation implements Comparable<AcCompartmentAssociation>, Serializable {

	private Long id;
	private Long version = new Long(1);
	private Integer order;
	private AcCompartment parentCompartment;
	private AcCompartment subCompartment;

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

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public AcCompartment getParentCompartment() {
		return parentCompartment;
	}

	public void setParentCompartment(AcCompartment parentCompartment) {
		this.parentCompartment = parentCompartment;
	}

	public AcCompartment getSubCompartment() {
		return subCompartment;
	}

	public void setSubCompartment(AcCompartment subCompartment) {
		this.subCompartment = subCompartment;
	}

	@Override
	public int compareTo(AcCompartmentAssociation assoc) {
		return ObjectUtil.compareObjects(order, assoc.order);
	}
}
