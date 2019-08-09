package com.bnd.chemistry.domain;

import java.util.ArrayList;
import java.util.List;

public class AcCompositeSpecies extends AcSpecies {

	private List<AcSpecies> components = new ArrayList<AcSpecies>();

	public AcCompositeSpecies() {
		super();
	}

	public AcCompositeSpecies(Long id) {
		super(id);
	}

	public AcCompositeSpecies(String label) {
		super(label);
	}

	public List<AcSpecies> getComponents() {
		return components;
	}

	public void setComponents(List<AcSpecies> components) {
		this.components = components;
	}

	public void addComponent(AcSpecies component) {
		components.add(component);
	}
}